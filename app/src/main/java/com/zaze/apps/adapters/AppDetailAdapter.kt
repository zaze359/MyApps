package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.core.base.adapter.AbsRecyclerAdapter
import com.zaze.apps.data.AppDetailItem
import com.zaze.apps.databinding.ItemAppDetailBinding
import com.zaze.apps.feature.settings.model.SettingsItem

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-26 - 13:22
 */
class AppDetailAdapter :
    AbsRecyclerAdapter<AppDetailItem, AppDetailAdapter.AppDetailHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<AppDetailItem>() {
            override fun areItemsTheSame(oldItem: AppDetailItem, newItem: AppDetailItem): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(
                oldItem: AppDetailItem,
                newItem: AppDetailItem
            ): Boolean {
                return oldItem.toString() == newItem.toString()
            }
        }
    }

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