package com.zaze.apps

import androidx.core.content.ContextCompat
import com.zaze.apps.core.base.BaseApplication
import com.zaze.apps.receiver.PackageReceiver
import com.zaze.core.common.utils.app.ApplicationManager
import com.zaze.core.common.utils.thread.ThreadPlugins
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
        initRouter()
        ContextCompat.registerReceiver(
            this,
            PackageReceiver(),
            PackageReceiver.createIntentFilter(),
            ContextCompat.RECEIVER_EXPORTED
        )
        ThreadPlugins.runInWorkThread({
            ApplicationManager.getInstallApps(this)
        }, 0)
    }

    private fun initRouter() {
//        if (BuildConfig.DEBUG) { // 这两行必须写在init之前，否则这些配置在init过程中将无效
//            // 打印日志
//            ARouter.openLog()
//            // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
//            ARouter.openDebug()
//        }
//        ARouter.init(this)
    }

}