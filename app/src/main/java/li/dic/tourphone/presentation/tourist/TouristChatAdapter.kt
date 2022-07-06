package li.dic.tourphone.presentation.tourist

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import li.dic.tourphone.databinding.LayoutMessageIncomingBinding
import li.dic.tourphone.databinding.LayoutMessageOutgoingBinding
import li.dic.tourphone.domain.Message
import li.dic.tourphone.domain.MessageType

class TouristChatAdapter :
    ListAdapter<Message, TouristChatAdapter.MessageViewHolder>(MessageDiffCallback()) {

    class MessageViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)
    class MessageDiffCallback: DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message) =
            oldItem.text == newItem.text && oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
    }

    private var text: TextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = when(viewType) {
            VIEW_INCOMING -> {
                val view = LayoutMessageIncomingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                text = view.txtMessage

                view
            }
            else -> {
                val view = LayoutMessageOutgoingBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                text = view.txtMessage

                view
            }
        }

        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: MessageViewHolder, position: Int) {
        val msg = getItem(position)
        viewHolder.setIsRecyclable(false)

        if (text != null) {
            text!!.text = msg.name + " : " + msg.text
        }
    }

    override fun getItemViewType(position: Int) =
        when (getItem(position).type) {
            MessageType.INCOMING -> VIEW_INCOMING
            else -> VIEW_OUTGOING
        }

    companion object {
        const val VIEW_INCOMING = 100
        const val VIEW_OUTGOING = 101
    }
}

