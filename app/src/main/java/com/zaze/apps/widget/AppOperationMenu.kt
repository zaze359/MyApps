package com.zaze.apps.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MenuItem.OnMenuItemClickListener
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.PopupMenu
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.zaze.apps.HomePagerFragmentDirections
import com.zaze.apps.R
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.data.OperationBean
import com.zaze.apps.databinding.AppOperationLayoutBinding
import com.zaze.apps.databinding.ItemAppOperationBinding
import com.zaze.apps.ext.onClick
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.apps.utils.SystemSettings
import com.zaze.utils.BmpUtil
import com.zaze.utils.DisplayUtil
import com.zaze.utils.EncryptionUtil
import com.zaze.utils.FileUtil
import com.zaze.utils.ToastUtil
import com.zaze.utils.ext.writeToFile
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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
    private val outApkFile = File("$outDir/${app.appName}_${app.versionName}.apk")
    private val outIconFile = File("$outDir/icon.jpg")


    private val onMenuItemClickListener = object : OnMenuItemClickListener {
        override fun onMenuItemClick(item: MenuItem?): Boolean {
            return when (item?.itemId) {
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
    }

    init {
        inflate(R.menu.menu_applist)
        setOnMenuItemClickListener(onMenuItemClickListener)
    }

    private fun backupApp() {
        CoroutineScope(Dispatchers.IO).launch {
            val sourceDir = app.sourceDir
            if (sourceDir.isNullOrEmpty()) {
                showTip("【${app.appName}】 提取失败, 找不到源APK文件")
                return@launch
            }
            val sourceFile = File(sourceDir)
            if (outApkFile.exists()) {
                showTip("【${app.appName}】 备份已存在")
                return@launch
            }
            FileUtil.copy(sourceFile, outApkFile)
            ApplicationManager.getAppIconHasDefault(context, app.packageName)
                ?.writeToFile(outIconFile)
            showTip("【${app.appName}】 备份完成")
        }
    }

    private suspend fun showTip(message: String) {
        withContext(Dispatchers.Main) {
            ZLog.i(ZTag.TAG, message)
            Snackbar.make(anchor.rootView, message, Snackbar.LENGTH_SHORT).setAction("确定") {}
                .show()
        }
    }
}