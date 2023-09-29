package com.zaze.apps.applist

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
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Orientation
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.zaze.apps.AppOperator
import com.zaze.apps.MenuHandler
import com.zaze.apps.MenuHandler.handlenMenuItemSelected
import com.zaze.apps.R
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.data.AppFilter
import com.zaze.apps.data.AppSort
import com.zaze.apps.databinding.FragmentAppListBinding
import com.zaze.apps.ext.onClick
import com.zaze.apps.ext.setupActionBar
import com.zaze.apps.utils.AppShortcut
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

    private lateinit var binding: FragmentAppListBinding
    private lateinit var appListAdapter: AppListAdapter
    private lateinit var appFilterAdapter: AppFilterAdapter

    private val viewModel: AppListViewModel by activityViewModels()

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
//        binding.appResolvingApkCb.setOnCheckedChangeListener { _, isChecked ->
//            if (isChecked) {
//                viewModel.loadSdcardApk()
//            } else {
////                viewModel.loadAppList()
//            }
//        }
        setupFilterRecycleView()
        setupAppListRecycleView()
        //
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is AppUiState.AppList -> {
                            binding.appCountTv.text = "查询到 ${it.apps.size}个应用"
                            appListAdapter.submitList(it.apps, it.sortType)
                            appFilterAdapter.submitList(it.appFilterList, it.appFilter)
                        }
                    }
                }
            }
        }
        viewModel.loadData()
    }

    private fun setupFilterRecycleView() {
        binding.appFilterRecyclerView.apply {
//            layoutManager =
//                StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL)
            layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            appFilterAdapter = AppFilterAdapter(viewModel::loadData)
            adapter = appFilterAdapter
        }
    }

    private fun setupAppListRecycleView() {
        binding.appListRecycleView.layoutManager = LinearLayoutManager(requireContext())
        appListAdapter = AppListAdapter()
        binding.appListRecycleView.adapter = appListAdapter
        binding.appListRecycleView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                when {
//                    !recyclerView.canScrollVertically(1) -> { // 滑动底部时，显示
//                        binding.moveToTopButton.show()
//                    }
                    !recyclerView.canScrollVertically(-1) -> { // 滑动顶部时，隐藏
                        binding.moveToTopButton.hide()
                    }

                    dy > 0 -> { // 向下滑动 ，隐藏
                        binding.moveToTopButton.hide()
                    }

                    dy < 0 -> { // 向上滑动，显示
                        binding.moveToTopButton.show()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        binding.moveToTopButton.hide()
        binding.moveToTopButton.setOnClickListener {
            binding.appListRecycleView.smoothScrollToPosition(0)
            binding.moveToTopButton.hide()
        }
    }


    override fun getApk(appShortcut: AppShortcut) {
//        viewModel.copyApkToSdcard(appShortcut)
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_applist, menu)
        MenuHandler.setupAppSortMenu(menu, AppSort.Name)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (handlenMenuItemSelected(menuItem)) {
            return true
        }
        return MenuHandler.handleAppSortOrder(menuItem, viewModel::sortBy)
    }

}
