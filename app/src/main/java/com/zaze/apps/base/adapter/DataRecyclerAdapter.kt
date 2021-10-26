package com.zaze.apps.base.adapter

import androidx.recyclerview.widget.RecyclerView
import java.util.*

/**
 * Description :
 * date : 2016-01-19 - 14:06
 *
 * @author : zaze
 * @version : 1.0
 */
abstract class DataRecyclerAdapter<V, H : RecyclerView.ViewHolder> : RecyclerView.Adapter<H>() {
    private val dataList: MutableList<V> = ArrayList()

    open fun setDataList(list: Collection<V>?, isNotify: Boolean = true) {
        dataList.clear()
        if (list != null && list.isNotEmpty()) {
            dataList.addAll(list)
        }
        if (isNotify) {
            notifyDataSetChanged()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    fun getItem(position: Int): V? {
        return if (position < 0 || position >= dataList.size) {
            null
        } else {
            dataList[position]
        }
    }

    fun getDataList(): List<V> {
        return dataList
    }

}