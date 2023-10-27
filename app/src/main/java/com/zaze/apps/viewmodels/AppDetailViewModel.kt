package com.zaze.apps.viewmodels

import android.app.Application
import android.appwidget.AppWidgetHostView
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import androidx.lifecycle.viewModelScope
import com.zaze.apps.appwidgets.LauncherAppWidgetHost
import com.zaze.apps.appwidgets.LauncherAppWidgetProviderInfo
import com.zaze.apps.appwidgets.PendingAddWidgetInfo
import com.zaze.apps.appwidgets.WidgetHostViewLoader
import com.zaze.apps.appwidgets.compat.AppWidgetManagerCompat
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.base.BaseApplication
import com.zaze.apps.data.AppDetailItem
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.utils.AppUtil
import com.zaze.utils.FileUtil
import com.zaze.utils.date.DateUtil
import com.zaze.utils.ext.debuggable
import com.zaze.utils.ext.isAAB
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.Stack

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-21 - 15:57
 */
class AppDetailViewModel(application: Application) : AbsAndroidViewModel(application) {
    companion object {
        private const val datePattern = "yyyy年MM月dd日 HH:mm"
    }

    private val viewModelState = MutableStateFlow(AppDetailViewModelState())
    val uiState = viewModelState.map(AppDetailViewModelState::toUiState).stateIn(
        viewModelScope, SharingStarted.Eagerly, AppDetailUiState.NULL
    )

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


    fun load(packageName: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val app = ApplicationManager.getAppShortcut(application, packageName)
            viewModelState.update {
                it.copy(appShortcut = app)
            }
            loadAppDetail(app)
            loadAppDirs(app)
            loadAppPermissions(app)
        }
    }

    /**
     * 加载应用详情
     */
    private fun loadAppDetail(app: AppShortcut) {
        val appSummary = ArrayList<AppDetailItem>()
        // --------------------------------------------------
        appSummary.add(AppDetailItem("包名", app.packageName))
        appSummary.add(AppDetailItem("版本", "${app.versionName} (${app.versionCode})"))
        appSummary.add(
            AppDetailItem(
                "安装来源",
                ApplicationManager.getAppNameHasDefault(
                    application,
                    app.installerPackageName,
                    "未知"
                )
            )
        )
        appSummary.add(
            AppDetailItem(
                "安装时间",
                DateUtil.timeMillisToString(app.firstInstallTime, datePattern)
            )
        )
        appSummary.add(
            AppDetailItem(
                "最近更新",
                DateUtil.timeMillisToString(app.lastUpdateTime, datePattern)
            )
        )
        appSummary.add(AppDetailItem("UID", app.uid.toString()))
        app.applicationInfo?.let {
            appSummary.add(
                AppDetailItem("目标 SDK", it.targetSdkVersion.toString())
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                appSummary.add(
                    AppDetailItem("最低 SDK", it.minSdkVersion.toString())
                )
            }
            if (it.debuggable) {
                appSummary.add(AppDetailItem("调试模式", "是"))
            }
            appSummary.add(
                AppDetailItem(
                    "安装程序类型",
                    if (it.isAAB) "Android App Bundle(拆分式APK)" else "APK"
                )
            )
        }
//            _appSummary.value = appSummary
        viewModelState.update {
            it.copy(appSummary = appSummary)
        }
    }

    /**
     * 加载应用目录
     */
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
        viewModelState.update {
            it.copy(appDirs = appDirs)
        }
