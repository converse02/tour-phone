package li.dic.tourphone.domain.mic

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.media.*
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.bridgefy.sdk.client.Bridgefy
import com.bridgefy.sdk.client.BridgefyUtils.isThingsDevice
import com.bridgefy.sdk.client.Message
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RecordService : Service() {

    companion object {
        var thread: (() -> Unit?)? = null
        var isRunning = false

        private var buffSize: Int = 0
        private var record: AudioRecord? = null
        private var track: AudioTrack? = null
        private var rate = 0
        private var channel = 0
        private var format = 0
        private var count = 0
        private var buffers = Array(32) { ShortArray(buffSize shr 1) }


        fun start() {
            GlobalScope.launch {
                thread?.invoke()
            }
        }
    }



    override fun onBind(p0: Intent?): IBinder? = null

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        prepareMic()

        if (isThingsDevice(this)) {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            bluetoothAdapter.enable()
        }
        val manager = (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)

        manager.createNotificationChannel(
            NotificationChannel(
                "MyChannelId",
                "My Foreground Service",
                NotificationManager.IMPORTANCE_LOW
            )
        )

        val notification = Notification.Builder(this, "MyChannelId")
            .setCategory(Notification.CATEGORY_SERVICE)
            .setChannelId("MyChannelId")
            .build()

        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        stopForeground(true)
        Bridgefy.stop()
    }

    @SuppressLint("MissingPermission")
    private fun prepareMic() {
        getFormat()

        buffSize = AudioRecord
            .getMinBufferSize(rate, channel, format)
        record = AudioRecord(
            MediaRecorder
                .AudioSource.MIC, rate, channel, format, buffSize * 10
        )

        buffers = Array(32) { ShortArray(buffSize shr 1) }

        track = AudioTrack(
            AudioManager.STREAM_VOICE_CALL,
//            AudioManager.STREAM_MUSIC,
            rate,
            AudioFormat.CHANNEL_OUT_STEREO,
            format,
            buffSize * 10,
            AudioTrack.MODE_STREAM
//            AudioTrack.PERFORMANCE_MODE_LOW_LATENCY
        )
        track!!.setVolume(100F)

        thread = getThread()

    }

    private fun getFormat() {
        val rates = intArrayOf(96000, 48000, 44100, 220050, 11025)
        val chans = intArrayOf(AudioFormat.CHANNEL_IN_STEREO, AudioFormat.CHANNEL_IN_MONO)
        val encs = intArrayOf(AudioFormat.ENCODING_PCM_16BIT, AudioFormat.ENCODING_PCM_8BIT)

        for (enc in encs) {
            for (ch in chans) {
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

    private fun getThread(): () -> Unit = {
        if (record != null && track != null) {
            record!!.startRecording()
            track!!.play()
            while (isRunning) {
                record!!.read(buffers[count], 0, buffers[count].size)
                track!!.write(buffers[count], 0, buffers[count].size)

//                Log.d("TAAG", buffers[count].toString())
                val data: HashMap<String, Any> = HashMap()

                data["type"] = "voice"
                data["foo"] = buffers[count].joinToString(", ")

                val msg = Message.Builder().setContent(data).build()
                try{
                    Bridgefy.sendBroadcastMessage(msg)
//                Bridgefy.sendMessage(msg)
                    Log.d("ewrwer", "send")
                    Log.d("ewrwer",  buffers[count].joinToString(", ").toString())
                }catch (e: Exception) {
                    Log.d("ewrwer", "errr")}
                count += 1
                count %= 32

            }
            record!!.stop()
        }
    }
}