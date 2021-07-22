package com.zaze.apps

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-20 - 09:18
 */
class HomePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return OverviewFragment()
    }
}