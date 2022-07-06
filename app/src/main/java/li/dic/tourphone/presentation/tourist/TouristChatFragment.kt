package li.dic.tourphone.presentation.tourist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bridgefy.sdk.client.Bridgefy
import com.bridgefy.sdk.client.Message
import li.dic.tourphone.databinding.TouristChatFragmentBinding
import li.dic.tourphone.domain.MessageType

class TouristChatFragment : Fragment() {

    private lateinit var binding: TouristChatFragmentBinding

    val adapter = TouristChatAdapter()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TouristChatFragmentBinding.inflate(inflater, container, false)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.chatRv.adapter = adapter
        binding.chatRv.layoutManager = LinearLayoutManager(requireContext())

        val viewModel = ViewModelProvider(requireActivity())[TouristViewModel::class.java]

        if (viewModel.messages.value == null) {
            viewModel.messages.value = mutableListOf()
        }

        viewModel.messages.observe(viewLifecycleOwner) { messageObserver(it) }

        binding.btnSendMsg.setOnClickListener {
            val msg = binding.etPhone.text.toString()
            val name = "Слушатель ${Bridgefy.getInstance().bridgefyClient.userUuid}"

            Toast.makeText(requireActivity(), "Tedfdf$name", Toast.LENGTH_LONG).show()

            if (msg != "") {
                binding.etPhone.text.clear()
                val message = li.dic.tourphone.domain.Message(
                    name,
                    msg,
                    MessageType.OUTGOING
                )
                val map = hashMapOf(
                    "type" to "msg",
                    "name" to name,
                    "message" to msg
                )
                val list = viewModel.messages.value

                list?.add(message)

                viewModel.messages.postValue(list)

                Bridgefy.sendBroadcastMessage(Message.Builder().setContent(map).build())
            }
        }


        return binding.root
    }

    fun messageObserver(list: MutableList<li.dic.tourphone.domain.Message>) {
        adapter.submitList(list)
        adapter.notifyDataSetChanged()

    }

}