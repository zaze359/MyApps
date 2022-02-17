package com.zaze.apps

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.zaze.apps.base.AbsActivity
import com.zaze.apps.appwidgets.WidgetHostViewLoader
import com.zaze.apps.databinding.ActivityMainBinding
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AbsActivity() {
    private var myNavController: NavController? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dataBinding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        // --------------------------------------------------
        setSupportActionBar(dataBinding.toolbar)
        val navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment?)?.navController
                ?: return
        myNavController = navController
        setupActionBarWithNavController(navController, AppBarConfiguration(navController.graph))
    }

    override fun onSupportNavigateUp(): Boolean {
        return myNavController?.navigateUp() ?: super.onSupportNavigateUp()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WidgetHostViewLoader.REQUEST_CREATE_APPWIDGET) {
            ZLog.i(ZTag.TAG, "onActivityResult: REQUEST_CREATE_APPWIDGET")
        }
    }
}