package com.zaze.apps.viewmodels

import android.os.Build
import androidx.lifecycle.MutableLiveData
import com.zaze.apps.App
import com.zaze.apps.R
import com.zaze.apps.base.AbsViewModel
import com.zaze.apps.data.Card
import com.zaze.apps.ext.action
import com.zaze.apps.utils.AppUsageHelper
import com.zaze.apps.utils.ApplicationManager
import com.zaze.apps.utils.SingleLiveEvent
import com.zaze.utils.DescriptionUtil
import com.zaze.utils.StorageLoader
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 15:36
 */
class OverviewViewModel : AbsViewModel() {
    val overviewListData = MutableLiveData<List<Card>>()
    val showAppsAction = SingleLiveEvent<Unit>()

    val requestAppUsagePermissionAction = SingleLiveEvent<Unit>()

//    var installedApps = emptyList<AppShortcut>()

    suspend fun loadOverview() {
        val list = ArrayList<Card>()
        val installedApps =
            ApplicationManager.getInstalledApps().values.toList().sortedByDescending {
                it.lastUpdateTime
            }
        // --------------------------------------------------
        val allAppSize = installedApps.size
        val systemAppSize = installedApps.count {
            it.isSystemApp()
        }
        // --------------------------------------------------
        list.add(
            Card.Overview(
                title = "应用统计: ${allAppSize}个",
                content = "${allAppSize - systemAppSize}普通应用  ${systemAppSize}系统应用",
                iconRes = R.drawable.ic_baseline_assessment_24,
                doAction = {
                    showAppsAction.value = null
                }
            )
        )
        if (!AppUsageHelper.checkAppUsagePermission(App.getInstance())) {
            ZLog.i(ZTag.TAG, "checkAppUsagePermission: denied")
            list.add(
                Card.Overview(
                    title = "Usage access denied",
                    content = "授予使用情况访问权限已启用更多功能",
                    iconRes = R.drawable.ic_baseline_assessment_24,
                    actionName = "GO", doAction = {
                        requestAppUsagePermissionAction.action()
                    }
                )
            )
        } else {
            // --------------------------------------------------
            val apps = installedApps.subList(0, 10)
            list.add(
                Card.Apps(
                    title = "最近更新TOP${apps.size}",
                    apps = apps,
                    iconRes = R.drawable.ic_baseline_apps_24,
                    doAction = {
                    }
                )
            )
            // --------------------------------------------------
            installedApps.forEach {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.queryStorageStats()
                }
            }
            val unit = 1024 * 1024
            val storageInfo = StorageLoader.loadStorageStats(App.getInstance())
            list.add(
                Card.Progress(
                    title = "存储情况",
                    max = (storageInfo.totalBytes / unit).toInt(),
                    progresses = listOf(
                        Card.Progress.Item(
                            tag = "空闲",
                            progress = ((storageInfo.totalBytes - storageInfo.freeBytes) / unit).toInt()
                        ),
                        Card.Progress.Item(tag = "Apps", progress = 1000),
                    ),
                    trans = {
                        DescriptionUtil.toByteUnit(it.toLong() * unit)
                    },
                    doAction = {
                    }
                )
            )
        }
        withContext(Dispatchers.Main) {
            overviewListData.value = list
        }
    }
}