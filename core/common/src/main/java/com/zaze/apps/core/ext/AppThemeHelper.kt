package com.zaze.apps.core.ext

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import com.zaze.core.common.utils.ScreenUtils
import com.zaze.core.common.R
import com.zaze.core.designsystem.util.ColorUtil

/**
 * Description : 设置应用主题
 * @author : zaze
 * @version : 2021-07-22 - 15:29
 */
fun Activity.setImmersion(isFullScreen: Boolean = false, color: Int = R.color.colorPrimary) {
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//        return
//    }
//    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//        return
//    }
    if (isFullScreen) {
        ScreenUtils.addLayoutFullScreen(window)
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
        window.statusBarColor = Color.TRANSPARENT
    } else {
        window.statusBarColor = ContextCompat.getColor(this, color)
    }
}

fun Activity.setImmersionOnWindowFocusChanged(isFullScreen: Boolean = false, hasFocus: Boolean) {
    if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isFullScreen) {
        ScreenUtils.addLayoutFullScreen(window)
        window.decorView.systemUiVisibility =
            window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_FULLSCREEN
    }
}

/**
 * 设置navigationBarColor颜色
 */
fun Activity.setNavigationBarColor(color: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        window.navigationBarColor = color
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        window.navigationBarColor = ColorUtil.darkenColor(color)
    }
    setLightNavigationBar(ColorUtil.isColorLight(color))
}

fun Activity.setLightNavigationBar(enabled: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val decorView = window.decorView
        var systemUiVisibility = decorView.systemUiVisibility
        systemUiVisibility = if (enabled) {
            systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        } else {
            systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
        decorView.systemUiVisibility = systemUiVisibility
    }
}