package com.zaze.apps

import android.content.Intent
import android.os.Bundle
import androidx.compose.ui.util.fastForEachReversed
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationBarView
import com.zaze.apps.appwidgets.WidgetHostViewLoader
import com.zaze.apps.core.base.AbsActivity
import com.zaze.apps.databinding.ActivityMainBinding
import com.zaze.apps.core.ext.findNavController
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference


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
//        val provider = FragmentNavigator(this, supportFragmentManager, R.id.fragment_container)
//        navController.graph.addDestination(provider.createDestination().apply {
//            id = R.id.message_fragment
//        })
//        binding.bottomNav.setupWithNavController(navController)
        setupWithNavController(binding.bottomNav, navController)
        binding.bottomNav.setOnItemReselectedListener {
            navController.popBackStack(it.itemId, false)
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.fragment_container).navigateUp()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == WidgetHostViewLoader.REQUEST_CREATE_APPWIDGET) {
            ZLog.i(ZTag.TAG, "onActivityResult: REQUEST_CREATE_APPWIDGET")
        }
    }

    /**
     * 重写 setupWithNavController
     * 处理 顶级导航发生跳转后，重写点回来由于 destination和 item 不匹配，导致无法选中的问题
     */
    private fun setupWithNavController(
        navigationBarView: NavigationBarView,
        navController: NavController
    ) {
        val weakReference = WeakReference(navigationBarView)
        navigationBarView.setOnItemSelectedListener { item ->
            val res = NavigationUI.onNavDestinationSelected(
                item,
                navController
            )
            if (!res) {
                //
            }
            true
        }
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    when (destination.id) {
                        R.id.overview_fragment, R.id.app_list_fragment, R.id.message_fragment -> { // 顶层页面导航可见
                            binding.bottomNav.isVisible = true
                        }

                        else -> {
                            binding.bottomNav.isVisible = false
                        }
                    }
                    val view = weakReference.get()
                    if (view == null) {
                        navController.removeOnDestinationChangedListener(this)
                        return
                    }
                    // 从 返回栈中 倒序匹配，匹配到最近一个顶层导航 就置为选中。
                    controller.backQueue.fastForEachReversed {
                        view.menu.forEach { item ->
                            if (it.destination.id == item.itemId) {
                                item.isChecked = true
                                return
                            }
                        }
                    }
                }
            })
    }
//
//    private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
//        hierarchy.any { it.id == destId }
}