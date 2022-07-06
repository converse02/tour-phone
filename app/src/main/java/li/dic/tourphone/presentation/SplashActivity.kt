package li.dic.tourphone.presentation

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.bridgefy.sdk.client.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import li.dic.tourphone.BuildConfig
import li.dic.tourphone.R
import li.dic.tourphone.presentation.auth.AuthActivity
import li.dic.tourphone.presentation.gid.GidActivity
import li.dic.tourphone.data.storage
import li.dic.tourphone.presentation.tourist.TouristActivity

@DelicateCoroutinesApi
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        Bridgefy.debug = BuildConfig.DEBUG

        Bridgefy.initialize(this, object : RegistrationListener() {
            override fun onRegistrationSuccessful(bridgefyClient: BridgefyClient) {
                Toast.makeText(this@SplashActivity, "reg_succ ${bridgefyClient.userUuid}", Toast.LENGTH_LONG).show()
                GlobalScope.launch {
                    delay(2000)
                    val nextIntent =
                        if (intent.data != null && intent.data!!.getQueryParameter("excursionId") != null) {
                            val link = intent.data!!.toString()
                            Intent(
                                this@SplashActivity,
                                TouristActivity::class.java
                            ).apply { putExtra("link", link) }
                        } else if (storage().loginData?.jwt == null) Intent(
                            this@SplashActivity,
                            AuthActivity::class.java
                        ) else Intent(this@SplashActivity, GidActivity::class.java)
                    startActivity(nextIntent)
                    finish()
                }
            }

            override fun onRegistrationFailed(errorCode: Int, message: String) {
                Toast.makeText(this@SplashActivity, "reg_failed", Toast.LENGTH_LONG).show()
            }
        })


    }
}