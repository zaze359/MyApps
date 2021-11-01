package com.zaze.apps.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.zaze.apps.base.BaseApplication
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : zaze
 * @version : 2021-05-21 - 16:03
 */
object PermissionManager {
    private val permissionMap = HashMap<String, String>().apply {
        this[Manifest.permission.WRITE_EXTERNAL_STORAGE] = "访问存储空间"
        this[Manifest.permission.READ_PHONE_STATE] = "读取手机状态"
        this[Manifest.permission.ACCESS_FINE_LOCATION] = "获取位置"
    }

    fun getPermissionNames(permissions: Array<String>): String {
        val rationaleBuilder = StringBuilder()
        getPermissionNameSet(permissions).forEachIndexed { index, s ->
            if (index > 0) {
                rationaleBuilder.append(",$s")
            } else {
                rationaleBuilder.append(s)
            }
        }
        return rationaleBuilder.toString()
    }

    fun getPermissionNameSet(permissions: Array<String>): Collection<String> {
        val permissionNameSet = HashSet<String>()
        permissions.forEach {
            val permissionName = permissionMap[it]
            if (!permissionName.isNullOrEmpty()) {
                permissionNameSet.add(permissionName)
            }
        }
        return permissionNameSet
    }

    fun getDeniedPermission(neededPermissions: Collection<String>): Set<String> {
        if (neededPermissions.isEmpty()) {
            return emptySet()
        }
        val permissionSet = HashSet<String>()
        neededPermissions.forEach {
            if (!hasPermission(it)) {
                permissionSet.add(it)
            }
        }
        if (permissionSet.isNotEmpty()) {
            ZLog.v(ZTag.TAG, "deniedPermissions: $permissionSet")
        }
        return permissionSet
    }

    fun hasPermissions(permissions: Array<String>): Boolean {
        if (permissions.isEmpty() || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        permissions.forEach {
            if (!hasPermission(it)) {
                return false
            }
        }
        return true
    }

    fun hasPermission(permission: String): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        return ContextCompat.checkSelfPermission(
            BaseApplication.getInstance(),
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}