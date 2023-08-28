package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEachIndexed
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zaze.apps.adapters.HomePagerAdapter
import com.zaze.apps.base.AbsSlidingPanelFragment
import com.zaze.apps.databinding.FragmentHomePagerBinding
import com.zaze.apps.viewmodels.HomePagerViewModel
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-19 - 18:22
 */
class HomePagerFragment : AbsSlidingPanelFragment() {

    private lateinit var dataBinding: FragmentHomePagerBinding
    private val homePagerViewModel: HomePagerViewModel by viewModels()

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
        dataBinding.homeViewPager.isUserInputEnabled = false
        dataBinding.homeViewPager.getChildAt(0)?.overScrollMode = View.OVER_SCROLL_NEVER
//        dataBinding.homeViewPager.registerOnPageChangeCallback(object :
//            ViewPager2.OnPageChangeCallback() {
//            override fun onPageSelected(position: Int) {
//                super.onPageSelected(position)
//                ZLog.i(ZTag.TAG, "homeViewPager onPageSelected: $position")
//                val menu = bottomNavView.menu
//                if (position >= menu.size()) {
//                    return
//                }
//                bottomNavView.selectedItemId = menu.getItem(position).itemId
//            }
//        })
//        bottomNavView.setOnNavigationItemReselectedListener {
//            ZLog.i(ZTag.TAG, "homeViewPager ReselectedListener")
//        }
        bottomNavView.setOnItemSelectedListener {
            bottomNavView.menu.forEachIndexed { index, item ->
                ZLog.i(ZTag.TAG, "homeViewPager NavigationItemSelected: ${item.itemId}; $index")
                if (it.itemId == item.itemId) {
                    dataBinding.homeViewPager.currentItem = index
                }
            }
            true
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homePagerViewModel.homePages.collect {
                    dataBinding.homeViewPager.adapter = HomePagerAdapter(this@HomePagerFragment, it)
                }
            }
        }
    }
}