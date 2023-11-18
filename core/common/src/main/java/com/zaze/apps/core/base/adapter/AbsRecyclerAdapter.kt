package com.zaze.apps.core.base.adapter

import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Description :
 * date : 2016-01-19 - 14:06
 *
 * @author : zaze
 * @version : 1.0
 */
abstract class AbsRecyclerAdapter<V : Any, VH : RecyclerView.ViewHolder> : ListAdapter<V, VH> {

    constructor(diffCallback: DiffUtil.ItemCallback<V>? = null) : super(diffCallback ?: object :
        DiffUtil.ItemCallback<V>() {
        // 对象是否相同
        override fun areItemsTheSame(oldItem: V, newItem: V): Boolean {
            return oldItem == newItem
        }

        // 内容是否相同
        override fun areContentsTheSame(oldItem: V, newItem: V): Boolean {
            return oldItem.toString() == newItem.toString()
        }
    })

    constructor(config: AsyncDifferConfig<V>) : super(config)

    override fun onBindViewHolder(holder: VH, position: Int) {
        val v: V? = getItem(position)
        if (v != null) {
            onBindView(holder, v, position)
        }
    }

    /**
     * view赋值
     *
     * @param holder   holder
     * @param value    value
     * @param position position
     */
    abstract fun onBindView(holder: VH, value: V, position: Int)
}