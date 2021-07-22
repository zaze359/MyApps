package com.zaze.apps

import android.os.Bundle
import com.zaze.apps.base.AbsSlidingPanelActivity
import com.zaze.apps.ext.replaceFragment

class MainActivity : AbsSlidingPanelActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(wrapSlidingMusicPanel(R.layout.activity_main))
        getBottomNavigation().also {
//            it.setOnNavigationItemReselectedListener {
//            }
            it.setOnNavigationItemSelectedListener {
                selectFragment(it.itemId)
                true
            }
        }
        selectFragment(R.id.action_overview)
    }

    private fun selectFragment(itemId: Int) =
        supportFragmentManager.findFragmentByTag("$itemId")
            ?: when (itemId) {
                R.id.action_overview -> OverviewFragment()
                else -> HomeViewPagerFragment()
            }.also {
                replaceFragment(it, R.id.mainFragmentContainer)
            }
}