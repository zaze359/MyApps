package com.zaze.apps.base

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zaze.apps.R
import com.zaze.apps.databinding.SlidingPanelLayoutBinding

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-20 - 10:20
 */
abstract class AbsSlidingPanelActivity : AbsActivity() {
    lateinit var panelBinding: SlidingPanelLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        panelBinding = SlidingPanelLayoutBinding.inflate(layoutInflater)
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