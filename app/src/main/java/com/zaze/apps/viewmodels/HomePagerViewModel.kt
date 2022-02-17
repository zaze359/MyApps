package com.zaze.apps.viewmodels

import android.app.Application
import androidx.lifecycle.viewModelScope
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
                R.id.app_list_dest,
                getString(R.string.apps),
                R.drawable.ic_baseline_apps_24,
                AppListFragment()
            ),
            HomePage(
                R.id.overview_dest,
                getString(R.string.overview),
                R.drawable.ic_baseline_assessment_24,
                OverviewFragment()
            ),
            HomePage(
                R.id.app_list_dest,
                getString(R.string.home),
                R.drawable.ic_baseline_assessment_24,
                AppWidgetsFragment()
            )
        )
    )
}