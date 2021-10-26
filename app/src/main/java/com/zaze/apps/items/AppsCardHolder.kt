package com.zaze.apps.items

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.adapters.LatelyUpdateAdapter
import com.zaze.apps.data.Card
import com.zaze.apps.databinding.ItemCardAppsBinding

/**
 * Description :
 * @author : zaze
 * @version : 2021-09-08 - 18:20
 */
class AppsCardHolder(private val binding: ItemCardAppsBinding) :
    CardHolder<Card.Apps>(binding.root) {

    override fun doSetup(value: Card.Apps) {
        binding.appsLogoIv.setImageResource(value.iconRes)
        binding.appsTitleTv.text = value.title
        binding.appsRecyclerView.let {
            it.layoutManager = LinearLayoutManager(itemView.context, RecyclerView.HORIZONTAL, false)
            it.adapter = LatelyUpdateAdapter().apply {
                setDataList(value.apps, false)
            }
        }
    }
}