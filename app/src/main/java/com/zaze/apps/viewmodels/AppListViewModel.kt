package com.zaze.apps.viewmodels

import android.app.Application
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.utils.FileUtil
import com.zaze.utils.TraceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * Description :
 * @author : ZAZE
 * @version : 2019-05-20 - 13:04
 */
//@HiltViewModel
class AppListViewModel constructor(application: Application) : AbsAndroidViewModel(application) {
    private val viewModelState = MutableStateFlow(AppViewModelState())
    val uiState = viewModelState.map(AppViewModelState::toUiState).stateIn(
        viewModelScope, SharingStarted.Eagerly, viewModelState.value.toUiState()
    )

    private var matchHistory = ""
    val appData = MutableStateFlow<List<AppShortcut>>(emptyList())


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
    fun loadData() {
//        loadApps(matchHistory)
        viewModelScope.launch(Dispatchers.IO) {
            loadAllApps()
        }
    }

    private suspend fun loadAllApps() {
        TraceHelper.beginSection("loadAllApps")
        viewModelState.update {
            it.copy(allApps = ApplicationManager.getInstallApps().values.toList())
        }
        TraceHelper.endSection("loadAllApps")
//
//            matchHistory = filter
//            val list = filterApp(ApplicationManager.getInstallApps().values, filter)
//            appData.value = list
    }


    fun searchApps(searchWords: String) {
        viewModelScope.launch(Dispatchers.Default) {
            viewModelState.update {
                it.copy(searchedApps = filterApp(it.allApps, searchWords))
            }
        }
    }

    private fun loadApps(filter: String) {
        viewModelScope.launch(Dispatchers.Default) {
            TraceHelper.beginSection("loadApps")
            matchHistory = filter
            val list = filterApp(ApplicationManager.getInstallApps().values, filter)
            TraceHelper.endSection("loadApps")
            appData.value = list
        }
    }

    // --------------------------------------------------
    fun extractApp(): Flow<String> {
        TraceHelper.beginSection("extractApp")
        return flowOf(appData.value)
            .filter {
                it.isNotEmpty()
            }.map { dataList ->
                FileUtil.deleteFile(extractPkgsFile)
                FileUtil.deleteFile(extractFile)
                FileUtil.deleteFile(jsonExtractFile)
                val pkgBuilder = StringBuilder()
                val xmlBuilder = StringBuilder()
                val jsonArray = JSONArray()
                for (entity in dataList) {
                    if (pkgBuilder.isNotEmpty()) {
                        pkgBuilder.append(",")
                    }
                    pkgBuilder.append("${entity.packageName}")
                    // --------------------------------------------------
                    if (xmlBuilder.isNotEmpty()) {
                        xmlBuilder.append("\n")
                    }
                    xmlBuilder.append("<item>${entity.packageName}</item><!--${entity.appName}-->")
                    //
                    val jsonObj = JSONObject()
                    jsonObj.put("name", entity.appName)
                    jsonObj.put("packageName", entity.packageName)
                    jsonArray.put(jsonObj)
                }
                FileUtil.writeToFile(extractPkgsFile, pkgBuilder.toString())
                FileUtil.writeToFile(extractFile, xmlBuilder.toString())
                FileUtil.writeToFile(jsonExtractFile, jsonArray.toString())
                TraceHelper.endSection("extractApp")
                baseDir
            }.flowOn(Dispatchers.IO)
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

    private fun filterApp(appList: Collection<AppShortcut>, matchStr: String): List<AppShortcut> {
        return appList.filter {
            matchStr.isEmpty()
                    || it.packageName.contains(matchStr, true)
                    || it.appName?.contains(matchStr, true) ?: false
        }.filter {
            !it.sourceDir.isNullOrEmpty()
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
}

private data class AppViewModelState(
    val allApps: List<AppShortcut> = emptyList(),
    val searchedApps: List<AppShortcut> = emptyList(),
) {
    fun toUiState(): AppUiState {
        return AppUiState.AppList(apps = allApps, searchedApps = searchedApps)
    }
}

sealed class AppUiState {
    data class AppList(
        val apps: List<AppShortcut>,
        val searchedApps: List<AppShortcut>
    ) : AppUiState()
}