package com.zaze.apps

import androidx.core.content.ContextCompat
import com.zaze.apps.base.BaseApplication
import com.zaze.apps.receiver.PackageReceiver
import com.zaze.utils.TraceHelper
import dagger.hilt.android.HiltAndroidApp

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-22 - 18:27
 */
@HiltAndroidApp
class App : BaseApplication() {
    companion object {
        fun getInstance() = BaseApplication.getInstance() as App
    }

    override fun onCreate() {
        super.onCreate()
        TraceHelper.enable(BuildConfig.DEBUG)
        ContextCompat.registerReceiver(
            this,
            PackageReceiver(),
            PackageReceiver.createIntentFilter(),
            ContextCompat.RECEIVER_EXPORTED
        )

    }
}