package com.zaze.apps.widget

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.view.View
import android.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar
import com.zaze.apps.R
import com.zaze.core.common.utils.app.AppShortcut
import com.zaze.core.common.utils.app.ApplicationManager
import com.zaze.core.common.utils.SystemSettings
import com.zaze.utils.FileUtil
import com.zaze.utils.compress.ZipUtil
import com.zaze.utils.ext.isAAB
import com.zaze.utils.ext.writeToFile
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-27 - 16:39
 */
class AppOperationMenu(
    private val context: Context,
    private val anchor: View,
    private val app: AppShortcut,
) : PopupMenu(context, anchor) {
    private val outDir = "${context.getExternalFilesDir(null)}/backups/${app.packageName}"
    private val outApkFile by lazy {
        File("$outDir/${app.appName}_${app.versionName}.apk")
    }
    private val outAABFile by lazy {
        File("$outDir/${app.appName}_${app.versionName}.apks")
    }
    private val outIconFile = File("$outDir/icon.jpg")


    private val onMenuItemClickListener = OnMenuItemClickListener { item ->
        when (item?.itemId) {
            R.id.action_startup -> { // 启动应用
                ApplicationManager.openApp(context, app.packageName)
                true
            }

            R.id.action_uninstall -> { // 卸载
                ApplicationManager.uninstallApp(context, app.packageName)
                true
            }

            R.id.action_detail -> { // 设置-详情
                context.startActivity(SystemSettings.applicationDetailsSettings(app.packageName))
                true
            }

            R.id.action_backup -> {
                backupApp()
                true
            }

            else -> {
                false
            }
        }
    }

    init {
        inflate(R.menu.menu_app)
        setOnMenuItemClickListener(onMenuItemClickListener)
    }

    private fun backupApp() {
        CoroutineScope(Dispatchers.IO).launch {
            val applicationInfo = app.applicationInfo ?: return@launch
            if (applicationInfo.sourceDir.isNullOrEmpty()) {
                showTip("【${app.appName}】 提取失败, 找不到APK文件")
                return@launch
            }
            val ret = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && applicationInfo.isAAB) {
                backupAAB(applicationInfo)
            } else {
                backupApk(applicationInfo)
            }
            if (!ret) {
                return@launch
            }
            ApplicationManager.getAppIconHasDefault(context, app.packageName)
                ?.writeToFile(outIconFile)
            showTip("【${app.appName}】 备份完成")
        }
    }

    private suspend fun backupApk(applicationInfo: ApplicationInfo): Boolean {
        val sourceFile = File(applicationInfo.sourceDir)
        if (outApkFile.exists()) {
            showTip("【${app.appName}】 备份已存在")
            return false
        }
        FileUtil.copy(sourceFile, outApkFile)
        return true
    }

    private suspend fun backupAAB(applicationInfo: ApplicationInfo): Boolean {
        val sourceFiles = applicationInfo.splitSourceDirs.toMutableList()
        sourceFiles.add(applicationInfo.sourceDir)
        // 是 aab, 导出为 apks
        if (outAABFile.exists()) {
            showTip("【${app.appName}】 备份已存在")
            return false
        }
        ZipUtil.compressFiles(sourceFiles.filter { !it.isNullOrEmpty() }
            .map { File(it) }.toTypedArray(), outAABFile.absolutePath)
        return true
    }


    private suspend fun showTip(message: String) {
        withContext(Dispatchers.Main) {
            ZLog.i(ZTag.TAG, message)
            Snackbar.make(anchor.rootView, message, Snackbar.LENGTH_SHORT).setAction("确定") {}
                .show()
        }
    }
}