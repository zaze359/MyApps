package com.zaze.apps.items

import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import com.zaze.apps.databinding.ItemOverviewCardBinding
import com.zaze.apps.ext.gone
import com.zaze.apps.ext.visible

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:40
 */
class OverviewHolder(private val binding: ItemOverviewCardBinding) :
    CardHolder<CardItem.Overview>(binding.root) {

    override fun doSetup(value: CardItem.Overview) {
        binding.overviewCardLogo.setImageResource(value.iconRes)
        binding.overviewCardTitleTv.text = value.title
        binding.overviewCardContentTv.text = value.content
        if (value.actionName.isNullOrEmpty()) {
            binding.overviewCardButton.gone()
        } else {
            binding.overviewCardButton.visible()
            binding.overviewCardButton.text = value.actionName
        }
        binding.overviewCardButton.setOnClickListener {
            value.doAction?.invoke()
        }
        itemView.background.setColorFilter(
            ContextCompat.getColor(itemView.context, value.backgroundColor),
            PorterDuff.Mode.SRC_ATOP
        )
    }
}
