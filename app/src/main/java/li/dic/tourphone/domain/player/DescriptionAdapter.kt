package li.dic.tourphone.domain.player

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.support.v4.media.session.MediaControllerCompat
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import li.dic.tourphone.domain.player.NotificationManager.Companion.NOTIFICATION_LARGE_ICON_SIZE
import kotlinx.coroutines.*
import li.dic.tourphone.R
import java.lang.Exception

class DescriptionAdapter(
    private val controller: MediaControllerCompat,
    private val context: Context
) :
    PlayerNotificationManager.MediaDescriptionAdapter {

    var currentIconUri: Uri? = null
    var currentBitmap: Bitmap? = null

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    private val glideOptions = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.DATA)

    override fun createCurrentContentIntent(player: Player): PendingIntent? =
        controller.sessionActivity

    override fun getCurrentContentText(player: Player) =
        try {
            player.mediaMetadata.artist.toString()
        } catch (e: Exception) {
            "text"
        }

    override fun getCurrentContentTitle(player: Player) =
        try {
            player.mediaMetadata.title.toString()
        } catch (e: Exception) {
            "title"
        }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        try {
            return AppCompatResources.getDrawable(context, R.drawable.ic_logo_auth)?.toBitmap()
        } catch (e: Exception) {
            return null
        }
    }

    private suspend fun resolveUriAsBitmap(uri: Uri): Bitmap? {
        return withContext(Dispatchers.IO) {
            Glide.with(context).applyDefaultRequestOptions(glideOptions)
                .asBitmap()
                .load(uri)
                .submit(
                    NOTIFICATION_LARGE_ICON_SIZE,
                    NOTIFICATION_LARGE_ICON_SIZE
                )
                .get()
        }
    }
}