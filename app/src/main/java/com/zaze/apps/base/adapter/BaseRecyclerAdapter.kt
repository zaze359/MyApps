package com.zaze.apps.base.adapter

import androidx.recyclerview.widget.RecyclerView

/**
 * Description :
 * date : 2016-01-19 - 14:06
 *
 * @author : zaze
 * @version : 1.0
 */
abstract class BaseRecyclerAdapter<V, H : RecyclerView.ViewHolder> : DataRecyclerAdapter<V, H>() {

    override fun onBindViewHolder(holder: H, position: Int) {
        getItem(position)?.let {
            onBindView(holder, it, position)
        }
    }

    abstract fun onBindView(holder: H, value: V, position: Int)
}