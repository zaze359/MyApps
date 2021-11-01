package com.zaze.apps.data

import androidx.annotation.DrawableRes

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-28 - 13:18
 */
data class OperationBean(val title: String, @DrawableRes val iconRes: Int, val action: () -> Unit)