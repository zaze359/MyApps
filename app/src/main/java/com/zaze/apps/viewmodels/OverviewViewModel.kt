package com.zaze.apps.viewmodels

import androidx.lifecycle.MutableLiveData
import com.zaze.apps.R
import com.zaze.apps.base.AbsViewModel
import com.zaze.apps.items.CardItem
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.apps.utils.SingleLiveEvent
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 15:36
 */
class OverviewViewModel : AbsViewModel() {
    val overviewListData = MutableLiveData<List<CardItem>>()
    val showAppsAction = SingleLiveEvent<Unit>()
    var installedApps = emptyList<AppShortcut>()

    fun loadOverview() {
        val list = ArrayList<CardItem>()
        installedApps = ApplicationManager.getInstalledApps().values.toList().sortedBy {
            it.lastUpdateTime
        }
        val allAppSize = installedApps.size
        val systemAppSize = installedApps.count {
            it.isSystemApp()
        }
        list.add(
            CardItem.Overview(
                title = "应用安装情况",
                content = "${allAppSize}个应用  ${allAppSize - systemAppSize}普通应用  ${systemAppSize}系统应用",
                iconRes = R.drawable.ic_baseline_assessment_24,
                actionName = "更多",
                doAction = {
                    showAppsAction.value = null
                }
            )
        )
        // --------------------------------------------------
        list.add(
            CardItem.AppsView(
                title = "最近更新应用",
                apps = installedApps.subList(0, 5),
                iconRes = R.drawable.ic_baseline_apps_24,
                doAction = {
                }
            )
        )
        overviewListData.postValue(list)
    }
}