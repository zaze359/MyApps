package com.zaze.apps.viewmodels

import android.app.Application
import android.appwidget.AppWidgetHostView
import android.content.ContextWrapper
import android.os.Build
import android.os.Environment
import androidx.lifecycle.viewModelScope
import com.zaze.apps.App
import com.zaze.apps.appwidgets.*
import com.zaze.apps.appwidgets.compat.AppWidgetManagerCompat
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.base.AbsViewModel
import com.zaze.apps.base.BaseApplication
import com.zaze.apps.data.AppDetailItem
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.utils.FileUtil
import com.zaze.utils.date.DateUtil
import com.zaze.utils.ext.debuggable
import com.zaze.utils.ext.isAAB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-21 - 15:57
 */
class AppDetailViewModel(application: Application) : AbsAndroidViewModel(application) {
    private val datePattern = "yyyy年MM月dd日 HH:mm"
    private val _appShortcut = MutableStateFlow<AppShortcut?>(null)
    val appShortcut: Flow<AppShortcut?> = _appShortcut

    /**
     * 应用详情
     */
    private val _appDetailItems = MutableStateFlow<List<AppDetailItem>>(emptyList())
    val appDetailItems: Flow<List<AppDetailItem>> = _appDetailItems

    /**
     * 应用目录
     */
    private val _appDirs = MutableStateFlow<List<AppDetailItem>>(emptyList())
    val appDirs: Flow<List<AppDetailItem>> = _appDirs

    /**
     * app widgets
     */
    private val _appWidgets = MutableStateFlow<List<AppWidgetHostView>>(emptyList())
    val appWidgets: Flow<List<AppWidgetHostView>> = _appWidgets
    private val waitingLoaders = Stack<WidgetHostViewLoader>()
    val bindWidgetAction = Channel<WidgetHostViewLoader>()
    private val appWidgetHost by lazy {
        LauncherAppWidgetHost(BaseApplication.getInstance())
    }

    fun loadAppDetail(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val app = ApplicationManager.getAppShortcut(application, packageName)
            _appShortcut.value = app
            // --------------------------------------------------
            val appDetailItems = ArrayList<AppDetailItem>()
            // --------------------------------------------------
            appDetailItems.add(AppDetailItem("包名", app.packageName))
            appDetailItems.add(AppDetailItem("版本", "${app.versionName} (${app.versionCode})"))
            appDetailItems.add(
                AppDetailItem(
                    "安装来源",
                    ApplicationManager.getAppNameHasDefault(
                        application,
                        app.installerPackageName,
                        "未知"
                    )
                )
            )
            appDetailItems.add(
                AppDetailItem(
                    "安装时间",
                    DateUtil.timeMillisToString(app.firstInstallTime, datePattern)
                )
            )
            appDetailItems.add(
                AppDetailItem(
                    "最近更新",
                    DateUtil.timeMillisToString(app.lastUpdateTime, datePattern)
                )
            )
            appDetailItems.add(AppDetailItem("UID", app.uid.toString()))
            app.applicationInfo?.let {
                appDetailItems.add(
                    AppDetailItem("目标 SDK", it.targetSdkVersion.toString())
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    appDetailItems.add(
                        AppDetailItem("最低 SDK", it.minSdkVersion.toString())
                    )
                }
                if (it.debuggable) {
                    appDetailItems.add(AppDetailItem("调试模式", "是"))
                }
                appDetailItems.add(
                    AppDetailItem(
                        "安装程序类型",
                        if (it.isAAB) "Android App Bundle(拆分式APK)" else "APK"
                    )
                )
            }
            _appDetailItems.value = appDetailItems
            loadAppDirs(app)
        }
    }

    private fun loadAppDirs(app: AppShortcut) {
        val appDirs = ArrayList<AppDetailItem>()
        app.applicationInfo?.let {
            appDirs.add(AppDetailItem("源", File(it.sourceDir).parent))
            appDirs.add(AppDetailItem("数据", it.dataDir))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appDirs.add(AppDetailItem("受保护数据", it.deviceProtectedDataDir))
            }
            val externalStorageDirectory =
                "${Environment.getExternalStorageDirectory().absolutePath}/Android/data/${it.packageName}"
            appDirs.add(
                AppDetailItem(
                    "外部数据",
                    if (FileUtil.exists(externalStorageDirectory)) externalStorageDirectory else "无"
                )
            )
            appDirs.add(AppDetailItem("本地库", it.nativeLibraryDir ?: "无"))

        }
        _appDirs.value = appDirs
    }

    fun preloadAppWidgets(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            waitingLoaders.clear()
            // --------------------------------------------------
            val appWidgets = ArrayList<AppWidgetHostView>()
            val context = BaseApplication.getInstance()
            val appWidgetManager = AppWidgetManagerCompat.getInstance(context)
            appWidgetManager.getAllProviders(null).filter {
                it.provider.packageName == packageName
            }.forEach { pInfo ->
                val loader = WidgetHostViewLoader(
                    appWidgetHost, PendingAddWidgetInfo(
                        LauncherAppWidgetProviderInfo.fromProviderInfo(pInfo)
                    )
                )
                if (loader.bindWidget()) {
                    loader.inflaterWidget()?.let {
                        appWidgets.add(it)
                    }
                } else {
                    waitingLoaders.add(loader)
                }
            }
            bindWidget()
            _appWidgets.emit(appWidgets)
        }
    }

    private suspend fun bindWidget() {
        if (waitingLoaders.empty()) {
            return
        } else {
            bindWidgetAction.send(waitingLoaders.peek())
        }
    }

    fun bindNext() {
//        val pm = BaseApplication.getInstance().packageManager
//        AppWidgetManagerCompat.getInstance(BaseApplication.getInstance())
//            .getAllProviders(null)?.forEach {
//                val widgetItem =
//                    WidgetItem(LauncherAppWidgetProviderInfo.fromProviderInfo(it), pm)
//                ZLog.i(ZTag.TAG, "app widget: ${widgetItem.label}")
//            }

        viewModelScope.launch {
            val loader = waitingLoaders.pop()
            loader.inflaterWidget()?.let {
                _appWidgets.value = _appWidgets.value.toMutableList().apply {
                    this.add(it)
                }
            }
            bindWidget()
        }
    }
}

private data class AppDetailViewModelState(
    val appShortcut: AppShortcut,
    val appDirs: List<AppDetailItem>
) {
    fun toUiState(): AppDetailUiState {
        return AppDetailUiState.App(
            name = appShortcut.appName ?: "未知",
            versionName = appShortcut.versionName ?: "未知"
        )
    }
}

sealed class AppDetailUiState {
    data class App(
        val name: String, val versionName: String,

        ) : AppDetailUiState()
}