package com.zaze.apps.items

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.zaze.apps.data.Card
import com.zaze.apps.databinding.ItemCardOverviewBinding
import com.zaze.apps.ext.onClick

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:40
 */
class OverviewCardHolder(private val binding: ItemCardOverviewBinding) :
    CardHolder<Card.Overview>(binding.root) {

    override fun doSetup(value: Card.Overview) {
        binding.overviewLogoIv.setImageResource(value.iconRes)
        binding.overviewTitleTv.text = value.title
        binding.overviewContentTv.text = value.content
        if (value.actionName.isNullOrEmpty()) {
            binding.overviewBtn.isGone = true
            itemView.onClick {
                value.doAction?.invoke()
            }
        } else {
            binding.overviewBtn.let {
                it.isVisible = true
                it.text = value.actionName
                it.setOnClickListener {
                    value.doAction?.invoke()
                }
            }
        }
//        itemView.background.setColorFilter(
//            ContextCompat.getColor(itemView.context, value.backgroundColor),
//            PorterDuff.Mode.SRC_ATOP
//        )
    }
}
