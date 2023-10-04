package com.zaze.apps

import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.zaze.apps.data.AppSort
import com.zaze.core.ext.normalNavOptions

object MenuHandler {
    fun Fragment.handleMenuItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController().navigate(R.id.settings_fragment, null, normalNavOptions)
                true
            }

            else -> false
        }
    }

    fun setupAppSortMenu(menu: Menu, sort: AppSort) {
        menu.findItem(R.id.action_sort_order)?.subMenu?.let { appSortMenu ->
            when (sort) {
                AppSort.Name -> appSortMenu.findItem(R.id.action_sort_order_title).isChecked = true
                AppSort.Size -> appSortMenu.findItem(R.id.action_sort_order_size).isChecked = true
                AppSort.InstallTime -> appSortMenu.findItem(R.id.action_sort_order_install_time).isChecked =
                    true

                AppSort.UpdateTime -> appSortMenu.findItem(R.id.action_sort_order_update_time).isChecked =
                    true
            }
        }

    }

    fun handleAppSortOrder(item: MenuItem, sortChanged: (AppSort) -> Unit): Boolean {
        val ret = when (item.itemId) {
            R.id.action_sort_order_title -> {
                sortChanged(AppSort.Name)
                true
            }

            R.id.action_sort_order_size -> {
                sortChanged(AppSort.Size)
                true
            }

            R.id.action_sort_order_install_time -> {
                sortChanged(AppSort.InstallTime)
                true
            }

            R.id.action_sort_order_update_time -> {
                sortChanged(AppSort.UpdateTime)
                true
            }

            else -> false
        }
        if (ret) {
            item.isChecked = true
        }
        return ret
    }
}