package li.dic.tourphone.data.storage

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import li.dic.tourphone.domain.auth.Login

class DataStorage(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val gson = Gson()

    var loginData: Login?
        set(value) = prefs.edit(commit = true)  { putString(PREF_LOGIN, gson.toJson(value)) }
        get() = gson.fromJson(
            prefs.getString(
                PREF_LOGIN,
                null
            ), Login::class.java
        )

    companion object {
        const val PREFS = "tour_phone_preferences"
        const val PREF_LOGIN = "pref_login_data"
    }
}