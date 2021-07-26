package com.zaze.apps.ext

import android.content.pm.ApplicationInfo

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-26 - 17:12
 */

fun ApplicationInfo.isSystemApp(): Boolean {
    return this.flags and ApplicationInfo.FLAG_SYSTEM > 0
}