package com.zaze.apps

import android.os.Bundle
import com.zaze.apps.base.AbsActivity

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-04 - 09:24
 */
class AppDetailActivity : AbsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)
    }
}