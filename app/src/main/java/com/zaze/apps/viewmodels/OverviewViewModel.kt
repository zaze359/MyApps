package com.zaze.apps.viewmodels

import android.app.Application
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import com.zaze.apps.App
import com.zaze.apps.R
import com.zaze.apps.core.base.AbsAndroidViewModel
import com.zaze.apps.data.Card
import com.zaze.core.common.utils.app.AppShortcut
import com.zaze.core.common.utils.AppUsageHelper
import com.zaze.core.common.utils.app.ApplicationManager
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
    private var isFirstLoad = true

    private val viewModelState = MutableStateFlow(OverviewViewModelState())
    val uiState = viewModelState.map(OverviewViewModelState::toUiState)
        .stateIn(viewModelScope, SharingStarted.Eagerly, OverviewUiState.NULL)


    //    fun loadSync() {
//        val installedApps = ApplicationManager.getInstallApps(application).values.toList().sortedByDescending {
//            it.lastUpdateTime
//        }
//        viewModelState.update {
//            val new = it.copy(
//                appsCard = loadAppUsage(),
//                usageAccessPermissionCard = loadAppUsage(),
//                storageCard = loadStorageInfo(installedApps),
//                newUpdateAppCard = loadNews(installedApps),
//            )
//            new
//        }
//    }
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
                    it.copy(usageAccessPermissionCard = loadAppUsage())
                }
            }.start()
            // --------------------------------------------------
            val installedApps = loadAppsAsync.await()
            arrayListOf(
                async {
                    viewModelState.update {
                        it.copy(appsCard = loadAppCount(installedApps))
                    }
                },
                // --------------------------------------------------
                async {
                    viewModelState.update {
                        it.copy(newUpdateAppCard = loadNews(installedApps))
                    }
                },
                //
                async {
                    viewModelState.update {
                        it.copy(
                            storageCard = loadStorageInfo(installedApps),
                        )
                    }
                }
            ).forEach {
                it.await()
            }
            // --------------------------------------------------
            viewModelState.update {
                val new = it.copy(
                    moveToTop = isFirstLoad
                )
                isFirstLoad = false
                new
            }
            TraceHelper.endSection("loadOverview")
        }
    }

    /**
     * 应用使用情况
     */
    private fun loadAppUsage(): Card? {
        return if (!AppUsageHelper.checkAppUsagePermission(
                application
            )
        ) {
            ZLog.i(ZTag.TAG, "checkAppUsagePermission: denied")
            Card.Overview(
                title = "Usage access denied",
                content = "授予使用情况访问权限已启用更多功能",
                iconRes = R.drawable.ic_assessment,
                actionName = "GO", doAction = {
                    addAction(OverviewAction.RequestAppUsagePermission)
                }
            )
        } else {
            null
        }
    }

    /**
     * 应用数量统计
     */
    private fun loadAppCount(apps: List<AppShortcut>): Card {
        val allAppSize = apps.size
        val systemAppSize = apps.count {
            it.isSystemApp()
        }
        return Card.Overview(
            title = "应用统计: ${allAppSize}个",
            content = "${allAppSize - systemAppSize}个用户应用  ${systemAppSize}个系统应用",
            iconRes = R.drawable.ic_assessment,
            doAction = {
                addAction(OverviewAction.JumpToAppListFragment)
            }
        )
    }

    /**
     * 最近更新/安装应用
     */
    private fun loadNews(apps: List<AppShortcut>): Card {
        return Card.Apps(
            title = "最近更新和安装的应用",
            apps = apps.subList(0, 10),
            iconRes = R.drawable.ic_apps,
        )
    }

    private fun loadStorageInfo(apps: List<AppShortcut>): Card {
        val unit = StorageInfo.UNIT * StorageInfo.UNIT
        val storageInfo = StorageLoader.loadStorageStats(App.getInstance())
        val usedSpace = ((storageInfo.totalBytes - storageInfo.freeBytes) / unit).toInt()
//            val appsSpace = 0
        // --------------------------------------------------
        val transFun = { it: Int ->
            DescriptionUtil.toByteUnit(it.toLong() * unit)
        }
        val max = (storageInfo.totalBytes / unit).toInt()
        return Card.Progress(
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
                addAction(OverviewAction.StartActivity(Intent(Settings.ACTION_INTERNAL_STORAGE_SETTINGS)))
            }
        )
    }

    private fun addAction(action: OverviewAction) {
        viewModelState.update {
            it.copy(
                action = action
            )
        }
    }

    fun actionHandled(action: OverviewAction) {
        if (action == OverviewAction.None) {
            return
        }
        viewModelState.update {
            it.copy(
                action = OverviewAction.None
            )
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
    val storageCard: Card? = null,
    val action: OverviewAction = OverviewAction.None
) {
    fun toUiState(): OverviewUiState {
        val overviews = ArrayList<Card>()
        fillCard(overviews, appsCard)
        fillCard(overviews, usageAccessPermissionCard)
        fillCard(overviews, newUpdateAppCard)
        fillCard(overviews, storageCard)
        return OverviewUiState.HasData(
            cards = overviews,
            moveToTop = moveToTop,
            action = action
        )
    }

    fun fillCard(overviews: MutableList<Card>, card: Card?) {
        card?.let {
            overviews.add(it)
        }
    }
}


sealed class OverviewUiState {
    object NULL : OverviewUiState()
    data class HasData(
        val cards: List<Card>,
        val moveToTop: Boolean,
        val action: OverviewAction
    ) : OverviewUiState()
}

sealed class OverviewAction {
    object None : OverviewAction()
    data class StartActivity(val intent: Intent) : OverviewAction()
    object JumpToAppListFragment : OverviewAction()
    object RequestAppUsagePermission : OverviewAction()
}