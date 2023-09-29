package com.zaze.apps

import android.content.Intent
import android.os.Bundle
import androidx.navigation.ui.setupWithNavController
import com.zaze.apps.appwidgets.WidgetHostViewLoader
import com.zaze.apps.base.AbsActivity
import com.zaze.apps.databinding.ActivityMainBinding
import com.zaze.core.ext.currentNavFragment
import com.zaze.core.ext.findNavController
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AbsActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigationController()
    }

    private fun setupNavigationController() {
        val navController = findNavController(R.id.fragment_container)
//        myNavController = navController
//        setupActionBarWithNavController(navController, AppBarConfiguration(navController.graph))
        binding.bottomNav.setupWithNavController(navController)
        binding.bottomNav.setOnItemReselectedListener {
            currentNavFragment(R.id.fragment_container).apply {
                // scrollToTop()
            }
        }
//        binding.bottomNav.setOnItemSelectedListener {
//            ZLog.i(ZTag.TAG, "destination: ${it}")
//            true
//        }
        val navGraph = navController.graph
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            ZLog.i(ZTag.TAG, "destination: $destination")
//            if (destination.id == navGraph.startDestinationId) {
//                currentNavFragment(R.id.fragment_container)?.enterTransition = null
//            }
//            when (destination.id) {
//                R.id.home_fragment, R.id.app_list_fragment, R.id.overview_fragment -> {
//                    binding.bottomNav.visible()
//                }
//
//                else -> {
//                    binding.bottomNav.gone()
//                }
//            }

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