package com.zaze.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.adapters.AppDetailAdapter
import com.zaze.apps.base.AbsFragment
import com.zaze.apps.databinding.FragmentAppDetailBinding
import com.zaze.apps.ext.myViewModels
import com.zaze.apps.viewmodels.AppDetailViewModel
import kotlinx.coroutines.flow.collect

/**
 * Description :
 * @author : zaze
 * @version : 2021-08-04 - 09:24
 */
class AppDetailFragment : AbsFragment() {
    private lateinit var dataBinding: FragmentAppDetailBinding
    private val viewModel: AppDetailViewModel by myViewModels()

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
        lifecycleScope.launchWhenResumed {
            viewModel.loadAppDetail(safeArgs.packageName).collect { appDetail ->
                dataBinding.appNameTv.text = appDetail.appName
                dataBinding.appVersionNameTv.text = appDetail.appVersion
                dataBinding.appIconIv.setImageBitmap(appDetail.appIcon)
                // --------------------------------------------------
                dataBinding.appDetailRecyclerView.adapter = AppDetailAdapter().apply {
                    setDataList(appDetail.appDetailItems, false)
                }
                // --------------------------------------------------
                dataBinding.appDirRecyclerView.adapter = AppDetailAdapter().apply {
                    setDataList(appDetail.appDirs, false)
                }
            }
        }
    }
}