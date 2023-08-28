package com.zaze.apps.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.App
import com.zaze.apps.AppListFragmentDirections
import com.zaze.apps.R
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.databinding.ItemAppListBinding
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.widget.AppOperationMenu
import com.zaze.apps.widget.AppOperationWindow

/**
 * Description :
 * @author : ZAZE
 *
 * @version : 2017-04-17 - 17:21
 */
class AppListAdapter :
    BaseRecyclerAdapter<AppShortcut, AppListAdapter.PackageHolder>(diffCallback) {
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
//        private val iconDpi by lazy { InvariantDeviceProfile().getLauncherIconDensity(iconSize) }
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
            binding.appVersionNameTv.text = "版本: ${value.versionName}"
            binding.appIconIv.setImageBitmap(value.getAppIcon(App.getInstance()))
            binding.menuIv.setOnClickListener {
//                AppOperationWindow(it.context, value).show(it)
                AppOperationMenu(it.context, it, value).show()
            }
            binding.root.setOnClickListener {
//                AppUtil.getAppMetaData(context, packageName)?.let { bundle ->
//                    bundle.keySet().forEach {
//                        ZLog.i(ZTag.TAG, "$it >> ${bundle.get(it)}")
//                    }
//                }
                it.findNavController()
                    .navigate(AppListFragmentDirections.appDetailAction(value.packageName))
            }
        }
    }

}
