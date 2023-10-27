package com.zaze.apps

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.compose.ui.util.fastForEachReversed
import androidx.core.view.contains
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.contains
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import com.zaze.apps.appwidgets.WidgetHostViewLoader
import com.zaze.apps.base.AbsActivity
import com.zaze.apps.databinding.ActivityMainBinding
import com.zaze.core.ext.currentNavFragment
import com.zaze.core.ext.findNavController
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
            NavigationUI.onNavDestinationSelected(
                item,
                navController
            )
        }
        navController.addOnDestinationChangedListener(
            object : NavController.OnDestinationChangedListener {
                override fun onDestinationChanged(
                    controller: NavController,
                    destination: NavDestination,
                    arguments: Bundle?
                ) {
                    when(destination.id) {
                        R.id.overview_fragment, R.id.app_list_fragment ->{
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

    private fun NavDestination.matchDestination(@IdRes destId: Int): Boolean =
        hierarchy.any { it.id == destId }
}