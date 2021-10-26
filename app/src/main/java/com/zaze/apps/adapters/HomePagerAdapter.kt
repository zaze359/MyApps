package com.zaze.apps.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.zaze.apps.OverviewFragment
import com.zaze.apps.data.HomePage

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-20 - 09:18
 */
class HomePagerAdapter(fragment: Fragment, private val homePages: List<HomePage>) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return homePages.size
    }

    override fun createFragment(position: Int): Fragment {
        return homePages[position].fragment
    }
}