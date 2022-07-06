package li.dic.tourphone.domain.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import li.dic.tourphone.R
import li.dic.tourphone.domain.player.PlayerService.Companion.NOW_PLAYING_CHANNEL_ID
import li.dic.tourphone.domain.player.PlayerService.Companion.NOW_PLAYING_NOTIFICATION_ID

class NotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token
) {
    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaControllerCompat(context, sessionToken)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "name"
            val description = "description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOW_PLAYING_CHANNEL_ID, name, importance)
            channel.description = description
            val notificationManager: NotificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = PlayerNotificationManager.Builder(
            context,
            NOW_PLAYING_NOTIFICATION_ID,
            NOW_PLAYING_CHANNEL_ID
        )
        with(builder) {
            setMediaDescriptionAdapter(DescriptionAdapter(mediaController, context))
        }

        notificationManager = builder.build()
        notificationManager.setMediaSessionToken(sessionToken)
        notificationManager.setSmallIcon(R.drawable.ic_logo_auth)
        notificationManager.setUseRewindAction(false)
        notificationManager.setUseFastForwardAction(false)
        notificationManager.setUsePreviousAction(false)
        notificationManager.setUseNextAction(false)
        notificationManager.setUsePlayPauseActions(false)
    }

    fun hideNotification() {
        notificationManager.setPlayer(null)
    }

    fun showNotificationForPlayer(player: Player) {
        notificationManager.setPlayer(player)
    }

    companion object {
        const val NOTIFICATION_LARGE_ICON_SIZE = 144
    }
}


