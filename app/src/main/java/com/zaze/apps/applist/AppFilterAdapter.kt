package com.zaze.apps.applist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.base.adapter.AbsRecyclerAdapter
import com.zaze.apps.data.AppFilter
import com.zaze.apps.databinding.ItemAppFilterBinding


class AppFilterAdapter(private val onItemClick: (AppFilter) -> Unit) :
    AbsRecyclerAdapter<AppFilter, AppFilterAdapter.FilterHolder>() {
    private var curPosition = 0
    override fun onBindView(holder: FilterHolder, value: AppFilter, position: Int) {
        holder.binding.filterBtn.apply {
            text = value.name
            setOnClickListener {
                val pre = curPosition
                curPosition = position
                notifyItemChanged(pre)
                notifyItemChanged(position)
                onItemClick.invoke(value)
            }
        }
        holder.binding.filterBtn.isEnabled = curPosition != position
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterHolder {
        return FilterHolder(
            ItemAppFilterBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class FilterHolder(val binding: ItemAppFilterBinding) : RecyclerView.ViewHolder(binding.root)

}