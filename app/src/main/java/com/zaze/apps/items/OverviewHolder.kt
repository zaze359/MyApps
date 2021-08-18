package com.zaze.apps.items

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.zaze.apps.R
import com.zaze.apps.ext.gone
import com.zaze.apps.ext.visible

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:40
 */
class OverviewHolder(itemView: View) : CardHolder(itemView) {
    companion object {
        fun create(viewGroup: ViewGroup): OverviewHolder {
            return OverviewHolder(
                LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.item_overview_card, viewGroup, false)
            )
        }
    }

    val overviewCardLogo: ImageView = itemView.findViewById(R.id.overviewCardLogo)
    val overviewCardTitleTv: TextView = itemView.findViewById(R.id.overviewCardTitleTv)
    val overviewCardContentTv: TextView = itemView.findViewById(R.id.overviewCardContentTv)
    val overviewCardButton: Button = itemView.findViewById(R.id.overviewCardButton)

    override fun setup(value: CardItem) {
        if (value !is CardItem.Overview) {
            return
        }
        value.iconRes?.let {
            overviewCardLogo.setImageResource(it)
        }
        overviewCardTitleTv.text = value.title
        overviewCardContentTv.text = value.content
        if (value.action.isNullOrEmpty()) {
            overviewCardButton.gone()
        } else {
            overviewCardButton.visible()
            overviewCardButton.text = value.action
        }
        overviewCardButton.setOnClickListener {
            value.doAction?.invoke()
        }

        itemView.background.setColorFilter(
            ContextCompat.getColor(itemView.context, value.backgroundColor),
            PorterDuff.Mode.SRC_ATOP
        )
//        itemView.setBackgroundColor(itemView.resources.getColor(value.backgroundColor))
    }
}
