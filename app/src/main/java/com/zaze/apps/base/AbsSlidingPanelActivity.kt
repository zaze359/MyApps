package com.zaze.apps.base

import android.view.View
import androidx.annotation.LayoutRes
import androidx.navigation.NavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zaze.apps.databinding.SlidingPanelLayoutBinding

/**
 * Description : 封装了底部导航、抽屉和内容展示页
 * @author : zaze
 * @version : 2021-07-20 - 10:20
 */
abstract class AbsSlidingPanelActivity : AbsActivity() {
    private val panelBinding: SlidingPanelLayoutBinding by lazy {
        SlidingPanelLayoutBinding.inflate(layoutInflater)
    }

    val bottomNavigationView
        get() = panelBinding.panelBottomNav

    protected fun wrapSlidingMusicPanel(view: View): View {
        panelBinding.panelContentFrame.removeAllViews()
        panelBinding.panelContentFrame.addView(view)
        return panelBinding.root
    }

    protected fun wrapSlidingMusicPanel(@LayoutRes layoutResId: Int): View {
        layoutInflater.inflate(layoutResId, panelBinding.panelContentFrame)
//        panelBinding.panelBottomNav
        return panelBinding.root
    }

    protected fun getBottomNavigation(): BottomNavigationView {
        return panelBinding.panelBottomNav
    }
}