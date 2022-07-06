package li.dic.tourphone.data

import android.content.Context
import android.content.res.Resources
import androidx.multidex.MultiDexApplication
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import li.dic.tourphone.data.storage.DataStorage

class App : MultiDexApplication() {
    lateinit var queue: RequestQueue
    lateinit var storage: DataStorage

    override fun onCreate() {
        super.onCreate()
        queue = Volley.newRequestQueue(this)
        storage = DataStorage(this)
    }
}

fun Context.app(): App = applicationContext as App
fun Context.storage() = app().storage

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()