package com.zaze.apps.viewmodels

import android.appwidget.AppWidgetHostView
import android.os.Build
import androidx.lifecycle.viewModelScope
import com.zaze.apps.App
import com.zaze.apps.appwidgets.*
import com.zaze.apps.appwidgets.compat.AppWidgetManagerCompat
import com.zaze.apps.base.AbsViewModel
import com.zaze.apps.base.BaseApplication
import com.zaze.apps.data.AppDetailItem
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.utils.date.DateUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-21 - 15:57
 */
class AppDetailViewModel : AbsViewModel() {

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
            val app = ApplicationManager.getAppShortcut(packageName)
            _appShortcut.value = app
            // --------------------------------------------------
            val appDetailItems = ArrayList<AppDetailItem>()
            val appDirs = ArrayList<AppDetailItem>()
            // --------------------------------------------------
            appDetailItems.add(AppDetailItem("包名", app.packageName))
            appDetailItems.add(AppDetailItem("版本号", app.versionCode.toString()))
            appDetailItems.add(
                AppDetailItem(
                    "安装时间",
                    DateUtil.timeMillisToString(app.firstInstallTime, "yyyy-MM-dd HH:mm:ss")
                )
            )
            appDetailItems.add(
                AppDetailItem(
                    "最近更新",
                    DateUtil.timeMillisToString(app.lastUpdateTime, "yyyy-MM-dd HH:mm:ss")
                )
            )
            appDetailItems.add(AppDetailItem("UID", app.uid.toString()))
            app.getApplicationInfo(App.getInstance())?.let {
                appDetailItems.add(
                    AppDetailItem("目标 SDK", it.targetSdkVersion.toString())
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    appDetailItems.add(
                        AppDetailItem("最低 SDK", it.minSdkVersion.toString())
                    )
                }
                // --------------------------------------------------
                appDirs.add(AppDetailItem("APK路径", it.sourceDir))
                appDirs.add(AppDetailItem("Data", it.dataDir))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    appDirs.add(AppDetailItem("ProtectedData", it.deviceProtectedDataDir))
                }
            }
            _appDirs.value = appDirs
            _appDetailItems.value = appDetailItems
        }
    }

    fun preloadAppWidgets(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            waitingLoaders.clear()
            // --------------------------------------------------
            val appWidgets = ArrayList<AppWidgetHostView>()
            val context = BaseApplication.getInstance()
            val appWidgetManager = AppWidgetManagerCompat.getInstance(context)
            appWidgetManager.getAllProviders(null)
                .filter {
                    it.provider.packageName == packageName
                }
                .forEach { pInfo ->
                    val loader = WidgetHostViewLoader(
                        appWidgetHost,
                        PendingAddWidgetInfo(
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