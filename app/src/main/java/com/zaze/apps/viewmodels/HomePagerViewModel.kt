package com.zaze.apps.viewmodels

import android.app.Application
import com.zaze.apps.AppListFragment
import com.zaze.apps.AppWidgetsFragment
import com.zaze.apps.OverviewFragment
import com.zaze.apps.R
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.data.HomePage
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-18 - 15:48
 */
class HomePagerViewModel(application: Application) : AbsAndroidViewModel(application) {

    val homePages = MutableStateFlow(
        listOf(
            HomePage(
                R.id.app_list_fragment,
                getString(R.string.apps),
                R.drawable.ic_apps,
                AppListFragment()
            ),
            HomePage(
                R.id.overview_fragment,
                getString(R.string.overview),
                R.drawable.ic_assessment,
                OverviewFragment()
            ),
            HomePage(
                R.id.app_list_fragment,
                getString(R.string.home),
                R.drawable.ic_assessment,
                AppWidgetsFragment()
            )
        )
    )
}