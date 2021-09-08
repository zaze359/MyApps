package com.zaze.apps.items

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.zaze.apps.R
import com.zaze.apps.utils.AppShortcut

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 13:30
 */
open class CardItem(val type: Int) {
    object Type {
        const val OVERVIEW = 0
        const val APPSVIEW = 1
    }

    data class Overview(
        val title: String,
        val content: String,
        @DrawableRes val iconRes: Int = 0,
        @ColorRes val backgroundColor: Int = R.color.white,
        val actionName: String? = null,
        val doAction: (() -> Unit)? = null
    ) : CardItem(Type.OVERVIEW)

    data class AppsView(
        val title: String,
        @DrawableRes val iconRes: Int = 0,
        val apps: List<AppShortcut>,
        @ColorRes val backgroundColor: Int = R.color.white,
        val doAction: (() -> Unit)? = null
    ) : CardItem(Type.APPSVIEW)
}

