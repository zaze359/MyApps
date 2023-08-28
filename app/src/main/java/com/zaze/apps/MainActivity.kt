package com.zaze.apps

import android.content.Intent
import android.os.Bundle
import androidx.navigation.ui.setupWithNavController
import com.zaze.apps.appwidgets.WidgetHostViewLoader
import com.zaze.apps.base.AbsSlidingPanelActivity
import com.zaze.apps.databinding.ActivityMainBinding
import com.zaze.apps.ext.gone
import com.zaze.apps.ext.visible
import com.zaze.core.ext.currentNavFragment
import com.zaze.core.ext.findNavController
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AbsSlidingPanelActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(wrapSlidingMusicPanel(binding.root))
        setupNavigationController()
    }

    private fun setupNavigationController() {
        val navController = findNavController(R.id.fragment_container)
//        myNavController = navController
//        setupActionBarWithNavController(navController, AppBarConfiguration(navController.graph))
        bottomNavigationView.setupWithNavController(navController)
        bottomNavigationView.setOnItemReselectedListener {
            currentNavFragment(R.id.fragment_container).apply {
                // scrollToTop()
            }
        }
        val navGraph = navController.graph
        navController.addOnDestinationChangedListener { controller, destination, _ ->
//            if (destination.id == navGraph.startDestinationId) {
//                currentNavFragment(R.id.fragment_container)?.enterTransition = null
//            }
            when (destination.id) {
                R.id.home_fragment, R.id.app_list_fragment, R.id.overview_fragment -> {
                    bottomNavigationView.visible()
                }

                else -> {
                    bottomNavigationView.gone()
                }
            }

//            binding.toolbar.setNavigationIcon(R.drawable.icon_return)
//            if(controller.currentDestination?.id == controller.graph.startDestinationId) {
//                binding.toolbar.setNavigationOnClickListener {
//                    finish()
//                }
//            } else {
//                binding.toolbar.setNavigationOnClickListener {
//                    controller.navigateUp()
//                }
//            }
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.fragment_container).navigateUp()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WidgetHostViewLoader.REQUEST_CREATE_APPWIDGET) {
            ZLog.i(ZTag.TAG, "onActivityResult: REQUEST_CREATE_APPWIDGET")
        }
    }
}