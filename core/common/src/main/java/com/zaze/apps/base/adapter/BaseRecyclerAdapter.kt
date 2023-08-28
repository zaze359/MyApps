package com.zaze.apps.base.adapter

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
abstract class BaseRecyclerAdapter<V : Any, H : RecyclerView.ViewHolder> : ListAdapter<V, H> {
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

    @Deprecated("use submitList()", ReplaceWith("submitList(list)"))
    open fun setDataList(list: List<V>?, isNotify: Boolean = true) {
        submitList(list)
    }

    override fun onBindViewHolder(holder: H, position: Int) {
        getItem(position)?.let {
            onBindView(holder, it, position)
        }
    }

    abstract fun onBindView(holder: H, value: V, position: Int)
}