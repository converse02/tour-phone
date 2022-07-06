package li.dic.tourphone.domain.player

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.audio.AudioAttributes

class PlayerService : MediaBrowserServiceCompat() {
    private lateinit var notificationManager: NotificationManager
    private lateinit var currentPlayer: Player

    protected lateinit var mediaSession: MediaSessionCompat

    companion object {
        var isForegroundService = false
        const val NOW_PLAYING_NOTIFICATION_ID = 129232131
        const val NOW_PLAYING_CHANNEL_ID = "li.dic.tourphone.domain.player"
        lateinit var exoPlayer: ExoPlayer
        var onMediaItemChanged: ((MediaItem?) -> Unit)? = null

    }

    private var playerListener = playerListener()

    private val attributes = AudioAttributes.Builder()
        .setContentType(C.CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    override fun onCreate() {
        super.onCreate()

        exoPlayer = ExoPlayer
            .Builder(this)
            .setAudioAttributes(attributes, true)
            .build()

        val sessionActivityPendingIntent =
            packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                PendingIntent.getActivity(this, 0, sessionIntent, PendingIntent.FLAG_IMMUTABLE)
            }
        mediaSession = MediaSessionCompat(this, "MusicService")
            .apply {
                setSessionActivity(sessionActivityPendingIntent)
                isActive = true
            }

        notificationManager = NotificationManager(
            this,
            mediaSession.sessionToken
        )



        exoPlayer.addListener(playerListener)
        exoPlayer.playWhenReady = true

        currentPlayer = exoPlayer

        sessionToken = mediaSession.sessionToken

        notificationManager.showNotificationForPlayer(currentPlayer)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            exoPlayer.setForegroundMode(true)
        } catch (e: Exception) {
            onCreate()
        }
        return super.onStartCommand(intent, flags, startId)
    }


    private fun playerListener() = object : Player.Listener {
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
            onMediaItemChanged?.invoke(mediaItem)
        }

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING,
                Player.STATE_READY -> {
                    notificationManager.showNotificationForPlayer(exoPlayer)
                    if (playbackState == Player.STATE_READY) {

                        if (!playWhenReady) {
                            isForegroundService = false
                        }
                    }
                }
                else -> {
                    notificationManager.hideNotification()
                }
            }
        }
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        super.onTaskRemoved(rootIntent)
        currentPlayer.stop(true)
    }

    fun play() {
        try {
            exoPlayer.play()
        } catch (e: Exception) {
        }
    }

    fun addItem(item: MediaItem) {
        exoPlayer.addMediaItem(item)
    }

    fun clearItems() {
        exoPlayer.clearMediaItems()
    }

    override fun onDestroy() {
        mediaSession.run {
            isActive = false
            release()
        }

        exoPlayer.removeListener(playerListener)
        exoPlayer.release()
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? = null

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
    }
}