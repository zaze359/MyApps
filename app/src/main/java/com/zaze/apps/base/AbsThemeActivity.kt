package com.zaze.apps.base

import android.os.Bundle
import com.zaze.apps.R
import com.zaze.apps.ext.setImmersion
import com.zaze.apps.ext.setImmersionOnWindowFocusChanged
import com.zaze.apps.ext.setNavigationBarColor

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-15 - 15:27
 */
abstract class AbsThemeActivity : AbsLogActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setImmersion(isFullScreen(), getStatusBarColor())
        setNavigationBarColor(getStatusBarColor())
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setImmersionOnWindowFocusChanged(isFullScreen(), hasFocus)
    }

    /**
     * 获取状态栏色值
     * color res id
     */
    protected open fun getStatusBarColor(): Int {
        return R.color.colorPrimary
    }

    /**
     * 是否全屏
     */
    protected open fun isFullScreen(): Boolean {
        return false
    }
}