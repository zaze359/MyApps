package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.adapters.AppListAdapter
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentAppListBinding
import com.zaze.apps.ext.onClick
import com.zaze.apps.ext.setupActionBar
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.viewmodels.AppListViewModel
import com.zaze.apps.viewmodels.AppUiState
import com.zaze.core.ext.normalNavOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Description :
 * @author : zaze
 * @version : 2017-04-17 05:15 1.0
 */
@AndroidEntryPoint
class AppListFragment : AbsFragment(), MenuProvider, AppOperator {

    private val viewModel: AppListViewModel by activityViewModels()
    private lateinit var binding: FragmentAppListBinding

    override val showLifeCycle: Boolean
        get() = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBar(binding.appBarLayout.toolbar) {
            it.setNavigationIcon(R.drawable.ic_search)
            it.setNavigationOnClickListener {
                findNavController().navigate(R.id.app_search_fragment, null, normalNavOptions)
            }
        }
        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.STARTED)
        binding.appExtractDataBtn.onClick { v ->
            lifecycleScope.launch {
                showToast("~~~~")
//                v.isEnabled = false
//                viewModel.extractApp().collect { filePath ->
//                    v.isEnabled = true
//                    showToast("已导出到: $filePath")
//                }
            }
        }
        binding.appResolvingApkCb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.loadSdcardApk()
            } else {
//                viewModel.loadAppList()
            }
        }
        binding.appListRecycleView.layoutManager = LinearLayoutManager(requireContext())
        //
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is AppUiState.AppList -> {
                            binding.appCountTv.text = "查询到 ${it.apps.size}个应用"
                            binding.appListRecycleView.adapter = AppListAdapter().apply {
                                submitList(it.apps)
                            }
                        }

                        else -> {

                        }
                    }
                }
//                viewModel.appData.collect { appList ->
//                    binding.appCountTv.text = "查询到 ${appList.size}个应用"
//                    binding.appListRecycleView.adapter = AppListAdapter().apply {
//                        submitList(appList)
//                    }
//                }
            }
        }
        viewModel.loadData()
    }

    override fun getApk(appShortcut: AppShortcut) {
//        viewModel.copyApkToSdcard(appShortcut)
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.action_settings).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_settings -> {
                findNavController().navigate(R.id.settings_fragment, null, normalNavOptions)
                true
            }

            else -> true
        }
    }

}
