package com.zaze.apps.items

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.zaze.apps.R

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 13:30
 */
open class CardItem(open val type: Int) {

    object Type {
        const val OVERVIEW = 1
    }

    data class Overview(
        override val type: Int,
        val title: String,
        val content: String,
        @DrawableRes val iconRes: Int? = null,
        @ColorRes val backgroundColor: Int = R.color.white,
        val action: String? = null,
        val doAction: (() -> Unit)? = null
    ) : CardItem(type)
}

