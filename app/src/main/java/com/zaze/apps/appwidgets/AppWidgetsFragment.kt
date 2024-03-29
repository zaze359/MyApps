package com.zaze.apps.appwidgets

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zaze.apps.R
import com.zaze.apps.core.base.AbsFragment
import com.zaze.apps.databinding.FragmentAppWidgetsBinding
import com.zaze.apps.core.ext.setupActionBar
import com.zaze.apps.viewmodels.AppWidgetsViewModel
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-04 - 09:24
 */
class AppWidgetsFragment : AbsFragment() {
    private var _binding: FragmentAppWidgetsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppWidgetsViewModel by viewModels()

    companion object {
        private const val REQUEST_CREATE_APPWIDGET = 5
    }

    private val bindAppwidgetRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            ZLog.i(
                ZTag.TAG,
                "bindAppwidgetRequest: ${it.resultCode}, ${
                    it.data?.getIntExtra(
                        AppWidgetManager.EXTRA_APPWIDGET_ID,
                        -1
                    )
                }"
            )
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
        _binding = FragmentAppWidgetsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(binding.appBarLayout.toolbar) {
            title = getString(R.string.app_widgets)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appWidgets.collectLatest { appWidgets ->
                    binding.appWidgetsLayout.removeAllViews()
                    appWidgets.filter { it.parent == null }.forEach {
                        binding.appWidgetsLayout.addView(it)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                for (bind in viewModel.bindWidgetAction) {
                    bind.startBindFlow(bindAppwidgetRequest)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                for (loader in viewModel.startWidgetConfigAction) {
                    loader.startConfigActivity(requireActivity(), REQUEST_CREATE_APPWIDGET)
                }
            }
        }
        viewModel.preloadAppWidgets()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}