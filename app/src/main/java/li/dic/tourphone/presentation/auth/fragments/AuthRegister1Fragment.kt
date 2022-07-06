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
import li.dic.tourphone.databinding.AuthRegister1FragmentBinding

class AuthRegister1Fragment : Fragment() {

    private lateinit var binding: AuthRegister1FragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)

        binding = AuthRegister1FragmentBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.textNeedHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Нужно быть самостоятельным!", Toast.LENGTH_SHORT)
                .show()
        }
        binding.btnNext.setOnClickListener {
            val surname = binding.etSurname.text.toString()
            val name = binding.etName.text.toString()
            val org = binding.etOrganization.text.toString()
            val isFill = surname.isNotEmpty() && name.isNotEmpty() && org.isNotEmpty()

            if (surname.isEmpty()) {
                binding.etSurname.error = getString(R.string.enter_all)
            }
            if (name.isEmpty()) {
                binding.etName.error = getString(R.string.enter_all)
            }
            if (org.isEmpty()) {
                binding.etOrganization.error = getString(R.string.enter_all)
            }
            if (isFill) {
                findNavController().navigate(
                    R.id.action_auth_register1_fragment_to_auth_register2_fragment,
                    bundleOf("fio" to name + surname, "org" to org),
                )
            }
        }
        binding.etSurname.addTextChangedListener { binding.tilSurname.isErrorEnabled = false }
        binding.etName.addTextChangedListener { binding.tilName.isErrorEnabled = false }
        binding.etOrganization.addTextChangedListener {
            binding.tilOrganization.isErrorEnabled = false
        }

        return binding.root
    }
}