//        _appDirs.value = appDirs
    }

    private fun loadAppPermissions(app: AppShortcut) {
        AppUtil.getPackageInfo(
            application,
            app.packageName,
            PackageManager.GET_PERMISSIONS
        )?.permissions?.forEach {
            ZLog.i(ZTag.TAG, "PERMISSION_GRANTED: ${it.packageName} >> ${it.name};")
            ZLog.i(ZTag.TAG, "----------------- group: ${it.group}")
            ZLog.i(ZTag.TAG, "----------------- flags: ${it.flags}")
            ZLog.i(ZTag.TAG, "----------------- it: ${it}")
        }
        AppUtil.getPackageInfo(
            application,
            app.packageName,
            PackageManager.GET_PERMISSIONS
        )?.requestedPermissions?.forEach {
            ZLog.i(
                ZTag.TAG,
                "requestedPermissions: $it; ${
                    application.packageManager.checkPermission(
                        it,
                        app.packageName
                    ) == PackageManager.PERMISSION_GRANTED
                }"
            )
            application.packageManager.getPermissionInfo(it, 0)?.let { info ->
                ZLog.i(
                    ZTag.TAG,
                    "----------------- loadDescription: ${info.loadDescription(application.packageManager)}"
                )
                ZLog.i(
                    ZTag.TAG,
                    "----------------- labelRes: ${
                        application.packageManager.getText(
                            app.packageName,
                            info.labelRes,
                            null
                        )
                    }"
                )
                ZLog.i(ZTag.TAG, "----------------- group: ${info.group}")
                val flags = when (info.flags) {
                    PermissionInfo.FLAG_COSTS_MONEY -> {
                        "FLAG_COSTS_MONEY"
                    }

                    PermissionInfo.FLAG_HARD_RESTRICTED -> {
                        "FLAG_HARD_RESTRICTED"
                    }

                    PermissionInfo.FLAG_INSTALLED -> { // 已允许
                        "FLAG_INSTALLED"
                    }

                    PermissionInfo.FLAG_IMMUTABLY_RESTRICTED -> {
                        "FLAG_IMMUTABLY_RESTRICTED"
                    }

                    PermissionInfo.FLAG_SOFT_RESTRICTED -> {
                        "FLAG_SOFT_RESTRICTED"
                    }

                    1 shl 1 -> {
                        "FLAG_REMOVED"
                    }

                    else -> {
                        "${info.flags}"
                    }
                }
                ZLog.i(ZTag.TAG, "----------------- flags: ${flags}")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ZLog.i(ZTag.TAG, "----------------- protection: ${info.protection}")
                    val protection = when (info.protection) {
                        PermissionInfo.PROTECTION_DANGEROUS -> {
                            "危险敏感权限"
                        }

                        PermissionInfo.PROTECTION_INTERNAL -> {
                            "PROTECTION_INTERNAL"
                        }

                        PermissionInfo.PROTECTION_NORMAL -> {
                            "普通权限"
                        }

                        PermissionInfo.PROTECTION_SIGNATURE -> {
                            "PROTECTION_SIGNATURE"
                        }

                        else -> {
                            "${info.protection}"
                        }
                    }
                    ZLog.i(ZTag.TAG, "----------------- protection2: ${protection}")
                    val protectionFlags = when (info.protectionFlags) {
                        PermissionInfo.PROTECTION_FLAG_APPOP -> {
                            "PROTECTION_FLAG_APPOP"
                        }

                        PermissionInfo.PROTECTION_FLAG_DEVELOPMENT -> {
                            "PROTECTION_FLAG_DEVELOPMENT"
                        }

                        PermissionInfo.PROTECTION_FLAG_INSTALLER -> {
                            "PROTECTION_FLAG_INSTALLER"
                        }

                        PermissionInfo.PROTECTION_FLAG_SETUP -> {
                            "PROTECTION_FLAG_SETUP"
                        }

                        else -> {
                            "${info.protectionFlags}"
                        }
                    }
                    ZLog.i(ZTag.TAG, "----------------- protection: ${protectionFlags}")
                }

            }
        }
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
    private val appShortcut: AppShortcut? = null,
    private val appSummary: List<AppDetailItem>? = null,
    private val appDirs: List<AppDetailItem>? = null,
) {
    fun toUiState(): AppDetailUiState {
        return AppDetailUiState.App(
            name = appShortcut?.appName ?: "未知",
            versionName = appShortcut?.versionName ?: "未知",
            appIcon = appShortcut?.getAppIcon(BaseApplication.getInstance()),
            appSummary = appSummary,
            appDirs = appDirs,
        )
    }
}

sealed class AppDetailUiState {
    object NULL : AppDetailUiState()
    data class App(
        val name: String,
        val versionName: String,
        val appIcon: Bitmap?,
        val appSummary: List<AppDetailItem>?,
        val appDirs: List<AppDetailItem>?,
    ) : AppDetailUiState()
}