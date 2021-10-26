package com.zaze.apps.viewmodels

import android.app.Application
import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.utils.AppUtil
import com.zaze.utils.FileUtil
import com.zaze.utils.TraceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Description :
 * @author : ZAZE
 * @version : 2019-05-20 - 13:04
 */
//@HiltViewModel
class AppListViewModel constructor(application: Application) : AbsAndroidViewModel(application) {
    private var matchHistory = ""
    val apkDir = "${application.getExternalFilesDir(null)}/zaze/apk"
    val baseDir = "${application.getExternalFilesDir(null)}/zaze/${Build.MODEL}"

    //
    val existsFile = "$baseDir/exists.xml"
    val unExistsFile = "$baseDir/unExists.xml"
    val allFile = "$baseDir/all.xml"

    //
    val extractPkgsFile = "$baseDir/extract_pkgs.txt"
    val extractFile = "$baseDir/extract.xml"
    val jsonExtractFile = "$baseDir/jsonExtract.xml"

    val packageSet = HashMap<String, AppShortcut>()
    val appData = MutableLiveData<List<AppShortcut>>()

    // --------------------------------------------------

    fun loadAppList() {
        viewModelScope.launch(Dispatchers.IO) {
            if (isLoading()) {
                return@launch
            }
            TraceHelper.beginSection("loadAppList")
            dragLoading.postValue(true)
            // --------------------------------------------------
            packageSet.clear()
            // --------------------------------------------------
            FileUtil.deleteFile(baseDir)
//                FileUtil.deleteFile(unExistsFile)
//                FileUtil.deleteFile(extractFile)
//                FileUtil.deleteFile(allFile)
            // --------------------------------------------------
            ApplicationManager.getInstallApps().filter {
//                it.isSystemApp()
                true
            }.let {
                packageSet.putAll(it)
            }
            // --------------------------------------------------
            matchAppAndShow(appList = packageSet.values)
            dragLoading.postValue(false)
            TraceHelper.endSection("loadAppList")
        }
    }

    // --------------------------------------------------
    fun extractApp() {
        if (!isLoading()) {
            dataLoading.postValue(true)
//            showProgress("正在提前数据....")
//            Observable.fromCallable {
//                val dataList = appData.get()
//                if (dataList != null) {
//                    FileUtil.deleteFile(extractPkgsFile)
//                    FileUtil.deleteFile(extractFile)
//                    FileUtil.deleteFile(jsonExtractFile)
//                    val pkgBuilder = StringBuilder()
//                    val xmlBuilder = StringBuilder()
//                    val jsonArray = JSONArray()
//                    for (entity in dataList) {
//                        if (pkgBuilder.isNotEmpty()) {
//                            pkgBuilder.append(",")
//                        }
//                        pkgBuilder.append("${entity.packageName}")
//                        // --------------------------------------------------
//                        if (xmlBuilder.isNotEmpty()) {
//                            xmlBuilder.append("\n")
//                        }
//                        xmlBuilder.append("<item>${entity.packageName}</item><!--${entity.name}-->")
//                        //
//                        val jsonObj = JSONObject()
//                        jsonObj.put("name", entity.name)
//                        jsonObj.put("packageName", entity.packageName)
//                        jsonArray.put(jsonObj)
//                    }
//                    FileUtil.writeToFile(extractPkgsFile, pkgBuilder.toString())
//                    FileUtil.writeToFile(extractFile, xmlBuilder.toString())
//                    FileUtil.writeToFile(jsonExtractFile, jsonArray.toString())
//                }
//            }.subscribeOn(ThreadPlugins.ioScheduler())
//                .doFinally {
//                    dataLoading.set(false)
//                    hideProgress()
//                }
//                .subscribe(MyObserver(compositeDisposable))
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

    fun filterApp(matchStr: String) {
        viewModelScope.launch {
            matchAppAndShow(matchStr, packageSet.values)
        }
    }

    private suspend fun matchAppAndShow(
        matchStr: String = matchHistory,
        appList: Collection<AppShortcut>
    ) {
        this.matchHistory = matchStr
        val apps = withContext(Dispatchers.Default) {
            appList.filter {
                matchStr.isEmpty()
                        || it.packageName.contains(matchStr, true)
                        || it.appName?.contains(matchStr, true) ?: false
            }.filter {
                !it.sourceDir.isNullOrEmpty()
            }.sortedBy {
                it.appName
            }
        }
        appData.postValue(apps)
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
}