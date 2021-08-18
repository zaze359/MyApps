package com.zaze.apps.overview

import android.content.Context
import android.view.ViewGroup
import com.zaze.apps.base.adapter.DataRecyclerAdapter
import com.zaze.apps.items.CardHolder
import com.zaze.apps.items.CardItem
import com.zaze.apps.items.OverviewHolder

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 10:21
 */
class OverviewAdapter(context: Context, data: List<CardItem>?) :
    DataRecyclerAdapter<CardItem, CardHolder>(data) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        return when (viewType) {
            CardItem.Type.OVERVIEW -> {
                OverviewHolder.create(parent)
            }
            else -> {
                OverviewHolder.create(parent)
            }
        }
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        val value = getItem(position)
        value?.let {
            holder.setup(value)
        }
    }
}