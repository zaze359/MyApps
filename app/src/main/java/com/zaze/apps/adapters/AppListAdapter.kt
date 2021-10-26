package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.*
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.databinding.ItemAppListBinding
import com.zaze.apps.utils.AppShortcut
import com.zaze.utils.*
import java.io.File

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
            val context = itemView.context
            val packageName = value.packageName
            // --------------------------------------------------
//            if (value.isCopyEnable) {
//                binding.itemAppExportBtn.visibility = View.VISIBLE
//                binding.itemAppExportBtn.setOnClickListener {
//                    val path = "/sdcard/zaze/apk/${value.appName}(${value.packageName}).apk"
//                    if (value.isCopyEnable) {
//                        FileUtil.copy(File(value.sourceDir), File(path))
//                    }
//                    ToastUtil.toast(context, "成功复制到 $path")
//                }
//            } else {
//                binding.itemAppExportBtn.visibility = View.GONE
//            }

            binding.appNameTv.text = value.appName
            binding.appVersionNameTv.text = "版本: ${value.versionName}"
//        Settings.ACTION_WIFI_SETTINGS
            binding.appIconIv.setImageBitmap(value.appIcon)
            binding.root.setOnClickListener {
//                AppUtil.getAppMetaData(context, packageName)?.let { bundle ->
//                    bundle.keySet().forEach {
//                        ZLog.i(ZTag.TAG, "$it >> ${bundle.get(it)}")
//                    }
//                }
                it.findNavController()
                    .navigate(AppListFragmentDirections.appDetailAction(value.packageName))

//            AppUtil.startApplication(context, packageName)
            }
        }
    }

}
