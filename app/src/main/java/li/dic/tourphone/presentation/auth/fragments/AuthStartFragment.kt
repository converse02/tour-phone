package li.dic.tourphone.presentation.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import li.dic.tourphone.databinding.AuthStartFragmentBinding
import li.dic.tourphone.presentation.tourist.TouristActivity

class AuthStartFragment : Fragment() {

    private lateinit var binding: AuthStartFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)

        binding = AuthStartFragmentBinding.inflate(inflater, container, false)

        binding.btnGid.setOnClickListener {
            findNavController().navigate(
                AuthStartFragmentDirections.actionAuthStartFragmentToAuthEnterFragment()
            )
        }

        binding.btnTourist.setOnClickListener {
            startActivity(Intent(requireContext(), TouristActivity::class.java))
//            requireActivity().finish()
        }

        return binding.root
    }
}