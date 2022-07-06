package li.dic.tourphone.presentation.gid.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.google.android.material.textview.MaterialTextView
import li.dic.tourphone.databinding.ItemExcursionsBinding
import li.dic.tourphone.domain.LocalExcursion
import li.dic.tourphone.domain.LocalExcursions

class ExcursionsAdapter(private val list: List<LocalExcursions>) :
    RecyclerView.Adapter<ExcursionsAdapter.ExcursionViewHolder>() {
    class ExcursionViewHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)

    var onExcursionClickListener: ((LocalExcursion, String) -> Unit)? = null
    private var textDay: MaterialTextView? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExcursionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemExcursionsBinding.inflate(inflater, parent, false)

        textDay = binding.tvHeader
        recyclerView = binding.rvItemExcursion

        return ExcursionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExcursionViewHolder, position: Int) {
        holder.setIsRecyclable(false)
        val item = list[position]

        if (textDay != null) {
            textDay!!.text = item.day
        }

        if (recyclerView != null) {
            val adapter = ExcursionAdapter(item.list)
            recyclerView!!.adapter = adapter
            adapter.onExcursionClickListener = {
                onExcursionClickListener?.invoke(it, item.day)
            }
            recyclerView!!.isNestedScrollingEnabled = false
            recyclerView!!.layoutManager = LinearLayoutManager(holder.itemView.context)
        }
    }

    override fun getItemCount(): Int = list.size
}