package com.zaze.apps

import android.os.Bundle
import com.zaze.apps.base.AbsSlidingPanelActivity
import com.zaze.apps.ext.replaceFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AbsSlidingPanelActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(wrapSlidingMusicPanel(R.layout.activity_main))
//        getBottomNavigation().setOnNavigationItemReselectedListener {
//        }
        getBottomNavigation().setOnNavigationItemSelectedListener {
            selectFragment(it.itemId)
            true
        }
        selectFragment(R.id.action_overview)
    }

    private fun selectFragment(itemId: Int) =
        supportFragmentManager.findFragmentByTag("$itemId")
            ?: when (itemId) {
                R.id.action_overview -> OverviewFragment()
                R.id.action_app_list -> AppListFragment()
                else -> HomeViewPagerFragment()
            }.also {
                replaceFragment(it, R.id.mainFragmentContainer)
            }
}