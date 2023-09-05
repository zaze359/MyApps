package com.zaze.apps.applist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.App
import com.zaze.apps.R
import com.zaze.apps.base.adapter.AbsRecyclerAdapter
import com.zaze.apps.data.AppSort
import com.zaze.apps.databinding.ItemAppListBinding
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.widget.AppOperationMenu
import com.zaze.core.ext.normalNavOptions
import com.zaze.utils.DescriptionUtil
import com.zaze.utils.date.DateUtil

/**
 * Description :
 * @author : ZAZE
 *
 * @version : 2017-04-17 - 17:21
 */
class AppListAdapter :
    AbsRecyclerAdapter<AppShortcut, AppListAdapter.PackageHolder>(diffCallback) {
    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<AppShortcut>() {
            override fun areItemsTheSame(oldItem: AppShortcut, newItem: AppShortcut): Boolean {
                return oldItem.packageName == newItem.packageName
            }

            override fun areContentsTheSame(oldItem: AppShortcut, newItem: AppShortcut): Boolean {
                return oldItem.toString() == newItem.toString()
            }
        }
        private const val iconSize = 72
        private const val pattern = "yyyy年MM月dd日 HH:mm"
//        private val iconDpi by lazy { InvariantDeviceProfile().getLauncherIconDensity(iconSize) }
    }

    private var sortType: AppSort = AppSort.Name

    fun submitList(list: List<AppShortcut>?, sortType: AppSort) {
        super.submitList(list)
        if (this.sortType != sortType) {
            this.sortType = sortType
            notifyDataSetChanged()
        }
    }

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
            binding.appIconIv.setImageBitmap(value.getAppIcon(App.getInstance()))
            binding.menuIv.setOnClickListener {
//                AppOperationWindow(it.context, value).show(it)
                AppOperationMenu(it.context, it, value).show()
            }

            binding.appDescTv.text = when (sortType) {
                AppSort.Size -> {
                    "大小 ${DescriptionUtil.toByteUnit(value.apkSize)}"
                }

                AppSort.InstallTime -> {
                    "安装时间 ${
                        DateUtil.timeMillisToString(value.firstInstallTime, pattern)
                    }"
                }

                AppSort.UpdateTime -> {
                    "更新时间 ${
                        DateUtil.timeMillisToString(value.lastUpdateTime, pattern)
                    }"
                }

                else -> {
                    "版本: ${value.versionName}"
                }
            }

            binding.root.setOnClickListener {
//                AppUtil.getAppMetaData(context, packageName)?.let { bundle ->
//                    bundle.keySet().forEach {
//                        ZLog.i(ZTag.TAG, "$it >> ${bundle.get(it)}")
//                    }
//                }

                it.findNavController().navigate(R.id.app_detail_fragment, Bundle().apply {
                    putString("packageName", value.packageName)
                }, normalNavOptions)
//                it.findNavController()
//                    .navigate(AppListFragmentDirections.appDetailAction(value.packageName))
            }
        }
    }

}
