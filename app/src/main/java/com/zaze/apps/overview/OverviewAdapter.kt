package com.zaze.apps.overview

import android.content.Context
import android.view.ViewGroup
import com.zaze.apps.base.adapter.DataRecyclerAdapter
import com.zaze.apps.items.AppsViewHolder
import com.zaze.apps.items.CardHolder
import com.zaze.apps.items.CardItem
import com.zaze.apps.items.OverviewHolder

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:21
 */
class OverviewAdapter(context: Context, data: List<CardItem>?) :
    DataRecyclerAdapter<CardItem, CardHolder<CardItem>>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder<CardItem> {
        return when (viewType) {
            CardItem.Type.OVERVIEW -> {
                OverviewHolder.create(parent)
            }
            else -> {
                OverviewHolder.create(parent)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.type ?: 0
    }

    override fun onBindViewHolder(holder: CardHolder<CardItem>, position: Int) {
        val value = getItem(position)
        value?.let {
//            holder.setup<CardItem>(value)
        }
    }
}