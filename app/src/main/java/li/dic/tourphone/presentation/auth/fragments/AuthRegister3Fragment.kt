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
import li.dic.tourphone.databinding.AuthRegister3FragmentBinding
import li.dic.tourphone.domain.auth.Login
import li.dic.tourphone.data.extensions.register
import li.dic.tourphone.presentation.gid.GidActivity
import li.dic.tourphone.data.storage

class AuthRegister3Fragment : Fragment() {

    private lateinit var binding: AuthRegister3FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)

        binding = AuthRegister3FragmentBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.textNeedHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Нужно быть самостоятельным!", Toast.LENGTH_SHORT)
                .show()
        }
        binding.btnNext.setOnClickListener {
            val password = binding.etPassword.text.toString()
            val isFill = password.isNotEmpty()

            if (password.isEmpty()) {
                binding.etPassword.error = getString(R.string.enter_all)
            }
            if (isFill) {
                binding.registerLoader.visibility = View.VISIBLE
                binding.btnNext.visibility = View.INVISIBLE

                val callback = object : (Login?) -> Unit {
                    override fun invoke(loginData: Login?) {
                        if (loginData == null) {
                            binding.etPassword.error = "Не удалось зарегистрироваться"
                            binding.registerLoader.visibility = View.GONE
                            binding.btnNext.visibility = View.VISIBLE
                        } else {
                            activity?.runOnUiThread {
                                context?.storage()?.loginData = loginData
                                startActivity(Intent(requireContext(), GidActivity::class.java))
                                requireActivity().finish()
                            }
                        }
                    }
                }
                context?.register(
                    callback,
                    login = arguments?.getString("mail"),
                    fio = arguments?.getString("fio"),
                    phone = arguments?.getString("phone"),
                    email = arguments?.getString("mail"),
                    password = password,
                )
            }
        }
        binding.etPassword.addTextChangedListener { binding.tilPassword.isErrorEnabled = false }

        return binding.root
    }
}