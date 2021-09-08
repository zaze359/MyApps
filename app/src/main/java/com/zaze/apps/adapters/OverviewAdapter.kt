package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.zaze.apps.base.adapter.DataRecyclerAdapter
import com.zaze.apps.databinding.ItemAppsCardBinding
import com.zaze.apps.databinding.ItemOverviewCardBinding
import com.zaze.apps.items.AppsViewHolder
import com.zaze.apps.items.CardHolder
import com.zaze.apps.items.CardItem
import com.zaze.apps.items.OverviewHolder

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:21
 */
class OverviewAdapter(data: List<CardItem>?) :
    DataRecyclerAdapter<CardItem, CardHolder<CardItem>>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder<CardItem> {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CardItem.Type.OVERVIEW -> {
                OverviewHolder(
                    ItemOverviewCardBinding.inflate(inflater, parent, false)
                )
            }
            CardItem.Type.APPSVIEW -> {
                AppsViewHolder(
                    ItemAppsCardBinding.inflate(inflater, parent, false)
                )
            }
            else -> {
                OverviewHolder(
                    ItemOverviewCardBinding.inflate(inflater, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type ?: 0
    }

    override fun onBindViewHolder(holder: CardHolder<CardItem>, position: Int) {
        val value = getItem(position)
        value?.let {
            holder.setup(value)
        }
    }
}