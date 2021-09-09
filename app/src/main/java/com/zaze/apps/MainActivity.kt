package com.zaze.apps

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.zaze.apps.base.AbsSlidingPanelActivity
import com.zaze.apps.ext.*
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AbsSlidingPanelActivity() {
    private var currentFragment: Fragment? = null
//
//    override fun showLifeCycle(): Boolean {
//        return true
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(wrapSlidingMusicPanel(R.layout.activity_main))
//        getBottomNavigation().setOnNavigationItemReselectedListener {
//        }
        getBottomNavigation().setOnNavigationItemSelectedListener {
            selectFragment(it.itemId)
            true
        }
        if (savedInstanceState == null) {
            selectFragment(R.id.action_overview)
        } else {
            currentFragment = supportFragmentManager.findFragmentById(R.id.mainFragmentContainer)
        }
    }

    private fun selectFragment(itemId: Int) {
        val finedFragment = supportFragmentManager.findFragmentByTag("$itemId")
        if (currentFragment != null && currentFragment == finedFragment) {
            return
        }
        hideFragment(currentFragment)
        if (finedFragment != null && finedFragment.isAdded) {
            currentFragment = finedFragment
            showFragment(finedFragment)
            return
        }
        when (itemId) {
            R.id.action_overview -> OverviewFragment()
            R.id.action_app_list -> AppListFragment()
            else -> HomeViewPagerFragment()
        }.also {
            currentFragment = it
            addFragment(R.id.mainFragmentContainer, it, "$itemId")
        }
    }
}