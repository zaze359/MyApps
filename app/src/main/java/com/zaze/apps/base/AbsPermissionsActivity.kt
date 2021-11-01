package com.zaze.apps.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.zaze.apps.utils.PermissionManager
import com.zaze.apps.utils.SystemSettings
import com.zaze.apps.widgets.dialog.DialogProvider
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-15 - 15:45
 */
abstract class AbsPermissionsActivity : AbsThemeActivity() {
    private val permissions by lazy {
        getPermissionsToRequest()
    }

    private val permissionsRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var permissionGranted = true
            // 是否有权限被永久拒绝，默认false
            var permanentlyDenied = false
            it.forEach { result ->
                ZLog.i(
                    ZTag.TAG,
                    "onRequestPermissionsResult registerForActivityResult: ${result.key}: ${result.value}"
                )
                if (permissionGranted) {
                    permissionGranted = result.value
                }
                // 权限被拒绝 && 不需要解释
                if (!permanentlyDenied &&
                    result.value == false && !ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        result.key
                    )
                ) {
                    permanentlyDenied = true
                }
            }
            when {
                permissionGranted -> {
                    afterPermissionGranted()
                }
                permanentlyDenied -> {
                    onSomePermanentlyDenied()
                }
                else -> {
                    onPermissionDenied()
                }
            }
        }

    private val startSettingRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasPermission()) {
                afterPermissionGranted()
            } else {
                setupPermission()
            }
        }

    open fun getPermissionsToRequest(): Array<String> {
        return arrayOf()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupPermission()
    }

    private fun hasPermission(): Boolean {
        return PermissionManager.hasPermissions(permissions)
    }

    open fun setupPermission() {
        lifecycleScope.launchWhenResumed {
            if (hasPermission()) {
                afterPermissionGranted()
            } else {
                beforePermissionGranted()
                permissionsRequest.launch(permissions)
            }
        }
    }

    open fun afterPermissionGranted() {
//        ZLog.i(ZTag.TAG, "afterPermissionGranted")
    }

    open fun beforePermissionGranted() {
//        ZLog.i(ZTag.TAG, "beforePermissionGranted")
    }

    open fun onSomePermanentlyDenied() {
        val builder = DialogProvider.Builder()
            .message("如果没有「${PermissionManager.getPermissionNames(permissions)}」相关权限，此应用可能无法正常工作。")
            .negative("取消") {
                finish()
            }.positive {
                ZLog.i(ZTag.TAG, "openApplicationDetailsSetting")
                // 打开设置
                val intent = SystemSettings.applicationDetailsSettings(packageName)
                startSettingRequest.launch(intent)
            }
        builder.build().show(supportFragmentManager)
    }

    open fun onPermissionDenied() {
        setupPermission()
    }
}