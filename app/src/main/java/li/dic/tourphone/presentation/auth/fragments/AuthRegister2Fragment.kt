package li.dic.tourphone.presentation.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import li.dic.tourphone.R
import li.dic.tourphone.databinding.AuthRegister2FragmentBinding

class AuthRegister2Fragment : Fragment() {

    private lateinit var binding: AuthRegister2FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)

        binding = AuthRegister2FragmentBinding.inflate(inflater, container, false)


        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.textNeedHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Нужно быть самостоятельным!", Toast.LENGTH_SHORT)
                .show()
        }
        binding.btnNext.setOnClickListener {
            val mail = binding.etMail.text.toString()
            val phone = binding.etPhone.text.toString()
            val isFill = mail.isNotEmpty() && phone.isNotEmpty()

            if (mail.isEmpty()) {
                binding.etMail.error = getString(R.string.enter_all)
            }
            if (phone.isEmpty()) {
                binding.etPhone.error = getString(R.string.enter_all)
            }
            if (isFill) {
                findNavController().navigate(
                    R.id.action_auth_register2_fragment_to_auth_register3_fragment,
                    bundleOf(
                        "phone" to phone,
                        "mail" to mail,
                        "fio" to arguments?.get("fio"),
                        "org" to arguments?.get("org"),
                    ),
                )
            }
        }

        binding.etPhone.addTextChangedListener { binding.tilPhone.isErrorEnabled = false }
        binding.etMail.addTextChangedListener { binding.tilMail.isErrorEnabled = false }

        return binding.root
    }
}