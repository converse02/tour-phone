package li.dic.tourphone.presentation.tourist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import li.dic.tourphone.R
import li.dic.tourphone.databinding.LayoutDialogEnterWithCodeBinding
import li.dic.tourphone.databinding.TouristActivityBinding
import li.dic.tourphone.domain.LocalExcursion
import java.io.IOException

class TouristActivity : AppCompatActivity() {
    private lateinit var binding: TouristActivityBinding

    private lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""

    private var dialogEnterFromQr: AlertDialog? = null
    private var dialogEnterFromCode: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TouristActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupDialog()
        startScan()

        binding.btnMenu.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tourphone.ru/"))
            startActivity(browserIntent)
        }

        binding.btnLastExcursion.setOnClickListener {
            stopScan()
            navigate(
                LocalExcursion(
                    12,
                    "10:00",
                    "11:00",
                    "Прошлая экскурсия"
                ),
                "Чт 26 сентября"
            )

        }

        binding.btnEnterCode.setOnClickListener { dialogEnterFromCode?.show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::cameraSource.isInitialized) cameraSource.stop()
    }

    private fun checkPermission(): Boolean {
        return ContextCompat
            .checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askForCameraPermission() {
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                startActivity(Intent(this, TouristActivity::class.java))
                finish()
            } else {
                permissionsNotGranted()
            }
        }
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun permissionsNotGranted() {
        AlertDialog.Builder(this).setTitle(getString(R.string.error_permission))
            .setMessage(getString(R.string.need_permission))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.get_permission)) { _, _ -> askForCameraPermission() }
            .setOnCancelListener {
                it.dismiss()
                stopScan()
            }
            .show()
    }

    private fun stopScan() {
        binding.camera.visibility = View.GONE
        binding.barcodeLine.clearAnimation()
    }

    private fun startScan() {
        if (!checkPermission())
            askForCameraPermission()
        else {
            binding.camera.visibility = View.VISIBLE
            setupControls()
        }
    }

    private fun setupControls() {
        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this, R.anim.scanner_animation)
        binding.barcodeLine.startAnimation(aniSlide)

        barcodeDetector =
            BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build()

        cameraSource = CameraSource.Builder(this, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true)
            .build()

        binding.cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (checkPermission()) {
                    try {
                        cameraSource.start(holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                if (checkPermission()) {
                    try {
                        cameraSource.start(holder)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {}

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue
                    runOnUiThread {
                        if (!dialogEnterFromQr?.isShowing!!)
                            dialogEnterFromQr?.show()
                    }
                }
            }
        })
    }

    private fun setupDialog() {
        val dialogBinding = LayoutDialogEnterWithCodeBinding.inflate(layoutInflater)

        dialogEnterFromCode = MaterialAlertDialogBuilder(
            this,
            R.style.MyThemeOverlay_MaterialComponents_MaterialAlertDialog
        ).setView(dialogBinding.root).create()


        dialogEnterFromQr = MaterialAlertDialogBuilder(this)
            .setTitle("Войти в экскурсию?")
            .setMessage(scannedValue)
            .setPositiveButton("Да") { p0, p1 ->
                stopScan()

                navigate(
                    LocalExcursion(
                        13,
                        "11:00",
                        "12:00",
                        "Экскурсия из qr кода"
                    ),
                    "Чт 26 сентября"
                )

                p0.dismiss()
            }
            .setNegativeButton("Нет") { p0, p1 ->
                p0.dismiss()
            }
            .create()


        val btn = dialogBinding.btnSend
        val tl = dialogBinding.tilCode
        val et = dialogBinding.etCode


        btn.setOnClickListener {
            val code = et.text.toString()

            when {
                code.isEmpty() -> {
                    tl.error = getString(R.string.enter_code)
                }
                code != "1234" -> {
                    tl.error = getString(R.string.error_code)
                }
                else -> {
                    navigate(
                        LocalExcursion(
                            14,
                            "11:00",
                            "12:00",
                            "Экскурсия из кода"
                        ),
                        "Чт 26 сентября"
                    )

                    dialogEnterFromCode!!.dismiss()
                }
            }


        }
        et.addTextChangedListener { tl.isErrorEnabled = false }
    }

    private fun navigate(exc: LocalExcursion, day: String) {
        stopScan()
        dialogEnterFromCode?.dismiss()
        dialogEnterFromQr?.dismiss()

        val intent = Intent(this, TouristExcursionActivity::class.java)
        val bundle = Bundle()

        bundle.putInt("id", exc.id)
        bundle.putString("timeStart", exc.timeStart)
        bundle.putString("timeEnd", exc.timeEnd)
        bundle.putString("name", exc.name)
        bundle.putInt("listeners", exc.listeners)
        bundle.putString("data", day)

        intent.putExtras(bundle)

        startActivity(intent)
    }

}