package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.OverviewFragmentDirections
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.databinding.ItemLatelyUpdateAppBinding
import com.zaze.apps.ext.onClick
import com.zaze.apps.utils.AppShortcut
import com.zaze.utils.date.DateUtil

/**
 * 最近更新应用信息
 */
class LatelyUpdateAdapter :
    BaseRecyclerAdapter<AppShortcut, LatelyUpdateAdapter.LatelyUpdateHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LatelyUpdateHolder {
        return LatelyUpdateHolder(
            ItemLatelyUpdateAppBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindView(holder: LatelyUpdateHolder, value: AppShortcut, position: Int) {
        holder.binding.latelyUpdateAppIv.setImageBitmap(value.appIcon)
        holder.binding.latelyUpdateAppNameTv.text = value.appName
        holder.binding.latelyUpdateAppUpdateTimeTv.text =
            DateUtil.timeMillisToString(value.lastUpdateTime, "yyyy.MM.dd")
        holder.binding.root.onClick {
            it.findNavController()
                .navigate(OverviewFragmentDirections.appDetailAction(value.packageName))
        }
    }


    class LatelyUpdateHolder(val binding: ItemLatelyUpdateAppBinding) :
        RecyclerView.ViewHolder(binding.root)
}