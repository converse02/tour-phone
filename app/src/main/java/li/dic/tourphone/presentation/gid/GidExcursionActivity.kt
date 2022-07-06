package li.dic.tourphone.presentation.gid

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bridgefy.sdk.client.*
import com.bridgefy.sdk.framework.exceptions.MessageException
import li.dic.tourphone.R
import li.dic.tourphone.domain.LocalExcursion
import li.dic.tourphone.domain.MessageType
import li.dic.tourphone.domain.mic.RecordService
import li.dic.tourphone.presentation.tourist.TouristExcursionActivity
import li.dic.tourphone.presentation.tourist.TouristViewModel

class GidExcursionActivity : AppCompatActivity() {

    companion object {
        var excursion: MutableLiveData<LocalExcursion> =
            MutableLiveData(LocalExcursion(0, "", "", "", 0))
        var day = MutableLiveData("")
        lateinit var viewModel : GidViewModel
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.excursion_gid_activity)

        val id: Int = intent.extras?.getInt("id") ?: 0
        val timeStart = intent.extras?.getString("timeStart").toString()
        val timeEnd = intent.extras?.getString("timeEnd").toString()
        val name = intent.extras?.getString("name").toString()
        val listeners: Int = intent.extras?.getInt("listeners") ?: 0

        viewModel = ViewModelProvider(this)[GidViewModel::class.java]
        startService(Intent(this, RecordService::class.java))

        if (viewModel.messages.value == null) {
            viewModel.messages.value = mutableListOf()
        }
        excursion.value = LocalExcursion(id, timeStart, timeEnd, name, listeners)
        day.value = intent.extras?.getString("data").toString()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE),
                0
            )
        }else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )

        }


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
                    Toast.makeText(this@GidExcursionActivity, content.toString(), Toast.LENGTH_LONG).show()
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
                Toast.makeText(this@GidExcursionActivity, "Connected", Toast.LENGTH_LONG).show()
            }

            override fun onDeviceUnavailable(device: Device?) {
                Log.d("rwsersdrsd", "unavailable $device")}

        }

        Bridgefy.start(
            messageListener,
            stateListener,
            builder
        )


        startService(Intent(this, RecordService::class.java))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(Intent(this, RecordService::class.java))
    }
}