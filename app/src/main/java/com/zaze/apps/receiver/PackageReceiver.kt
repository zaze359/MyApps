package com.zaze.apps.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-09 - 13:34
 */
class PackageReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "${ZTag.TAG}PackageReceiver"
        fun createIntentFilter(): IntentFilter {
            val intentFilter = IntentFilter()
            intentFilter.addAction("android.intent.action.PACKAGE_ADDED")
            intentFilter.addAction("android.intent.action.PACKAGE_REPLACED")
            intentFilter.addAction("android.intent.action.PACKAGE_REMOVED")
            intentFilter.addDataScheme("package")
            return intentFilter
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        val packageName = intent?.data?.schemeSpecificPart
        ZLog.i(TAG, "$packageName $action")
        ZLog.i(TAG, "${intent?.dataString}")
        if (packageName.isNullOrEmpty()) {
            return
        }
        // --------------------------------------------------
        when (action) {
            Intent.ACTION_PACKAGE_ADDED -> {
                afterAppAdded(packageName)
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                afterAppReplaced(packageName)
            }
            Intent.ACTION_PACKAGE_REMOVED -> {
                afterAppRemoved(packageName)
            }
        }
    }

    private fun afterAppAdded(packageName: String) {
        ZLog.i(TAG, "添加应用 : $packageName")
    }

    private fun afterAppReplaced(packageName: String) {
        ZLog.i(TAG, "替换应用 : $packageName")
    }

    private fun afterAppRemoved(packageName: String) {
        ZLog.i(TAG, "卸载成功$packageName")
    }
}