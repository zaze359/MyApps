package com.zaze.apps

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.adapters.AppDetailAdapter
import com.zaze.apps.core.base.AbsFragment
import com.zaze.apps.databinding.FragmentAppDetailBinding
import com.zaze.apps.core.ext.initToolbar
import com.zaze.apps.viewmodels.AppDetailUiState
import com.zaze.apps.viewmodels.AppDetailViewModel
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-04 - 09:24
 */
class AppDetailFragment : AbsFragment() {
    private var _binding: FragmentAppDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppDetailViewModel by viewModels()
    private val bindAppwidgetRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            ZLog.i(ZTag.TAG, "result: ${it.resultCode}, ${it.data}")
            if (it.resultCode == Activity.RESULT_OK) {
                viewModel.bindNext()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val safeArgs: AppDetailFragmentArgs by navArgs()

        initToolbar(binding.appBarLayout.toolbar) {
            setTitle(R.string.app_detail)
            it.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        binding.appDetailRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.appDirRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.appMoreBtn.setOnClickListener {

        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {appDetail ->
                    when (appDetail) {
                        AppDetailUiState.NULL -> {}
                        is AppDetailUiState.App -> {
                            //
                            binding.appNameTv.text = appDetail.name
                            binding.appVersionNameTv.text = appDetail.versionName
                            binding.appIconIv.setImageBitmap(appDetail.appIcon)
                            // 详情
                            appDetail.appSummary?.let {
                                binding.appDetailRecyclerView.adapter = AppDetailAdapter().apply {
                                    submitList(it)
                                }
                            }
                            // 目录
                            appDetail.appDirs?.let {
                                binding.appDirRecyclerView.adapter = AppDetailAdapter().apply {
                                    submitList(it)
                                }
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.appWidgets.collect { appWidgets ->
                binding.appWidgetsLayout.removeAllViews()
                appWidgets.forEach {
                    it.isFocusable = true
                    binding.appWidgetsLayout.addView(it)
                }
            }
        }
        lifecycleScope.launch {
            for (bind in viewModel.bindWidgetAction) {
                bind.startBindFlow(bindAppwidgetRequest)
            }
        }
        viewModel.load(safeArgs.packageName)
//        viewModel.preloadAppWidgets(safeArgs.packageName)
    }
}