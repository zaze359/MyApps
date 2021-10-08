package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentOverviewBinding
import com.zaze.apps.adapters.CardsAdapter
import com.zaze.apps.utils.AppUsageHelper
import com.zaze.apps.viewmodels.OverviewViewModel
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : zaze
 * @version : 2021-07-21 - 16:14
 */
class OverviewFragment : AbsFragment() {
    private lateinit var binding: FragmentOverviewBinding
    private val viewModel: OverviewViewModel by viewModels()
    private val appUsagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            ZLog.i(ZTag.TAG, "result: ${it.resultCode}, ${it.data}")
            lifecycleScope.launchWhenResumed {
                viewModel.loadOverview()
            }
        }

    override fun showLifeCycle(): Boolean {
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.overviewRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewModel.overviewListData.observe(viewLifecycleOwner) {
            binding.overviewRecyclerView.adapter = CardsAdapter(it)
        }
        viewModel.showAppsAction.observe(viewLifecycleOwner) {}
        viewModel.requestAppUsagePermissionAction.observe(viewLifecycleOwner) {
            AppUsageHelper.requestAppUsagePermission(appUsagePermissionLauncher)
        }
        viewModel.settingsAction.observe(viewLifecycleOwner) {
            startActivity(it)
        }
        lifecycleScope.launchWhenResumed {
            viewModel.loadOverview()
        }
    }
}