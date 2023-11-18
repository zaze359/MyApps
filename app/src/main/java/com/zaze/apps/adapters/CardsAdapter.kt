package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zaze.apps.core.base.adapter.AbsRecyclerAdapter
import com.zaze.apps.core.base.adapter.DataRecyclerAdapter
import com.zaze.apps.items.AppsCardHolder
import com.zaze.apps.items.CardHolder
import com.zaze.apps.data.Card
import com.zaze.apps.databinding.ItemCardAppsBinding
import com.zaze.apps.databinding.ItemCardOverviewBinding
import com.zaze.apps.databinding.ItemCardProgressBinding
import com.zaze.apps.items.OverviewCardHolder
import com.zaze.apps.items.ProgressCardHolder

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:21
 */
class CardsAdapter : AbsRecyclerAdapter<Card, CardHolder<Card>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder<Card> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            Card.Type.OVERVIEW -> {
                OverviewCardHolder(
                    ItemCardOverviewBinding.inflate(inflater, parent, false)
                )
            }

            Card.Type.APPS -> {
                AppsCardHolder(
                    ItemCardAppsBinding.inflate(inflater, parent, false)
                )
            }

            Card.Type.PROGRESS -> {
                ProgressCardHolder(
                    ItemCardProgressBinding.inflate(inflater, parent, false)
                )
            }

            else -> {
                OverviewCardHolder(
                    ItemCardOverviewBinding.inflate(inflater, parent, false)
                )
            }
        }
    }

    override fun onBindView(holder: CardHolder<Card>, value: Card, position: Int) {
        holder.setup(value)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type ?: 0
    }
}