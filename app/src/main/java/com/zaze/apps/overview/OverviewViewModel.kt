package com.zaze.apps.overview

import androidx.lifecycle.MutableLiveData
import com.zaze.apps.R
import com.zaze.apps.base.AbsViewModel
import com.zaze.apps.items.CardItem
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

    fun loadOverview() {
        val list = ArrayList<CardItem>()
        val apps = ApplicationManager.getInstalledApps().values
        val allAppSize = apps.size
        val systemAppSize = apps.count {
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
            CardItem.Overview(
                title = "最近更新应用",
                content = "xxxxx",
                iconRes = R.drawable.ic_baseline_apps_24,
                actionName = "click"
            )
        )
        ZLog.i(ZTag.TAG, "list: ${list.size} ")
        overviewListData.postValue(list)
    }
}