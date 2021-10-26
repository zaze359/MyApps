package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.zaze.apps.base.AbsSlidingPanelFragment
import com.zaze.apps.databinding.FragmentHomeBinding
import com.zaze.apps.ext.transact

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-20 - 17:56
 */
class HomeFragment : AbsSlidingPanelFragment() {
    private var currentFragment: Fragment? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return wrapSlidingPanel(dataBinding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getBottomNavigation().setOnNavigationItemSelectedListener {
            selectFragment(it.itemId)
            true
        }
        lifecycleScope.launchWhenResumed {
            if (savedInstanceState == null) {
                selectFragment(R.id.overview_dest)
            } else {
                currentFragment = childFragmentManager.findFragmentById(R.id.home_container)
            }
        }
    }

    private fun selectFragment(itemId: Int) {
        val finedFragment = childFragmentManager.findFragmentByTag("$itemId")
        if (currentFragment != null && currentFragment == finedFragment) {
            return
        }
        currentFragment?.let {
            childFragmentManager.transact {
                hide(it)
            }
        }
        if (finedFragment != null && finedFragment.isAdded) {
            currentFragment = finedFragment
            childFragmentManager.transact {
                show(finedFragment)
            }
            return
        }
        when (itemId) {
            R.id.overview_dest -> OverviewFragment()
            R.id.app_list_dest -> AppListFragment()
            else -> HomePagerFragment()
        }.also {
            currentFragment = it
            childFragmentManager.transact {
                add(R.id.home_container, it, "$itemId")
            }
        }
    }
}