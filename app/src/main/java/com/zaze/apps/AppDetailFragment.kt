package com.zaze.apps

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.adapters.AppDetailAdapter
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentAppDetailBinding
import com.zaze.apps.ext.initToolbar
import com.zaze.apps.viewmodels.AppDetailViewModel
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
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

        lifecycleScope.launch {
            viewModel.appShortcut.collect { app ->
                app?.let {
                    binding.appNameTv.text = app.appName
                    binding.appVersionNameTv.text = app.versionName
                    binding.appIconIv.setImageBitmap(app.getAppIcon(context))
                }
            }
        }
        lifecycleScope.launch {
            viewModel.appDetailItems.collect { items ->
                binding.appDetailRecyclerView.adapter = AppDetailAdapter().apply {
                    submitList(items)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.appDirs.collect { dirs ->
                binding.appDirRecyclerView.adapter = AppDetailAdapter().apply {
                    submitList(dirs)
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
        viewModel.loadAppDetail(safeArgs.packageName)
//        viewModel.preloadAppWidgets(safeArgs.packageName)
    }
}