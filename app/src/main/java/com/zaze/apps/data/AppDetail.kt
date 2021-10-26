package com.zaze.apps.data

import android.graphics.Bitmap

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-26 - 17:42
 */
data class AppDetail(
    val appName: String?,
    val appVersion: String?,
    val appIcon: Bitmap?,
    val appDetailItems: List<AppDetailItem>,
    val appDirs: List<AppDetailItem>
)