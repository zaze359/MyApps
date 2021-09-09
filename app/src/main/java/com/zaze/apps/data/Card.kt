package com.zaze.apps.data

import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import com.zaze.apps.utils.AppShortcut

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 13:30
 */
sealed class Card(val type: Int) {
    object Type {
        const val OVERVIEW = 0
        const val APPS = 1
        const val PROGRESS = 2
    }

    /**
     * 用于描述一些信息
     */
    data class Overview(
        val title: String,
        val content: String,
        @DrawableRes val iconRes: Int = 0,
        val actionName: String? = null,
        val doAction: (() -> Unit)? = null
    ) : Card(Type.OVERVIEW)

    /**
     * 用于横向展示一些应用
     */
    data class Apps(
        val title: String,
        @DrawableRes val iconRes: Int = 0,
        val apps: List<AppShortcut>,
        val doAction: (() -> Unit)? = null
    ) : Card(Type.APPS)

    /**
     * 用于展示进度条
     * 支持三层
     */
    data class Progress(
        val title: String,
        val max: Int,
        val progresses: List<Item>,
        val trans: (Int) -> String,
        val doAction: (() -> Unit)? = null
    ) : Card(Type.PROGRESS) {

        data class Item(
            val tag: String,
            val icon: Bitmap? = null,
            val progress: Int
        )

    }
}

