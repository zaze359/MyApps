package com.zaze.apps.items

import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.adapters.ProgressSubAdapter
import com.zaze.apps.data.Card
import com.zaze.apps.databinding.ItemCardProgressBinding
import com.zaze.apps.ext.gone
import com.zaze.apps.ext.invisible

/**
 * Description :
 * @author : zaze
 * @version : 2021-09-08 - 18:20
 */
class ProgressCardHolder(private val binding: ItemCardProgressBinding) :
    CardHolder<Card.Progress>(binding.root) {


    override fun doSetup(value: Card.Progress) {
        binding.progressTitleTv.text = value.title
        val msg =
            "${"%.2f".format(value.progress * 100.0F / value.max)}% - ${value.trans(value.progress)}/${
                value.trans(
                    value.max
                )
            }"
        binding.progressMainTv.text = msg
        binding.progressMainPb.let {
            it.max = value.max
            it.progress = value.progress
        }
        binding.root.setOnClickListener {
            value.doAction?.invoke()
        }
        if (value.subProgresses.isNullOrEmpty()) {
            binding.progressRecyclerView.invisible()
        } else {
            binding.progressRecyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            binding.progressRecyclerView.adapter = ProgressSubAdapter().apply {
                setDataList(value.subProgresses, false)
            }
        }
    }
}