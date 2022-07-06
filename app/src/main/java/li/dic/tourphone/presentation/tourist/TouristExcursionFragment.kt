package li.dic.tourphone.presentation.tourist

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.transition.MaterialSharedAxis
import li.dic.tourphone.R
import li.dic.tourphone.databinding.ExcursionListenerFragmentBinding
import li.dic.tourphone.presentation.tourist.TouristExcursionActivity.Companion.day
import li.dic.tourphone.presentation.tourist.TouristExcursionActivity.Companion.excursion
class TouristExcursionFragment : Fragment() {

    private lateinit var binding: ExcursionListenerFragmentBinding


    private var volume = 1F

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, /* forward= */false)

        binding = ExcursionListenerFragmentBinding.inflate(inflater, container, false)


        binding.volumeUp.visibility = View.VISIBLE
        binding.volumeOff.visibility = View.GONE

        binding.tvDay.text = day.toString()

        day.observeForever { day ->
            binding.tvDay.text = day
        }

        excursion.observeForever { excursion ->
            binding.tvName.text = excursion.name
            binding.tvTime.text = "${excursion.timeStart} - ${excursion.timeEnd}"
        }

        binding.btnBack.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Вы хотите покинуть экскурсию?")
                .setPositiveButton("Да") { p0, p1 ->
                    requireActivity().startActivity(
                        Intent(
                            requireContext(),
                            TouristActivity::class.java
                        )
                    )
                    p0.dismiss()
//                    player.stop()
                    requireActivity().finish()
                }
                .setNegativeButton("Нет") { p0, p1 ->
                    p0.dismiss()
                }
                .create()
                .show()
        }

        binding.volumeOff.setOnClickListener {
            volume = 1F
//            player.volume = volume
            it.visibility = View.GONE
            binding.volumeUp.visibility = View.VISIBLE
        }

        binding.volumeUp.setOnClickListener {
            volume = 0F
//            player.volume = volume
            it.visibility = View.GONE
            binding.volumeOff.visibility = View.VISIBLE
        }

        binding.chat.setOnClickListener {
            findNavController().navigate(R.id.TouristChatFragment)
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
//        val player = PlayerService.exoPlayer
//        player.play()
    }
}