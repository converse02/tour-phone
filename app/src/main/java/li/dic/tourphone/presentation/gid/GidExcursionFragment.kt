package li.dic.tourphone.presentation.gid

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import li.dic.tourphone.R
import li.dic.tourphone.data.extensions.getExcursion
import li.dic.tourphone.data.extensions.showToast
import li.dic.tourphone.data.px
import li.dic.tourphone.databinding.ExcursionFragmentBinding
import li.dic.tourphone.domain.LocalExcursion
import li.dic.tourphone.domain.auth.Excursion
import li.dic.tourphone.domain.mic.RecordService


class GidExcursionFragment : Fragment() {

    private lateinit var binding: ExcursionFragmentBinding

    private var excursion: LocalExcursion = LocalExcursion(0, "", "", "", 0)
    private var day: String = ""

    private var isRunning = false

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)

        binding = ExcursionFragmentBinding.inflate(inflater, container, false)

        GidExcursionActivity.day.observeForever { it ->
            day = it

            displayExcursion()
        }

        GidExcursionActivity.excursion.observeForever { it ->
            excursion = it

            displayExcursion()
        }

        if (checkPermission()) {
            RecordService.isRunning = true
            RecordService.start()
            isRunning = true
        } else {
            Toast.makeText(
                requireContext(),
                "Разрешение на микрофон не выдано - аудиопоток заблокирован",
                Toast.LENGTH_LONG
            ).show()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.chat.setOnClickListener {
            findNavController().navigate(R.id.GidChatFragment)
        }

        with(binding.swipeRefreshLayout) {
            setColorSchemeResources(R.color.main)
            setOnRefreshListener {
                updateData()
            }
        }

        return binding.root
    }


    override fun onResume() {
        super.onResume()

        binding.swipeRefreshLayout.isRefreshing = true
        updateData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isRunning) {
            RecordService.isRunning = false
        }
    }

    private fun updateData() {
        context?.getExcursion(excursion.id) {
            it?.run {
                showExcursion(this)
            }
        }
    }

    private fun showExcursion(exc: Excursion?) {
        if (exc == null) showToast(getString(R.string.error_update))
        else {
            excursion.fromApiExcursion(
                exc, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    resources.configuration.locales.get(0)
                } else {
                    resources.configuration.locale
                }
            )
            displayExcursion()
        }

        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun displayExcursion() {
        binding.tvDay.text = day
        binding.tvName.text = excursion.name
        binding.tvTime.text = "${excursion.timeStart} - ${excursion.timeEnd}"
        binding.listenersCount.text = excursion.listeners.toString()
        binding.qrImage.setImageBitmap(createBitmap("https://tourphone.ru/?excursionId=${excursion.id}"))
    }

    private fun createBitmap(link: String): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(link, BarcodeFormat.QR_CODE, 150.px, 150.px)

            val w = bitMatrix.width
            val h = bitMatrix.height
            val pixels = IntArray(w * h)
            for (y in 0 until h) {
                for (x in 0 until w) {
                    pixels[y * w + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                .apply { setPixels(pixels, 0, w, 0, 0, w, h) }
        } catch (e: WriterException) {
            null
        }
    }


    private fun checkPermission() =
        ContextCompat
            .checkSelfPermission(
                requireContext(), Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

    private fun isThingsDevice(context: Context): Boolean {
        val pm = context.packageManager
        return pm.hasSystemFeature("android.hardware.type.embedded")
    }
}