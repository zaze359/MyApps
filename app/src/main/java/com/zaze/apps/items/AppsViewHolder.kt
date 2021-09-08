package com.zaze.apps.items

import android.graphics.PorterDuff
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.adapters.LatelyUpdateAdapter
import com.zaze.apps.databinding.ItemAppsCardBinding
import com.zaze.apps.ext.onClick

/**
 * Description :
 * @author : zaze
 * @version : 2021-09-08 - 18:20
 */
class AppsViewHolder(private val binding: ItemAppsCardBinding) :
    CardHolder<CardItem.AppsView>(binding.root) {

    override fun doSetup(value: CardItem.AppsView) {
        binding.appsCardLogo.setImageResource(value.iconRes)
        binding.appsCardTitleTv.text = value.title
        binding.appsCardRecyclerView.let {
            it.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            it.adapter = LatelyUpdateAdapter(value.apps)
        }
        binding.root.onClick {
            value.doAction?.invoke()
        }
        itemView.background.setColorFilter(
            ContextCompat.getColor(itemView.context, value.backgroundColor),
            PorterDuff.Mode.SRC_ATOP
        )
    }
}