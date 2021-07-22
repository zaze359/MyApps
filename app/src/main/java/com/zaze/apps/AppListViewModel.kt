package com.zaze.apps

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Build
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.base.BaseApplication
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.apps.utils.TraceHelper
import com.zaze.utils.AppUtil
import com.zaze.utils.FileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

/**
 * Description :
 * @author : ZAZE
 * @version : 2019-05-20 - 13:04
 */
class AppListViewModel(application: Application) : AbsAndroidViewModel(application) {
    private var matchStr = ""
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
    private fun loadAllInstallApp(
        appList: List<ApplicationInfo>,
        packageSet: HashMap<String, AppShortcut>
    ) {
        appList.forEach {
            packageSet[it.packageName] = ApplicationManager.getAppShortcut(it.packageName)
        }
    }

    private fun loadSystemApp(
        appList: List<ApplicationInfo>,
        packageSet: HashMap<String, AppShortcut>
    ) {
        appList.filter { it.flags and ApplicationInfo.FLAG_SYSTEM > 0 }
            .forEach {
                packageSet[it.packageName] = ApplicationManager.getAppShortcut(it.packageName)
            }
    }

    private fun loadUnSystemApp(
        appList: List<ApplicationInfo>,
        packageSet: HashMap<String, AppShortcut>
    ) {
        appList.filter { it.flags and ApplicationInfo.FLAG_SYSTEM <= 0 }
            .forEach {
                packageSet[it.packageName] = ApplicationManager.getAppShortcut(it.packageName)
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

    private fun show(apps: MutableCollection<AppShortcut>) {
        appData.postValue(apps.filter {
            !TextUtils.isEmpty(it.sourceDir)
        })
    }

    fun loadAppList() {
        viewModelScope.launch {
            if (isLoading()) {
                return@launch
            }
            TraceHelper.beginSection("loadAppList")
            dataLoading.postValue(true)
            // --------------------------------------------------
            packageSet.clear()
            // --------------------------------------------------
            val asyncClearHistory = async(Dispatchers.IO) {
                FileUtil.deleteFile(baseDir)
//                FileUtil.deleteFile(unExistsFile)
//                FileUtil.deleteFile(extractFile)
//                FileUtil.deleteFile(allFile)
            }
            val asyncGetInstalledApplications = async {
                val allAppList = AppUtil.getInstalledApplications(application)
                loadAllInstallApp(allAppList, packageSet)
//                loadSystemApp(allAppList, packageSet)
//                loadUnSystemApp(allAppList, packageSet)
                // --------------------------------------------------
                packageSet.remove(BaseApplication.getInstance().packageName)
                // --------------------------------------------------
                val filterSet = HashSet<String>()
//                filterSet.addAll(getStringArray(R.array.un_keep_app).toList())
                // --------------------------------------------------
//                filterSet.addAll(getStringArray(R.array.apps).toList())
                // 添加规则--------------------------------------------------
//                filterSet.mapTo(packageList) { it }
                // 移除规则--------------------------------------------------
                filterSet.forEach {
                    packageSet.remove(it)
                }
                matchApp(packageSet.values)
            }
            asyncClearHistory.await()
            asyncGetInstalledApplications.await()
            dataLoading.postValue(false)
            TraceHelper.endSection("loadAppList")
        }
    }

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
        this.matchStr = matchStr
        matchApp(packageSet.values)
    }

    private fun matchApp(appList: MutableCollection<AppShortcut>) {
//        Observable.create<MutableCollection<AppShortcut>> { e ->
//            e.onNext(appList)
//            e.onComplete()
//        }.subscribeOn(ThreadPlugins.ioScheduler())
//            .flatMap {
//                ObservableFromIterable(it)
//            }.filter {
//                matchStr.isEmpty() || it.packageName.contains(
//                    matchStr,
//                    true
//                ) || it.name.contains(matchStr, true)
//            }.toList()
//            .toObservable()
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe(object : MyObserver<MutableCollection<AppShortcut>>(compositeDisposable) {
//                override fun onNext(result: MutableCollection<AppShortcut>) {
//                    super.onNext(result)
//                    show(result)
//                }
//
//                override fun onError(e: Throwable) {
//                    super.onError(e)
//                    show(HashSet())
//                }
//            })
    }

    // --------------------------------------------------
    private fun initEntity(appShortcut: AppShortcut?): AppShortcut? {
        return appShortcut?.also {
            val packageName = appShortcut.packageName
            if (appShortcut.isInstalled) {
                FileUtil.writeToFile(
                    existsFile,
                    "<item>$packageName</item><!--${appShortcut.name}-->\n",
                    true
                )
            } else {
                FileUtil.writeToFile(unExistsFile, "<item>$packageName</item>\n", true)
            }
            FileUtil.writeToFile(
                allFile,
                "<item>$packageName</item><!--${appShortcut.name}-->\n",
                true
            )
        }
    }
}