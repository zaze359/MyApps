package com.zaze.apps.viewmodels

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import com.zaze.apps.App
import com.zaze.apps.R
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.data.Card
import com.zaze.apps.ext.action
import com.zaze.apps.utils.AppUsageHelper
import com.zaze.apps.utils.ApplicationManager
import com.zaze.core.common.utils.SingleLiveEvent
import com.zaze.utils.DescriptionUtil
import com.zaze.utils.TraceHelper
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import com.zaze.utils.storage.StorageInfo
import com.zaze.utils.storage.StorageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-02 - 15:36
 */
class OverviewViewModel(application: Application) : AbsAndroidViewModel(application) {
    val showAppsAction = SingleLiveEvent<Unit>()
    val requestAppUsagePermissionAction = SingleLiveEvent<Unit>()
    val settingsAction = SingleLiveEvent<Intent>()

    var isFirstLoad = true

    //    private val viewModelState = MutableStateFlow(AppViewModelState())
//    val uiState = viewModelState.map(AppViewModelState::toUiState).stateIn(
//        viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState()
//    )
    private val viewModelState = MutableStateFlow(OverviewViewModelState())
    val uiState = viewModelState.map(OverviewViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState())

    fun loadOverview() {
        viewModelScope.launch(Dispatchers.Default) {
            TraceHelper.beginSection("loadOverview")
            val loadAppsAsync = async {
                ApplicationManager.getInstallApps(application).values.toList().sortedByDescending {
                    it.lastUpdateTime
                }
            }
            async {
                viewModelState.update {
                    it.copy(usageAccessPermissionCard = if (!AppUsageHelper.checkAppUsagePermission(
                            application
                        )
                    ) {
                        ZLog.i(ZTag.TAG, "checkAppUsagePermission: denied")
                        Card.Overview(
                            title = "Usage access denied",
                            content = "授予使用情况访问权限已启用更多功能",
                            iconRes = R.drawable.ic_assessment,
                            actionName = "GO", doAction = {
                                requestAppUsagePermissionAction.action()
                            }
                        )
                    } else {
                        null
                    })
                }
            }.start()
            //
            val installedApps = loadAppsAsync.await()
            val allAppSize = installedApps.size
            val systemAppSize = installedApps.count {
                it.isSystemApp()
            }
            val appsCard = Card.Overview(
                title = "应用统计: ${allAppSize}个",
                content = "${allAppSize - systemAppSize}个用户应用  ${systemAppSize}个系统应用",
                iconRes = R.drawable.ic_assessment,
                doAction = {
                    showAppsAction.action()
                }
            )
            // --------------------------------------------------
            val newUpdateAppCard = Card.Apps(
                title = "最近更新和安装的应用",
                apps = installedApps.subList(0, 10),
                iconRes = R.drawable.ic_apps,
            )

            // --------------------------------------------------
            val storageCardAsync = async {
                val unit = StorageInfo.UNIT * StorageInfo.UNIT
                val storageInfo = StorageLoader.loadStorageStats(App.getInstance())
                val usedSpace = ((storageInfo.totalBytes - storageInfo.freeBytes) / unit).toInt()
//            val appsSpace = 0
                // --------------------------------------------------
                val transFun = { it: Int ->
                    DescriptionUtil.toByteUnit(it.toLong() * unit)
                }
                val max = (storageInfo.totalBytes / unit).toInt()
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
            }
            viewModelState.update {
                val new = it.copy(
                    appsCard = appsCard,
                    storageCard = storageCardAsync.await(),
                    newUpdateAppCard = newUpdateAppCard,
                    moveToTop = isFirstLoad
                )
                isFirstLoad = false
                new
            }
            TraceHelper.endSection("loadOverview")
        }
    }

//
//    fun loadOverview(): Flow<List<Card>> {
//        return flow {
//            this.emit(loadCards())
//        }.flowOn(Dispatchers.Default)
//    }
//
//    private fun loadCards(): List<Card> {
//        ZLog.i(ZTag.TAG, "loadCards: ${Thread.currentThread()}")
//        val list = ArrayList<Card>()
//        TraceHelper.beginSection("loadOverview")
//        val installedApps =
//            ApplicationManager.getInstallApps(application).values.toList().sortedByDescending {
//                it.lastUpdateTime
//            }
//        // --------------------------------------------------
//        val allAppSize = installedApps.size
//        val systemAppSize = installedApps.count {
//            it.isSystemApp()
//        }
//        // --------------------------------------------------
//        list.add(
//            Card.Overview(
//                title = "应用统计: ${allAppSize}个",
//                content = "${allAppSize - systemAppSize}个用户应用  ${systemAppSize}个系统应用",
//                iconRes = R.drawable.ic_assessment,
//                doAction = {
//                    showAppsAction.action()
//                }
//            )
//        )
//        if (!AppUsageHelper.checkAppUsagePermission(App.getInstance())) {
//            ZLog.i(ZTag.TAG, "checkAppUsagePermission: denied")
//            list.add(
//                Card.Overview(
//                    title = "Usage access denied",
//                    content = "授予使用情况访问权限已启用更多功能",
//                    iconRes = R.drawable.ic_assessment,
//                    actionName = "GO", doAction = {
//                        requestAppUsagePermissionAction.action()
//                    }
//                )
//            )
//        } else {
//        }
//        TraceHelper.endSection("loadOverview")
//        return list
//    }
}

private data class OverviewViewModelState(
    val moveToTop: Boolean = false,
    val appsCard: Card? = null,
    val usageAccessPermissionCard: Card? = null,
    val newUpdateAppCard: Card? = null,
    val storageCard: Card? = null
) {
    fun toUiState(): OverviewUiState {
        val overviews = ArrayList<Card>()
        fillCard(overviews, appsCard)
        fillCard(overviews, usageAccessPermissionCard)
        fillCard(overviews, newUpdateAppCard)
        fillCard(overviews, storageCard)
        return OverviewUiState(cards = overviews, moveToTop = moveToTop)
    }

    fun fillCard(overviews: MutableList<Card>, card: Card?) {
        card?.let {
            overviews.add(it)
        }
    }
}

data class OverviewUiState(val cards: List<Card>, val moveToTop: Boolean)