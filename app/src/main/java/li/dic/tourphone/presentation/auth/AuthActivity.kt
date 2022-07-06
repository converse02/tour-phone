package li.dic.tourphone.presentation.auth

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import androidx.navigation.NavController
import androidx.navigation.Navigation
import li.dic.tourphone.R
import li.dic.tourphone.databinding.AuthActivityBinding

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: AuthActivityBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.authHostFragment)

        binding.btnMenu.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://tourphone.ru/"))
            startActivity(browserIntent)
        }
    }
}