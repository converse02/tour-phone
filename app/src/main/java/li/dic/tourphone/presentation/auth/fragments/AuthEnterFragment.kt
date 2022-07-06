package li.dic.tourphone.presentation.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import li.dic.tourphone.R
import li.dic.tourphone.databinding.AuthEnterFragmentBinding
import li.dic.tourphone.domain.auth.Login
import li.dic.tourphone.data.extensions.login
import li.dic.tourphone.presentation.gid.GidActivity
import li.dic.tourphone.data.storage

class AuthEnterFragment : Fragment() {

    private lateinit var binding: AuthEnterFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)

        binding = AuthEnterFragmentBinding.inflate(inflater, container, false)

        binding.btnEnter.setOnClickListener {
            val login = binding.etLogin.text.toString()
            val password = binding.etPassword.text.toString()
            val isFill = login.isNotEmpty() && password.isNotEmpty()

            if (login.isEmpty()) {
                binding.tilLogin.error = getString(R.string.enter_all)
            }
            if (password.isEmpty()) {
                binding.tilPassword.error = getString(R.string.enter_all)
            }
            if (isFill) {
                binding.loginLoader.visibility = View.VISIBLE
                binding.btnEnter.visibility = View.INVISIBLE

                val callback = object : (Login?) -> Unit {
                    override fun invoke(loginData: Login?) {
                        if (loginData == null) {
                            binding.tilPassword.error = getString(R.string.error_auth)
                            binding.loginLoader.visibility = View.GONE
                            binding.btnEnter.visibility = View.VISIBLE
                        } else {
                            activity?.runOnUiThread {
                                context?.storage()?.loginData = loginData
                                startActivity(Intent(requireContext(), GidActivity::class.java))
                                requireActivity().finish()
                            }
                        }
                    }
                }

                context?.login(callback, login, password)
            }
        }
        binding.btnRegister.setOnClickListener {
            findNavController().navigate(
                AuthEnterFragmentDirections.actionAuthEnterFragmentToAuthRegister1Fragment()
            )

        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.textForgetPassword.setOnClickListener {
            Toast.makeText(requireContext(), "Что поделать, вспоминайте!", Toast.LENGTH_SHORT)
                .show()
        }
        binding.textNeedHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Нужно быть самостоятельным!", Toast.LENGTH_SHORT)
                .show()
        }

        binding.etLogin.addTextChangedListener { binding.tilLogin.isErrorEnabled = false }
        binding.etPassword.addTextChangedListener { binding.tilPassword.isErrorEnabled = false }

        return binding.root
    }
}