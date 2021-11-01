package com.zaze.apps.utils

import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-29 - 13:30
 */
object SystemSettings {

    fun applicationDetailsSettings(packageName: String): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(
            Uri.fromParts("package", packageName, null)
        )
    }

}