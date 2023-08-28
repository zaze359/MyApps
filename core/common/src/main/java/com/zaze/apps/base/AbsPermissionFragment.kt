package com.zaze.apps.base

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zaze.apps.widget.dialog.DialogProvider
import com.zaze.core.common.permission.PermissionHandler
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.launch

abstract class AbsPermissionFragment : AbsLogFragment {
    constructor() : super()
    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    private val permissionHandler by lazy {
        PermissionHandler(
            activity = requireActivity(),
            permissions = getPermissionsToRequest(),
            afterPermissionGranted = ::afterPermissionGranted,
            onSomePermanentlyDenied = ::onSomePermanentlyDenied,
            onPermissionDenied = ::onPermissionDenied
        )
    }

    private val permissionsRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionHandler.onActivityResult(it)
        }

    private val startSettingRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (hasPermissions()) {
                afterPermissionGranted()
            } else {
                setupPermission()
            }
        }

    open fun getPermissionsToRequest(): Array<String> {
        return arrayOf()
    }

    fun hasPermissions(): Boolean {
        return permissionHandler.hasPermissions()
    }

    open fun setupPermission() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (hasPermissions()) {
                    afterPermissionGranted()
                } else {
                    beforePermissionGranted()
                    permissionHandler.launch(permissionsRequest)
                }
            }
        }
    }

    /**
     * 获取权限后
     */
    open fun afterPermissionGranted() {
        ZLog.i(ZTag.TAG, "afterPermissionGranted")
    }

    /**
     * 获取权限前
     */
    open fun beforePermissionGranted() {
        ZLog.i(ZTag.TAG, "beforePermissionGranted")
    }

    /**
     * 部分权限被拒绝
     */
    open fun onSomePermanentlyDenied() {
        val builder = DialogProvider.Builder()
            .message("如果没有「${permissionHandler.getDeniedPermissionNames()}」相关权限，此应用可能无法正常工作。")
            .negative("取消") {
//                finish()
            }
        builder.positive {
            ZLog.i(ZTag.TAG, "openApplicationDetailsSetting")
            // 打开设置
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
                Uri.fromParts("package", requireContext().packageName, null)
            )
            startSettingRequest.launch(intent)
        }
        builder.build().show(childFragmentManager)
    }

    open fun onPermissionDenied() {
        setupPermission()
    }
}