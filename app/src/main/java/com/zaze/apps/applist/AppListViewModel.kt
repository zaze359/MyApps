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
import com.zaze.utils.FileUtil
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
    fun loadData(appFilter: AppFilter = AppFilter.ALL) {
        filterCondition = appFilter
        viewModelScope.launch(Dispatchers.Default) {
            loadApps(appFilter)
        }
    }

    private suspend fun loadApps(appFilter: AppFilter) {
        TraceHelper.beginSection("loadApps")
        val appApps = ApplicationManager.getInstallApps().values.toList()
        val resultApps = when (appFilter) {
            AppFilter.ALL -> {
                appApps
            }

            AppFilter.USER -> {
                appApps.filter {
                    !it.isSystemApp()
                }.toList()
            }

            AppFilter.SYSTEM -> {
                appApps.filter {
                    it.isSystemApp()
                }.toList()
            }

            AppFilter.FROZEN -> {
                appApps.filter {
                    !it.enable
                }.toList()
            }

            AppFilter.APK -> {
                withContext(Dispatchers.IO) {
                    emptyList()
                }
            }

            else -> {
                emptyList()
            }
        }
        viewModelState.update {
            it.copy(allApps = resultApps)
        }
        TraceHelper.endSection("loadApps")
    }

    fun searchApps(searchWords: String) {
        viewModelScope.launch(Dispatchers.Default) {
            viewModelState.update {
                it.copy(searchedApps = filterApp(it.allApps, searchWords))
            }
        }
    }

    // --------------------------------------------------
//    fun extractApp(): Flow<String> {
//        TraceHelper.beginSection("extractApp")
//        return flowOf(appData.value)
//            .filter {
//                it.isNotEmpty()
//            }.map { dataList ->
//                FileUtil.deleteFile(extractPkgsFile)
//                FileUtil.deleteFile(extractFile)
//                FileUtil.deleteFile(jsonExtractFile)
//                val pkgBuilder = StringBuilder()
//                val xmlBuilder = StringBuilder()
//                val jsonArray = JSONArray()
//                for (entity in dataList) {
//                    if (pkgBuilder.isNotEmpty()) {
//                        pkgBuilder.append(",")
//                    }
//                    pkgBuilder.append("${entity.packageName}")
//                    // --------------------------------------------------
//                    if (xmlBuilder.isNotEmpty()) {
//                        xmlBuilder.append("\n")
//                    }
//                    xmlBuilder.append("<item>${entity.packageName}</item><!--${entity.appName}-->")
//                    //
//                    val jsonObj = JSONObject()
//                    jsonObj.put("name", entity.appName)
//                    jsonObj.put("packageName", entity.packageName)
//                    jsonArray.put(jsonObj)
//                }
//                FileUtil.writeToFile(extractPkgsFile, pkgBuilder.toString())
//                FileUtil.writeToFile(extractFile, xmlBuilder.toString())
//                FileUtil.writeToFile(jsonExtractFile, jsonArray.toString())
//                TraceHelper.endSection("extractApp")
//                baseDir
//            }.flowOn(Dispatchers.IO)
//    }

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

    private fun filterApp(appList: Collection<AppShortcut>, matchStr: String): List<AppShortcut> {
        return appList.filter {
            matchStr.isEmpty()
                    || it.packageName.contains(matchStr, true)
                    || it.appName?.contains(matchStr, true) ?: false
        }.filter {
            !it.applicationInfo?.sourceDir.isNullOrEmpty()
        }.sortedBy {
            it.appName
        }
    }

    // --------------------------------------------------
    private fun initEntity(appShortcut: AppShortcut?): AppShortcut? {
        return appShortcut?.also {
            val packageName = appShortcut.packageName
            if (appShortcut.isInstalled) {
                FileUtil.writeToFile(
                    existsFile,
                    "<item>$packageName</item><!--${appShortcut.appName}-->\n",
                    true
                )
            } else {
                FileUtil.writeToFile(unExistsFile, "<item>$packageName</item>\n", true)
            }
            FileUtil.writeToFile(
                allFile,
                "<item>$packageName</item><!--${appShortcut.appName}-->\n",
                true
            )
        }
    }

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
    val allApps: List<AppShortcut> = emptyList(),
    val searchedApps: List<AppShortcut> = emptyList(),
    /** 排序类型*/
    val sortType: AppSort = AppSort.Name,

    /** 是否反向排序 */
    val reverse: Boolean = false
) {
    fun toUiState(): AppUiState {
        val resultApp = when (sortType) {
            AppSort.Name -> {
                // 反向: z-a；正向: a-z
                if (reverse) allApps.sortedByDescending { it.appName } else allApps.sortedBy { it.appName }
            }

            AppSort.Size -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) allApps.sortedBy { it.apkSize } else allApps.sortedByDescending { it.apkSize }
            }

            AppSort.InstallTime -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) allApps.sortedBy { it.firstInstallTime } else allApps.sortedByDescending { it.firstInstallTime }
            }

            AppSort.UpdateTime -> {
                // 反向: 小 -> 大 ;正向:  大 -> 小
                if (reverse) allApps.sortedBy { it.lastUpdateTime } else allApps.sortedByDescending { it.lastUpdateTime }
            }
        }
        ZLog.i(ZTag.TAG, "resultApp: ${resultApp.size}")
        repeat(10.coerceAtMost(resultApp.size)) {
            ZLog.i(ZTag.TAG, "app: ${resultApp[it]}")
        }
        return AppUiState.AppList(
            apps = resultApp,
            searchedApps = searchedApps,
            sortType = sortType
        )
    }
}

sealed class AppUiState {
    data class AppList(
        val apps: List<AppShortcut>,
        val searchedApps: List<AppShortcut>,
        val sortType: AppSort
    ) : AppUiState()
}