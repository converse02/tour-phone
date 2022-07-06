package li.dic.tourphone.presentation.gid

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import li.dic.tourphone.R
import li.dic.tourphone.data.extensions.getExcursions
import li.dic.tourphone.data.extensions.showToast
import li.dic.tourphone.databinding.GidActivityBinding
import li.dic.tourphone.domain.LocalExcursion
import li.dic.tourphone.domain.LocalExcursions
import li.dic.tourphone.domain.auth.Excursion
import li.dic.tourphone.domain.mic.RecordService
import li.dic.tourphone.presentation.gid.recyclerview.ExcursionsAdapter
import java.text.SimpleDateFormat
import java.util.*


class GidActivity : AppCompatActivity() {

    private lateinit var binding: GidActivityBinding
    private lateinit var rvExcursions: RecyclerView

    companion object {
        lateinit var notification: Notification
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GidActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.rvExcursions.visibility = View.INVISIBLE

        with(binding.swipeRefreshLayout) {
            setColorSchemeResources(R.color.main)
            setOnRefreshListener {
                updateData()
                isRefreshing = false
            }
        }

        if (checkPermission()) {
            startForegroundService(Intent(this, RecordService::class.java))
        } else {
            askPermission()
        }

        binding.btnMenu.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tourphone.ru/"))
            startActivity(browserIntent)
        }
    }

    private fun askPermission() {
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
            } else {
                askPermission()

            }
        }
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    override fun onResume() {
        super.onResume()

        updateData()
    }


    private fun updateData() {
        binding.rvExcursions.visibility = View.INVISIBLE

        getExcursions {
            it?.run {
                showExcursions(this)
            }
        }
    }

    private fun showExcursions(exc: Array<Excursion>?) {
        if (exc == null) showToast(getString(R.string.error_update))
        else {
            val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                resources.configuration.locales.get(0)
            } else {
                resources.configuration.locale
            }
            val mainDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale)
            val displayDateFormat = SimpleDateFormat("EE dd MMMM", locale)
            val hourFormat = SimpleDateFormat("HH:mm", locale)
            var prevDate: String? = null
            var lDate: Date? = null
            val list: MutableList<LocalExcursions> = mutableListOf()
            var curList: MutableList<LocalExcursion> = mutableListOf()

            for (excursion in exc) {
                val date: Date? = mainDateFormat.parse(excursion.start.trim())
                lDate = date
                val nowDate = displayDateFormat.format(date).replaceFirstChar { it.titlecase() }
                if (prevDate == null) {
                    prevDate = nowDate
                }
                if (prevDate == nowDate) {
                    curList.add(
                        LocalExcursion(
                            excursion.id,
                            hourFormat.format(date),
                            "11:00",
                            excursion.name
                        )
                    )
                } else {
                    curList.sortBy { hourFormat.parse(it.timeStart) }
                    list.add(LocalExcursions(date, prevDate, curList))
                    curList = mutableListOf(
                        LocalExcursion(
                            excursion.id,
                            hourFormat.format(date),
                            "11:00",
                            excursion.name
                        )
                    )
                    prevDate = nowDate
                }
            }
            prevDate?.let {
                curList.sortBy { hourFormat.parse(it.timeStart) }
                list.add(LocalExcursions(lDate, prevDate, curList))
            }
            list.sortByDescending { it.date }

            rvExcursions = binding.rvExcursions
            val adapter = ExcursionsAdapter(list)
            rvExcursions.adapter = adapter
            rvExcursions.isNestedScrollingEnabled = false
            rvExcursions.layoutManager = LinearLayoutManager(this)

            adapter.onExcursionClickListener = { p1, p2 ->
                navigate(p1, p2)
            }
        }

        rvExcursions.visibility = View.VISIBLE
    }

    private fun checkPermission() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat
                .checkSelfPermission(
                    this, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat
                        .checkSelfPermission(
                            this, Manifest.permission.BLUETOOTH_CONNECT
                        ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat
                .checkSelfPermission(
                    this, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
        }

    private fun navigate(exc: LocalExcursion, day: String) {
        val intent = Intent(this, GidExcursionActivity::class.java).apply {
            putExtras(
                bundleOf(
                    "id" to exc.id,
                    "timeStart" to exc.timeStart,
                    "timeEnd" to exc.timeEnd,
                    "name" to exc.name,
                    "listeners" to exc.listeners,
                    "data" to day,
                )
            )
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, RecordService::class.java))
    }
}