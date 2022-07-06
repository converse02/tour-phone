package li.dic.tourphone.data.volley

import androidx.annotation.Keep
import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

@Keep
class GsonRequest<T>(
    url: String,
    private val clazz: Class<T>,
    private val headers: MutableMap<String, String>? = mutableMapOf(),
    private val body: JSONObject? = null,
    private val listener: Response.Listener<T>,
    private val rMethod: Int = Method.GET,
    errorListener: Response.ErrorListener,
) : Request<T>(rMethod, url, errorListener) {
    private val gson = Gson()

    override fun getHeaders(): MutableMap<String, String> = headers ?: super.getHeaders()

    override fun deliverResponse(response: T) = listener.onResponse(response)

    override fun parseNetworkResponse(response: NetworkResponse?): Response<T> {
        return try {
            val json = String(
                response?.data ?: ByteArray(0),
                Charset.forName(HttpHeaderParser.parseCharset(response?.headers))
            )
            Response.success(
                gson.fromJson(json, clazz),
                HttpHeaderParser.parseCacheHeaders(response)
            )
        } catch (e: UnsupportedEncodingException) {
            Response.error(ParseError(e))
        } catch (e: JsonSyntaxException) {
            Response.error(ParseError(e))
        }
    }

    override fun getBody(): ByteArray {
        return if (rMethod == Method.POST) body.toString().toByteArray(charset("utf-8"))
        else return super.getBody()
    }

    override fun getBodyContentType(): String {
        return if (rMethod == Method.POST) "application/json; charset=utf-8" else super.getBodyContentType()
    }
}