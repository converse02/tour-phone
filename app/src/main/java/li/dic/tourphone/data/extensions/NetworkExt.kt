package li.dic.tourphone.data.extensions

import android.content.Context
import com.android.volley.Request
import li.dic.tourphone.R
import li.dic.tourphone.data.app
import li.dic.tourphone.domain.auth.Excursion
import li.dic.tourphone.domain.auth.Login
import li.dic.tourphone.data.storage
import li.dic.tourphone.data.volley.GsonRequest
import org.json.JSONObject


fun Context.login(
    callback: (loginData: Login?) -> Unit,
    login: String?,
    password: String?,
) {
    val url = getString(R.string.api_url) + "auth/local"

    val body = JSONObject()
    body.put("identifier", login)
    body.put("password", password)

    val loginRequest =
        GsonRequest(
            url, Login::class.java,
            listener = {
                callback.invoke(it)
            },
            errorListener = {
                it.printStackTrace()
                callback.invoke(null)
            },
            rMethod = Request.Method.POST,
            body = body,
        )
    app().queue.add(loginRequest)
}

fun Context.register(
    callback: (loginData: Login?) -> Unit,
    login: String?,
    email: String?,
    phone: String?,
    fio: String?,
    password: String?,
) {
    val url = getString(R.string.api_url) + "auth/local/register"

    val body = JSONObject()
    body.put("username", login)
    body.put("email", email)
    body.put("FIO", fio)
    body.put("Phone", phone)
    body.put("password", password)

    val registerRequest =
        GsonRequest(
            url, Login::class.java,
            listener = {
                callback.invoke(it)
            },
            errorListener = {
                it.printStackTrace()
                callback.invoke(null)
            },
            rMethod = Request.Method.POST,
            body = body,
        )
    app().queue.add(registerRequest)
}

fun Context.getExcursions(
    callback: (excursions: Array<Excursion>?) -> Unit
) {
    val userId = storage().loginData?.user?.id
    val url = getString(R.string.api_url) + "excursions?guide.id=$userId"

    val excursionsRequest =
        GsonRequest(url, Array<Excursion>::class.java,
            headers = mutableMapOf("Authorization" to "Bearer ${storage().loginData?.jwt}"),
            listener = {
                callback.invoke(it)
            }, errorListener = {
                it.printStackTrace()
                callback.invoke(null)
            })

    app().queue.add(excursionsRequest)
}

fun Context.getExcursion(
    id: Int,
    callback: (excursions: Excursion?) -> Unit,
) {
    val url = getString(R.string.api_url) + "excursions/$id"

    val excursionRequest =
        GsonRequest(url, Excursion::class.java,
            headers = mutableMapOf("Authorization" to "Bearer ${storage().loginData?.jwt}"),
            listener = {
                callback.invoke(it)
            }, errorListener = {
                it.printStackTrace()
                callback.invoke(null)
            })

    app().queue.add(excursionRequest)
}