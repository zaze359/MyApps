package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.zaze.apps.applist.AppListAdapter
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentAppSearchBinding
import com.zaze.apps.ext.initToolbar
import com.zaze.apps.applist.AppListViewModel
import com.zaze.apps.applist.AppUiState
import com.zaze.core.ext.focusAndShowKeyboard
import com.zaze.core.ext.hideKeyboard
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AppSearchFragment : AbsFragment() {

    private var _binding: FragmentAppSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AppListViewModel by activityViewModels()

    private lateinit var appListAdapter: AppListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)
        binding.searchEt.apply {
            doAfterTextChanged {
//                TransitionManager.beginDelayedTransition(binding.appBarLayout)
                if (it.isNullOrEmpty()) {
                    binding.clearIv.isGone = true
                } else {
                    binding.clearIv.isVisible = true
                    viewModel.searchApps(it.toString())
                }
            }
            focusAndShowKeyboard()
        }
        binding.clearIv.setOnClickListener {
            binding.searchEt.text = null
            viewModel.clearSearchInfo()
        }

        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest {
                    when (it) {
                        is AppUiState.AppList -> {
                            binding.empty.isVisible = it.searchedApps.isEmpty()
                            appListAdapter.submitList(it.searchedApps)
                        }
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        appListAdapter = AppListAdapter()
//        appListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
//            override fun onChanged() {
//                super.onChanged()
//                // 需要 调用 notifyDataSetChanged() 才会由回调。
//                // 使用 diffUtil 时不会调用，所以无回调
//                binding.empty.isVisible = appListAdapter.itemCount < 1
//            }
//        })
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = appListAdapter
            addOnScrollListener(object : OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    hideKeyboard()
//                    when (newState) {
//                        RecyclerView.SCROLL_STATE_IDLE -> {
//                            ZLog.i(ZTag.TAG, "SCROLL_STATE_IDLE")
//                        }
//
//                        RecyclerView.SCROLL_STATE_DRAGGING -> {
//                            ZLog.i(ZTag.TAG, "SCROLL_STATE_DRAGGING")
//                        }
//
//                        RecyclerView.SCROLL_STATE_SETTLING -> {
//                            ZLog.i(ZTag.TAG, "SCROLL_STATE_SETTLING")
//                        }
//
//                        else -> {
//                            ZLog.i(ZTag.TAG, "newState ${newState}")
//                        }
//                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.clearSearchInfo()
    }
}