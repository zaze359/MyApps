package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEachIndexed
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.zaze.apps.adapters.HomePagerAdapter
import com.zaze.apps.base.AbsSlidingPanelFragment
import com.zaze.apps.data.HomePage
import com.zaze.apps.databinding.FragmentHomePagerBinding
import com.zaze.apps.ext.myViewModels
import com.zaze.apps.viewmodels.HomePagerViewModel
import kotlinx.coroutines.flow.collect

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-19 - 18:22
 */
class HomePagerFragment : AbsSlidingPanelFragment() {

    private lateinit var dataBinding: FragmentHomePagerBinding
    private val homePagerViewModel: HomePagerViewModel by myViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentHomePagerBinding.inflate(inflater, container, false)
        return wrapSlidingPanel(dataBinding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomNavView = getBottomNavigation()

        dataBinding.homeViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
//                ZLog.i(ZTag.TAG, "homeViewPager onPageSelected: $position")
                val menu = bottomNavView.menu
                if (position >= menu.size()) {
                    return
                }
                bottomNavView.selectedItemId = menu.getItem(position).itemId
            }
        })
//        bottomNavView.setOnNavigationItemReselectedListener {
//            ZLog.i(ZTag.TAG, "homeViewPager ReselectedListener")
//        }
        bottomNavView.setOnNavigationItemSelectedListener {
//            ZLog.i(ZTag.TAG, "homeViewPager SelectedListener")
            bottomNavView.menu.forEachIndexed { index, item ->
                if (it.itemId == item.itemId) {
                    dataBinding.homeViewPager.currentItem = index
                }
            }
            true
        }

        lifecycleScope.launchWhenResumed {
            homePagerViewModel.homePages.collect {
                dataBinding.homeViewPager.adapter = HomePagerAdapter(this@HomePagerFragment, it)
            }
        }
    }
}