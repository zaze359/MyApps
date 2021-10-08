package com.zaze.apps.items

import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.adapters.ProgressSubAdapter
import com.zaze.apps.data.Card
import com.zaze.apps.databinding.ItemCardProgressBinding

/**
 * Description :
 * @author : zaze
 * @version : 2021-09-08 - 18:20
 */
class ProgressCardHolder(private val binding: ItemCardProgressBinding) :
    CardHolder<Card.Progress>(binding.root) {


    override fun doSetup(value: Card.Progress) {
        binding.progressTitleTv.text = value.title
        val progress = value.progresses.fold(0, { i, t ->
            i + t.progress
        })
        val msg = "${"%.2f".format(progress * 100.0F / value.max)}% - ${value.trans(progress)}/${
            value.trans(
                value.max
            )
        }"
        binding.progressMainTv.text = msg
        binding.progressMainPb.let {
            it.max = value.max
            it.progress = progress
        }
        binding.root.setOnClickListener {
            value.doAction?.invoke()
        }
        binding.progressRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
        binding.progressRecyclerView.adapter = ProgressSubAdapter(value.progresses, value.max)
    }
}