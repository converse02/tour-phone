package li.dic.tourphone.presentation.gid.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.textview.MaterialTextView
import li.dic.tourphone.databinding.ItemExcursionBinding
import li.dic.tourphone.domain.LocalExcursion

class ExcursionAdapter(private val list: List<LocalExcursion>) :
    RecyclerView.Adapter<ExcursionAdapter.ExcursionViewHolder>() {
    class ExcursionViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    var onExcursionClickListener: ((LocalExcursion) -> Unit)? = null
    private var textTime: MaterialTextView? = null
    private var textName: MaterialTextView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExcursionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemExcursionBinding.inflate(inflater, parent, false)

        textTime = binding.tvTime
        textName = binding.tvName

        return ExcursionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExcursionViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = list[position]

        if (textTime != null) {
            textTime!!.text = "${item.timeStart} - ${item.timeEnd}"
        }

        if (textName != null) {
            textName!!.text = item.name
        }
        holder.itemView.setOnClickListener {
            onExcursionClickListener?.invoke(item)
        }

    }

    override fun getItemCount(): Int = list.size
}