package com.zaze.apps.base

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zaze.core.common.permission.PermissionRequest
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-15 - 15:45
 */
abstract class AbsPermissionsActivity : AbsThemeActivity() {

//    private val permissionHandler by lazy {
//        PermissionHandler(
//            activity = this,
//            permissions = getPermissionsToRequest(),
//            afterPermissionGranted = ::afterPermissionGranted,
//            onSomePermanentlyDenied = ::onSomePermanentlyDenied,
//            onPermissionDenied = ::onPermissionDenied
//        )
//    }

    private val permissionRequest = PermissionRequest(this)

//    private val permissionsRequest =
//        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
//            permissionHandler.onActivityResult(it)
//        }
//
//    private val startSettingRequest =
//        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (hasPermissions()) {
//                afterPermissionGranted()
//            } else {
//                setupPermission()
//            }
//        }

    open fun getPermissionsToRequest(): Array<String> {
        return arrayOf()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        setupPermission()
    }

    fun hasPermissions(): Boolean {
        return permissionRequest.hasPermissions()
    }

    open fun setupPermission() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                if (hasPermissions()) {
                    afterPermissionGranted()
                } else {
                    beforePermissionGranted()
                    permissionRequest.onPermissionGranted {
                        afterPermissionGranted()
                    }.request(getPermissionsToRequest())
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

    open fun onPermissionDenied() {
        setupPermission()
    }
}