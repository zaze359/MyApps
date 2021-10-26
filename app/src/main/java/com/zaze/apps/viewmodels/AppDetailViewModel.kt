package com.zaze.apps.viewmodels

import android.os.Build
import com.zaze.apps.base.AbsViewModel
import com.zaze.apps.data.AppDetail
import com.zaze.apps.data.AppDetailItem
import com.zaze.apps.utils.ApplicationManager
import com.zaze.utils.date.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-21 - 15:57
 */
class AppDetailViewModel : AbsViewModel() {

    fun loadAppDetail(packageName: String): Flow<AppDetail> {
        return flow {
            emit(ApplicationManager.getAppShortcut(packageName))
        }.map { app ->
            val appDetailItems = ArrayList<AppDetailItem>()
            val appDirs = ArrayList<AppDetailItem>()
            // --------------------------------------------------
            appDetailItems.add(AppDetailItem("包名", app.packageName))
            appDetailItems.add(AppDetailItem("版本号", app.versionCode.toString()))
            appDetailItems.add(
                AppDetailItem(
                    "安装时间",
                    DateUtil.timeMillisToString(app.firstInstallTime, "yyyy-MM-dd HH:mm:ss")
                )
            )
            appDetailItems.add(
                AppDetailItem(
                    "最近更新",
                    DateUtil.timeMillisToString(app.lastUpdateTime, "yyyy-MM-dd HH:mm:ss")
                )
            )
            appDetailItems.add(AppDetailItem("UID", app.uid.toString()))
            app.applicationInfo?.let {
                appDetailItems.add(
                    AppDetailItem("目标 SDK", it.targetSdkVersion.toString())
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    appDetailItems.add(
                        AppDetailItem("最低 SDK", it.minSdkVersion.toString())
                    )
                }
                // --------------------------------------------------
                appDirs.add(AppDetailItem("APK路径", it.sourceDir))
                appDirs.add(AppDetailItem("Data", it.dataDir))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    appDirs.add(AppDetailItem("ProtectedData", it.deviceProtectedDataDir))
                }
            }
            AppDetail(
                appName = app.appName,
                appVersion = app.versionName,
                appIcon = app.appIcon,
                appDetailItems = appDetailItems,
                appDirs = appDirs
            )
        }.flowOn(Dispatchers.Default)
    }
}