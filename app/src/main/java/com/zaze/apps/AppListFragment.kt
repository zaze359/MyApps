package com.zaze.apps

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.adapters.AppListAdapter
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.base.AbsSlidingPanelFragment
import com.zaze.apps.databinding.FragmentAppListBinding
import com.zaze.apps.ext.myViewModels
import com.zaze.apps.ext.onClick
import com.zaze.apps.viewmodels.AppListViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Description :

 * @author : zaze
 * *
 * @version : 2017-04-17 05:15 1.0
 */
@AndroidEntryPoint
class AppListFragment : AbsFragment() {
    private val viewModel: AppListViewModel by myViewModels()

    private var adapter: AppListAdapter? = null

    private lateinit var binding: FragmentAppListBinding

    override fun showLifeCycle(): Boolean {
        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.appData.observe(viewLifecycleOwner, Observer { appList ->
            binding.appCountTv.text = "查询到 ${appList.size}个应用"
            binding.appListRecycleView.adapter = AppListAdapter().apply {
                setDataList(appList, false)
            }
        })

        binding.appExtractBtn.onClick {
            viewModel.extractApp()
        }

        binding.appResolvingApkCb.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.loadSdcardApk()
            } else {
                viewModel.loadAppList()
            }
        }
        binding.appSearchEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.filterApp(s.toString())
            }
        })
        binding.appListRecycleView.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launchWhenResumed {
            viewModel.loadAppList()
        }
    }
}
