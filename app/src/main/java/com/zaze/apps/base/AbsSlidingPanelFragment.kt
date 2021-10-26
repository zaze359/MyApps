package com.zaze.apps.base

import android.view.View
import androidx.annotation.LayoutRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zaze.apps.databinding.SlidingPanelLayoutBinding

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-20 - 10:20
 */
abstract class AbsSlidingPanelFragment : AbsFragment() {
    private val panelBinding: SlidingPanelLayoutBinding by lazy {
        SlidingPanelLayoutBinding.inflate(layoutInflater)
    }

    protected fun wrapSlidingPanel(view: View): View {
        panelBinding.panelContentFrame.removeAllViews()
        panelBinding.panelContentFrame.addView(view)
        return panelBinding.root
    }

    protected fun wrapSlidingPanel(@LayoutRes layoutResId: Int): View {
        layoutInflater.inflate(layoutResId, panelBinding.panelContentFrame)
//        panelBinding.panelBottomNav
        return panelBinding.root
    }

    protected fun getBottomNavigation(): BottomNavigationView {
        return panelBinding.panelBottomNav
    }
}