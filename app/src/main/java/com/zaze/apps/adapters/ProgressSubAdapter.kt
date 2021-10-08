package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.data.Card
import com.zaze.apps.databinding.ItemCardProgressSubBinding

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-08 - 18:10
 */
class ProgressSubAdapter(data: Collection<Card.Progress.Sub>?, private val max: Int) :
    BaseRecyclerAdapter<Card.Progress.Sub, ProgressSubAdapter.ProgressSubHolder>(data) {

    override fun onBindView(holder: ProgressSubHolder, value: Card.Progress.Sub, position: Int) {
        holder.binding.progressSubTagTv.text = value.tag
        holder.binding.progressPb.let {
            it.max = max
            it.progress = value.progress
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressSubHolder {
        return ProgressSubHolder(
            ItemCardProgressSubBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class ProgressSubHolder(val binding: ItemCardProgressSubBinding) :
        RecyclerView.ViewHolder(binding.root)

}