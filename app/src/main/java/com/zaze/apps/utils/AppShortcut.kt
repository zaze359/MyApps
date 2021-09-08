package com.zaze.apps.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Build
import com.google.gson.annotations.Expose
import com.zaze.apps.base.BaseApplication
import com.zaze.utils.AppUtil
import com.zaze.utils.SignaturesUtil.getSignatures
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import java.lang.Exception

/**
 * Description : 应用快捷信息(汇总最新的 Applicaiton 和 packageInfo)
 *
 * @author : ZAZE
 * @version : 2017-12-22 - 17:26
 */
data class AppShortcut(
    val appName: String? = null,
    val packageName: String = "",
    val versionCode: Long = 0,
    val versionName: String? = null,
    val sourceDir: String? = null,
    val uid: Int = 0,
    val flags: Int = 0,
    val signingInfo: String? = null,
    val isInstalled: Boolean = false,
    val firstInstallTime: Long = 0,
    val lastUpdateTime: Long = 0,
) {
    var isCopyEnable: Boolean = true


    @Transient
    @Expose(serialize = false, deserialize = false)
    var packageInfo: PackageInfo? = null
        get() {
            return when {
                field != null -> {
                    field
                }
                isInstalled -> {
                    field = AppUtil.getPackageInfo(BaseApplication.getInstance(), packageName)
                    field
                }
                else -> {
                    null
                }
            }
        }


    @Transient
    @Expose(serialize = false, deserialize = false)
    var applicationInfo: ApplicationInfo? = null
        get() {
            return when {
                field != null -> {
                    field
                }
                isInstalled -> {
                    field = AppUtil.getApplicationInfo(BaseApplication.getInstance(), packageName)
                    field
                }
                else -> {
                    null
                }
            }
        }

    companion object {
        fun empty(packageName: String): AppShortcut {
            return AppShortcut(packageName = packageName)
        }

        @JvmStatic
        fun transform(context: Context, packageInfo: PackageInfo): AppShortcut {

            val applicationInfo: ApplicationInfo? = packageInfo.applicationInfo
            val appShortcut = AppShortcut(
                packageName = packageInfo.packageName,
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode
                } else {
                    packageInfo.versionCode.toLong()
                },
                versionName = packageInfo.versionName,
                signingInfo = getSignatures(object : ContextWrapper(context) {
                    override fun getPackageName(): String {
                        return packageInfo.packageName
                    }
                }, "MD5"),
                appName = applicationInfo?.let {
                    context.packageManager.getApplicationLabel(it).toString()
                },
                isInstalled = applicationInfo != null,
                sourceDir = applicationInfo?.sourceDir,
                flags = applicationInfo?.flags ?: 0,
                uid = applicationInfo?.uid ?: 0,
                firstInstallTime = packageInfo.firstInstallTime,
                lastUpdateTime = packageInfo.lastUpdateTime,
            )
            appShortcut.packageInfo = packageInfo
            appShortcut.applicationInfo = applicationInfo
            ZLog.i(ZTag.TAG, "appShortcut: $appShortcut")
            return appShortcut
        }
    }

    fun isSystemApp(): Boolean {
        return flags and ApplicationInfo.FLAG_SYSTEM != 0
    }

    fun getAppIcon(): Bitmap {
        return ApplicationManager.getAppIconHasDefault(packageName)
    }

    fun getResource(): Resources? {
        return applicationInfo?.let {
            try {
                BaseApplication.getInstance().packageManager.getResourcesForApplication(it)
            } catch (e: Exception) {
                null
            }
        }
    }
}