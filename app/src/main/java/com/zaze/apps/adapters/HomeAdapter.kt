package com.zaze.apps.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zaze.apps.data.HomePage

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-20 - 09:18
 */
class HomeAdapter(activity: FragmentActivity, private val homePages: List<HomePage>) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return homePages.size
    }

    override fun createFragment(position: Int): Fragment {
        return homePages[position].fragment
    }
}