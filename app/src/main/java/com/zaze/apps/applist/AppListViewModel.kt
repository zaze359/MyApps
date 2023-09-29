package com.zaze.apps.applist

import android.app.Application
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.data.AppFilter
import com.zaze.apps.data.AppSort
import com.zaze.apps.utils.AppChangeListener
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.utils.TraceHelper
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Description :
 * @author : ZAZE
 * @version : 2019-05-20 - 13:04
 */
@HiltViewModel
class AppListViewModel @Inject constructor(application: Application) :
    AbsAndroidViewModel(application), AppChangeListener {

    private val viewModelState = MutableStateFlow(AppViewModelState())
    val uiState = viewModelState.map(AppViewModelState::toUiState).stateIn(
        viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState()
    )

    init {
        ApplicationManager.addAppObserver(this)
    }

    // --------------------------------------------------
    private var allApps: List<AppShortcut> = emptyList()

    /** 过滤条件 */
    private var filterCondition: AppFilter = AppFilter.ALL

    // --------------------------------------------------
    val apkDir = "${application.getExternalFilesDir(null)}/apk"
    val baseDir = "${application.getExternalFilesDir(null)}/${Build.MODEL}"

    //
    val existsFile = "$baseDir/exists.xml"
    val unExistsFile = "$baseDir/unExists.xml"
    val allFile = "$baseDir/all.xml"

    //
    val extractPkgsFile = "$baseDir/extract_pkgs.txt"
    val extractFile = "$baseDir/extract.xml"
    val jsonExtractFile = "$baseDir/jsonExtract.xml"

    // --------------------------------------------------
    fun loadData(appFilter: AppFilter = filterCondition) {
        ZLog.i(ZTag.TAG, "loadData appFilter: $appFilter")
        filterCondition = appFilter
        viewModelScope.launch(Dispatchers.Default) {
            loadApps(appFilter)
        }
    }

    private suspend fun loadApps(appFilter: AppFilter) {
        TraceHelper.beginSection("loadApps")
        allApps = ApplicationManager.getInstallApps(application).values.toList()
        val resultApps = when (appFilter) {
            AppFilter.ALL -> {
                allApps
            }

            AppFilter.USER -> {
                allApps.filter {
                    !it.isSystemApp()
                }.toList()
            }

            AppFilter.SYSTEM -> {
                allApps.filter {
                    it.isSystemApp()
                }.toList()
            }

            AppFilter.FROZEN -> {
                allApps.filter {
                    !it.enable
                }.toList()
            }

            AppFilter.APK -> {
                withContext(Dispatchers.IO) {
                    emptyList()
                }
            }
        }
        viewModelState.update {
            it.copy(apps = resultApps, appFilter = appFilter)
        }
        TraceHelper.endSection("loadApps")
    }

    fun searchApps(searchWords: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = allApps.filter {
                searchWords.isEmpty()
                        || it.packageName.contains(searchWords, true)
                        || it.appName?.contains(searchWords, true) ?: false
            }.filter {
                !it.applicationInfo?.sourceDir.isNullOrEmpty()
            }.sortedBy {
                it.appName
            }
            viewModelState.update {
                it.copy(searchedApps = result)
            }
        }
    }

    // ------------------------------------------------------
    fun loadSdcardApk() {
//        if (!isLoading()) {
//            showProgress("加载apk")
//            dataLoading.set(true)
//            Observable.fromCallable {
//                val showList = ArrayList<AppShortcut>()
//                FileUtil.searchFileBySuffix(apkDir, "apk", true).forEach { file ->
//                    initEntity(ApplicationManager.getAppShortcutFormApk(file.absolutePath))?.let {
//                        it.isCopyEnable = false
//                        showList.add(it)
//                    }
//                }
//                showList
//            }.subscribeOn(ThreadPlugins.ioScheduler())
//                .map {
//                    matchApp(it)
//                }
//                .doFinally {
//                    dataLoading.set(false)
//                    hideProgress()
//                }
//                .subscribe(MyObserver(compositeDisposable))
//        }
    }

    // --------------------------------------------------
    fun clearSearchInfo() {
        viewModelState.update {
            it.copy(searchedApps = emptyList())
        }
    }


    override fun afterAppAdded(packageName: String) {
        onAppChange(packageName)
    }

    override fun afterAppReplaced(packageName: String) {
        onAppChange(packageName)
    }

    override fun afterAppRemoved(packageName: String) {
        onAppChange(packageName)
    }

    private fun onAppChange(packageName: String) {
        ZLog.i(ZTag.TAG, "onAppChange: $packageName")
        when (filterCondition) {
            AppFilter.ALL, AppFilter.USER, AppFilter.SYSTEM, AppFilter.FROZEN -> {
                loadData(filterCondition)
            }

            else -> {}
        }
    }

    override fun onCleared() {
        super.onCleared()
        ApplicationManager.removeAppObserver(this)
    }

    fun sortBy(sortType: AppSort) {
        viewModelScope.launch(Dispatchers.Default) {
            viewModelState.update {
                it.copy(sortType = sortType)
            }
        }
    }

}

private data class AppViewModelState(
    val apps: List<AppShortcut> = emptyList(),
    val searchedApps: List<AppShortcut> = emptyList(),
    /** 过滤条件 */
    val appFilter: AppFilter = AppFilter.ALL,

    /** 排序类型*/
    val sortType: AppSort = AppSort.Name,

    /** 是否反向排序 */
    val reverse: Boolean = false
) {

    companion object {
        private val appFilterList = listOf(
            AppFilter.ALL,
            AppFilter.USER,
            AppFilter.SYSTEM,
            AppFilter.FROZEN,
            AppFilter.APK,
        )
    }

    fun toUiState(): AppUiState {
        val resultApp = when (sortType) {
            AppSort.Name -> {
                // 反向: z-a；正向: a-z
                if (reverse) apps.sortedByDescending { it.appName } else apps.sortedBy { it.appName }
            }

            AppSort.Size -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) apps.sortedBy { it.apkSize } else apps.sortedByDescending { it.apkSize }
            }

            AppSort.InstallTime -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) apps.sortedBy { it.firstInstallTime } else apps.sortedByDescending { it.firstInstallTime }
            }

            AppSort.UpdateTime -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) apps.sortedBy { it.lastUpdateTime } else apps.sortedByDescending { it.lastUpdateTime }
            }
        }
        return AppUiState.AppList(
            apps = sortApps(),
            searchedApps = searchedApps,
            appFilterList = appFilterList,
            appFilter = appFilter,
            sortType = sortType
        )
    }

    private fun sortApps(): List<AppShortcut> {
        return when (sortType) {
            AppSort.Name -> {
                // 反向: z-a；正向: a-z
                if (reverse) apps.sortedByDescending { it.appName } else apps.sortedBy { it.appName }
            }

            AppSort.Size -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) apps.sortedBy { it.apkSize } else apps.sortedByDescending { it.apkSize }
            }

            AppSort.InstallTime -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) apps.sortedBy { it.firstInstallTime } else apps.sortedByDescending { it.firstInstallTime }
            }

            AppSort.UpdateTime -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) apps.sortedBy { it.lastUpdateTime } else apps.sortedByDescending { it.lastUpdateTime }
            }
        }
    }


}

sealed class AppUiState {
    data class AppList(
        val apps: List<AppShortcut>,
        val searchedApps: List<AppShortcut>,
        val appFilterList: List<AppFilter>,
        val appFilter: AppFilter,
        val sortType: AppSort
    ) : AppUiState()
}