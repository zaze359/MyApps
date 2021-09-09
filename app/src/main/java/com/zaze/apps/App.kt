package com.zaze.apps

import com.zaze.apps.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp
import java.lang.IllegalStateException

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

}