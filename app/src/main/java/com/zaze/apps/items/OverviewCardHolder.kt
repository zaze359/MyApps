package com.zaze.apps.items

import com.zaze.apps.data.Card
import com.zaze.apps.databinding.ItemCardOverviewBinding
import com.zaze.apps.ext.gone
import com.zaze.apps.ext.onClick
import com.zaze.apps.ext.visible

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
            binding.overviewBtn.gone()
            itemView.onClick {
                value.doAction?.invoke()
            }
        } else {
            binding.overviewBtn.let {
                it.visible()
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
