package com.zaze.apps.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import com.zaze.utils.SignaturesUtil.getSignatures
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description : 应用快捷信息(汇总最新的 Applicaiton 和 packageInfo)
 *
 * @author : ZAZE
 * @version : 2017-12-22 - 17:26
 */
data class AppShortcut(
    var name: String = "",
    var packageName: String = "",
    var versionCode: Long = 0,
    var versionName: String = "",
    var sourceDir: String? = null,
    var uid: Int = 0,
    var flags: Int = 0,
    var signingInfo: String? = null,
    var isInstalled: Boolean = false,
    var isCopyEnable: Boolean = true,
    var firstInstallTime: Long = 0,
    var lastUpdateTime: Long = 0
) {
    companion object {
        @JvmStatic
        fun transform(context: Context, packageInfo: PackageInfo): AppShortcut {
            val appShortcut = AppShortcut()
            appShortcut.packageName = packageInfo.packageName
            appShortcut.versionCode = packageInfo.versionCode.toLong()
            appShortcut.versionName = packageInfo.versionName
            appShortcut.signingInfo = getSignatures(object : ContextWrapper(context) {
                override fun getPackageName(): String {
                    return packageInfo.packageName
                }
            }, "MD5")
            val applicationInfo = packageInfo.applicationInfo
            if (applicationInfo != null) {
                appShortcut.name =
                    context.packageManager.getApplicationLabel(applicationInfo).toString()
                appShortcut.isInstalled = true
                appShortcut.sourceDir = applicationInfo.sourceDir
                appShortcut.flags = applicationInfo.flags
                appShortcut.uid = applicationInfo.uid
            } else {
                appShortcut.isInstalled = false
            }
            appShortcut.firstInstallTime = packageInfo.firstInstallTime
            appShortcut.lastUpdateTime = packageInfo.lastUpdateTime
            ZLog.i(ZTag.TAG, "appShortcut: $appShortcut")
            return appShortcut
        }
    }

    fun isSystemApp(): Boolean {
        return flags and ApplicationInfo.FLAG_SYSTEM != 0
    }


    override fun toString(): String {
        return "AppShortcut(name=$name, packageName=$packageName, versionCode=$versionCode, versionName=$versionName, sourceDir=$sourceDir, uid=$uid, flags=$flags, signingInfo=$signingInfo, isInstalled=$isInstalled, isCopyEnable=$isCopyEnable, firstInstallTime=$firstInstallTime, lastUpdateTime=$lastUpdateTime)"
    }
}