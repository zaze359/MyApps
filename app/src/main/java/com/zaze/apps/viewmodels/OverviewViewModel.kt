package com.zaze.apps.viewmodels

import android.content.Intent
import android.provider.Settings
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
import com.zaze.utils.TraceHelper
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 15:36
 */
class OverviewViewModel : AbsViewModel() {
    val showAppsAction = SingleLiveEvent<Unit>()

    val requestAppUsagePermissionAction = SingleLiveEvent<Unit>()
    val settingsAction = SingleLiveEvent<Intent>()

    fun loadOverview(): Flow<List<Card>> {
        return flow {
            this.emit(loadCards())
        }.flowOn(Dispatchers.IO)
    }

    private fun loadCards(): List<Card> {
        val list = ArrayList<Card>()
        TraceHelper.beginSection("loadOverview")
        val installedApps =
            ApplicationManager.getInstallApps().values.toList().sortedByDescending {
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
                content = "${allAppSize - systemAppSize}个普通应用  ${systemAppSize}个系统应用",
                iconRes = R.drawable.ic_baseline_assessment_24,
                doAction = {
                    showAppsAction.action()
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
            val apps = installedApps.subList(0, 5)
            list.add(
                Card.Apps(
                    title = "最近更新TOP${apps.size}",
                    apps = apps,
                    iconRes = R.drawable.ic_baseline_apps_24,
                )
            )
            // --------------------------------------------------
            val unit = StorageLoader.StorageInfo.UNIT * StorageLoader.StorageInfo.UNIT
            val storageInfo = StorageLoader.loadStorageStats(App.getInstance())
            val usedSpace = ((storageInfo.totalBytes - storageInfo.freeBytes) / unit).toInt()
            val appsSpace = 0
            // --------------------------------------------------
            val transFun = { it: Int ->
                DescriptionUtil.toByteUnit(it.toLong() * unit)
            }
            val max = (storageInfo.totalBytes / unit).toInt()
            list.add(
                Card.Progress(
                    title = "存储情况",
                    max = max,
                    progress = usedSpace,
                    trans = transFun,
//                    subProgresses = listOf(
//                        Card.Progress.Sub(
//                            tag = "Apps",
//                            trans = transFun,
//                            max = max,
//                            progress = appsSpace
//                        ),
//                        Card.Progress.Sub(
//                            tag = "Others",
//                            trans = transFun,
//                            max = max,
//                            progress = usedSpace - appsSpace
//                        ),
//                    ),
                    doAction = {
                        settingsAction.postValue(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS))
                    }
                )
            )
        }
        TraceHelper.endSection("loadOverview")
        return list
    }
}