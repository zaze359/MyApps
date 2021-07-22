package com.zaze.apps

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.zaze.apps.base.AbsActivity
import com.zaze.apps.databinding.ActivityAppListBinding
import com.zaze.apps.ext.myViewModels
import com.zaze.utils.ZOnClickHelper

/**
 * Description :

 * @author : zaze
 * *
 * @version : 2017-04-17 05:15 1.0
 */
class AppListActivity : AbsActivity() {
    private val viewModel: AppListViewModel by myViewModels()

    private var adapter: AppListAdapter? = null

    private lateinit var binding: ActivityAppListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.appData.observe(this@AppListActivity, Observer { appList ->
            binding.appCountTv.text = "查询到 ${appList.size}个应用"
            adapter?.setDataList(appList) ?: let {
                adapter = AppListAdapter(this@AppListActivity, appList)
                binding.appRecycleView.layoutManager = LinearLayoutManager(this@AppListActivity)
                binding.appRecycleView.adapter = adapter
            }
        })

        ZOnClickHelper.setOnClickListener(binding.appExtractBtn) {
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
        viewModel.loadAppList()
    }
}
