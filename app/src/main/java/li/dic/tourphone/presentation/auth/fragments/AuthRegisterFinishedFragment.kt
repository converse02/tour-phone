package li.dic.tourphone.presentation.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import li.dic.tourphone.databinding.AuthRegisterFinishedFragmentBinding

class AuthRegisterFinishedFragment : Fragment() {

    private lateinit var binding: AuthRegisterFinishedFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)

        binding = AuthRegisterFinishedFragmentBinding.inflate(inflater, container, false)

        binding.textName.text = arguments?.get("fio").toString()
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().finish()
                    }
                }
            }
            )
        return binding.root
    }
}