package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.data.AppDetailItem
import com.zaze.apps.databinding.ItemAppDetailBinding

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-26 - 13:22
 */
class AppDetailAdapter : BaseRecyclerAdapter<AppDetailItem, AppDetailAdapter.AppDetailHolder>() {

    override fun onBindView(holder: AppDetailHolder, value: AppDetailItem, position: Int) {
        holder.binding.appDetailTagTv.text = value.key
        holder.binding.appDetailContentTv.text = value.value ?: ""
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppDetailHolder {
        return AppDetailHolder(
            ItemAppDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    class AppDetailHolder(val binding: ItemAppDetailBinding) : RecyclerView.ViewHolder(binding.root)
}