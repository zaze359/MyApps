package com.zaze.apps

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentAppWidgetsBinding
import com.zaze.apps.ext.myViewModels
import com.zaze.apps.viewmodels.AppWidgetsViewModel
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-04 - 09:24
 */
class AppWidgetsFragment : AbsFragment() {
    private lateinit var dataBinding: FragmentAppWidgetsBinding
    private val viewModel: AppWidgetsViewModel by myViewModels()
    private var appWidgetsJob: Job? = null
    private var bindWidgetJob: Job? = null
    private var startWidgetConfigJob: Job? = null

    companion object {
        private const val REQUEST_CREATE_APPWIDGET = 5
    }

    private val bindAppwidgetRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            ZLog.i(ZTag.TAG, "bindAppwidgetRequest: ${it.resultCode}, ${it.data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)}")
            lifecycleScope.launch {
                if (it.resultCode == Activity.RESULT_OK) {
                    viewModel.addAppWidget()
                } else {
                    viewModel.bindNext()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAppWidgetsBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appWidgetsJob?.cancel()
        appWidgetsJob = lifecycleScope.launch {
            viewModel.appWidgets.collect { appWidgets ->
                dataBinding.appWidgetsLayout.removeAllViews()
                appWidgets.forEach {
                    dataBinding.appWidgetsLayout.addView(it)
                }
            }
        }

        bindWidgetJob?.cancel()
        bindWidgetJob = lifecycleScope.launch {
            for (bind in viewModel.bindWidgetAction) {
                bind.startBindFlow(bindAppwidgetRequest)
            }
        }

        startWidgetConfigJob?.cancel()
        startWidgetConfigJob = lifecycleScope.launch {
            for (loader in viewModel.startWidgetConfigAction) {
                loader.startConfigActivity(requireActivity(), REQUEST_CREATE_APPWIDGET)
            }
        }
        viewModel.preloadAppWidgets()
    }
}