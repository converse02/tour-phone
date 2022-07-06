package li.dic.tourphone.presentation.tourist

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bridgefy.sdk.client.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import li.dic.tourphone.R
import li.dic.tourphone.domain.LocalExcursion
import li.dic.tourphone.domain.MessageType
import li.dic.tourphone.domain.player.PlayerService

class TouristExcursionActivity : AppCompatActivity() {

    companion object {
        var excursion: MutableLiveData<LocalExcursion> =
            MutableLiveData(LocalExcursion(0, "", "", "", 0))
        var day = MutableLiveData("")

        var rate = 0
        var channel = 0
        var format = 0

        lateinit var viewModel: TouristViewModel

        lateinit var track: AudioTrack

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.excursion_tourist_activity)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_ADVERTISE
                ),
                0
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                0
            )

        }
        setupBridgefy()


        try {
            val id: Int = intent.extras?.getInt("id") ?: 0
            val timeStart = intent.extras?.getString("timeStart").toString()
            val timeEnd = intent.extras?.getString("timeEnd").toString()
            val name = intent.extras?.getString("name").toString()
            val listeners: Int = intent.extras?.getInt("listeners") ?: 0

            excursion.value = LocalExcursion(id, timeStart, timeEnd, name, listeners)
            day.value = intent.extras?.getString("data").toString()

            viewModel = ViewModelProvider(this)[TouristViewModel::class.java]

            if (viewModel.messages.value == null) {
                viewModel.messages.value = mutableListOf()
            }
        } catch (e: Exception) {
            Toast.makeText(this, e.message.toString(), Toast.LENGTH_LONG).show()
        }
        startService(Intent(this, PlayerService::class.java))


    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Вы хотите покинуть экскурсию?")
            .setPositiveButton("Да") { p0, p1 ->
                startActivity(Intent(this, TouristActivity::class.java))
                p0.dismiss()
//                            player.stop()
                finish()
            }
            .setNegativeButton("Нет") { p0, p1 ->
                p0.dismiss()
            }
            .create()
            .show()
    }

    private fun getFormat() {
        val rates = intArrayOf(96000, 48000, 44100, 220050, 11025)
        val channels = intArrayOf(AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO)
        val encodes = intArrayOf(AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT)

        for (enc in encodes) {
            for (ch in channels) {
                for (r in rates) {
                    val t = AudioRecord.getMinBufferSize(r, ch, enc)
                    if (t != AudioRecord.ERROR && t != AudioRecord.ERROR_BAD_VALUE) {
                        rate = r
                        channel = ch
                        format = enc

                        return
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        setupBridgefy()
    }

    @SuppressLint("MissingPermission")
    private fun setupBridgefy() {
        if (isThingsDevice(this)) {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter.enable()
        }

        getFormat()

        Log.d("fsdfs", rate.toString() + " " + format + " " + channel)
        val buffSize = AudioRecord
            .getMinBufferSize(rate, AudioFormat.CHANNEL_OUT_STEREO, format)

        track = AudioTrack(
//            AudioManager.STREAM_VOICE_CALL,
            AudioManager.STREAM_MUSIC,
            rate,
            AudioFormat.CHANNEL_OUT_STEREO,
            format,
            buffSize * 10,
            AudioTrack.MODE_STREAM
//            AudioTrack.PERFORMANCE_MODE_LOW_LATENCY
        )
        track.setVolume(100F)

        track.play()

        val builder = Config.Builder()
            .setAutoConnect(true)
            .setEncryption(false)
            .build()

        val messageListener = object : MessageListener() {
            override fun onMessageReceived(message: Message?) {
                Log.d("mmmmm_received", message.toString())

                val data = message?.data
                Log.d("mmmmm_received", data.toString())
            }

            override fun onBroadcastMessageReceived(message: Message?) {
                val content = message?.content

                if (content != null) {
                    Toast.makeText(
                        this@TouristExcursionActivity,
                        content.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("tertrfgd", content.toString())
                    if (content["type"].toString() == "msg") {
                        val name = content["name"].toString()
                        val text = content["message"].toString()

                        val msg = li.dic.tourphone.domain.Message(
                            name = name,
                            text = text,
                            type = MessageType.INCOMING
                        )


                        val list = viewModel.messages.value

                        list?.add(msg)

                        viewModel.messages.postValue(list)
//                        adapter.submitList(list)
//                        adapter.notifyDataSetChanged()
                    } else {

                        try {
                            val shortArray = (content["foo"] as String)
                                .split(", ")
                                .map { it.toShort() }
                                .toShortArray()
//                            shortArray.forEach {
//                                val s = it.toString().toShort()
//                                arr.add(s)
//                            }
                            //Log./d("/fsdfsdfsd", (shortArray.size + 1).toString())
                            Toast.makeText(
                                this@TouristExcursionActivity,
                                shortArray.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            track.write(shortArray, 0, shortArray.size)
//                            track.play()

                        } catch (e: Exception) {
                            Toast.makeText(
                                this@TouristExcursionActivity,
                                "${e.message} errror voice",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
            }

            override fun onMessageSent(messageId: String?) {
                Log.d("mmmmm_sent", messageId.toString())
            }
        }
        val stateListener = object : StateListener() {
            override fun onDeviceDetected(device: Device?) {
                Log.d("rwsersdrsd", "detected $device")
            }

            override fun onDeviceConnected(device: Device?, session: Session?) {
                Toast.makeText(this@TouristExcursionActivity, "Connected", Toast.LENGTH_LONG).show()
            }

            override fun onDeviceUnavailable(device: Device?) {
                Log.d("rwsersdrsd", "unavailable $device")
            }

        }

        Bridgefy.start(
            messageListener,
            stateListener,
            builder
        )
    }

    private fun isThingsDevice(context: Context): Boolean {
        val pm = context.packageManager
        return pm.hasSystemFeature("android.hardware.type.embedded")
    }
}