package com.zaze.apps

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.adapters.AppDetailAdapter
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentAppDetailBinding
import com.zaze.apps.ext.myViewModels
import com.zaze.apps.viewmodels.AppDetailViewModel
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-04 - 09:24
 */
class AppDetailFragment : AbsFragment() {
    private lateinit var dataBinding: FragmentAppDetailBinding
    private val viewModel: AppDetailViewModel by myViewModels()
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
        dataBinding = FragmentAppDetailBinding.inflate(inflater, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.viewModel = viewModel
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.appDetailRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        dataBinding.appDirRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val safeArgs: AppDetailFragmentArgs by navArgs()
        lifecycleScope.launch {
            viewModel.appShortcut.collect { app ->
                app?.let {
                    dataBinding.appNameTv.text = app.appName
                    dataBinding.appVersionNameTv.text = app.versionName
                    dataBinding.appIconIv.setImageBitmap(app.appIcon)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.appDetailItems.collect { items ->
                dataBinding.appDetailRecyclerView.adapter = AppDetailAdapter().apply {
                    setDataList(items, false)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.appDirs.collect { dirs ->
                dataBinding.appDirRecyclerView.adapter = AppDetailAdapter().apply {
                    setDataList(dirs, false)
                }
            }
        }
        lifecycleScope.launch {
            viewModel.appWidgets.collect { appWidgets ->
                dataBinding.appWidgetsLayout.removeAllViews()
                appWidgets.forEach {
                    it.isFocusable = true
                    dataBinding.appWidgetsLayout.addView(it)
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