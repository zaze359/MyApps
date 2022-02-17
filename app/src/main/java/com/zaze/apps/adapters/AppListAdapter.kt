package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.databinding.ItemAppListBinding
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.widgets.AppOperationWindow

/**
 * Description :
 * @author : ZAZE
 *
 * @version : 2017-04-17 - 17:21
 */
class AppListAdapter : BaseRecyclerAdapter<AppShortcut, AppListAdapter.PackageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackageHolder {
        return PackageHolder(
            ItemAppListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindView(holder: PackageHolder, value: AppShortcut, position: Int) {
        holder.setup(value)
    }

    inner class PackageHolder(val binding: ItemAppListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setup(value: AppShortcut) {
            binding.appNameTv.text = value.appName
            binding.appVersionNameTv.text = "版本: ${value.versionName}"
            binding.appIconIv.setImageBitmap(value.appIcon)
            binding.root.setOnClickListener {
//                AppUtil.getAppMetaData(context, packageName)?.let { bundle ->
//                    bundle.keySet().forEach {
//                        ZLog.i(ZTag.TAG, "$it >> ${bundle.get(it)}")
//                    }
//                }
                AppOperationWindow(it.context, value).show(it)
//                it.findNavController()
//                    .navigate(AppListFragmentDirections.appDetailAction(value.packageName))
            }
        }
    }

}
