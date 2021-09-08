package com.zaze.apps.adapters

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.AppDetailActivity
import com.zaze.apps.R
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.base.BaseApplication
import com.zaze.apps.databinding.ItemAppListBinding
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.InvariantDeviceProfile
import com.zaze.utils.*
import com.zaze.utils.date.DateUtil
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag
import java.io.File

/**
 * Description :
 * @author : ZAZE
 *
 * @version : 2017-04-17 - 17:21
 */
class AppListAdapter(data: Collection<AppShortcut>) :
    BaseRecyclerAdapter<AppShortcut, AppListAdapter.PackageHolder>(data) {

    companion object {
        const val iconSize = 72
        val iconDpi by lazy {
            InvariantDeviceProfile().getLauncherIconDensity(iconSize)
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

    private fun formatTime(timeMillis: Long): String {
        return DateUtil.timeMillisToString(timeMillis, "yyyy-MM-dd HH:mm:ss")
    }

    inner class PackageHolder(val binding: ItemAppListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setup(value: AppShortcut) {
            val context = itemView.context
            val packageName = value.packageName
            // --------------------------------------------------
            binding.itemAppNumTv.text = "${position + 1}"
            if (value.isCopyEnable) {
                binding.itemAppExportBtn.visibility = View.VISIBLE
                binding.itemAppExportBtn.setOnClickListener {
                    val path = "/sdcard/zaze/apk/${value.appName}(${value.packageName}).apk"
                    if (value.isCopyEnable) {
                        FileUtil.copy(File(value.sourceDir), File(path))
                    }
                    ToastUtil.toast(context, "成功复制到 $path")
                }
            } else {
                binding.itemAppExportBtn.visibility = View.GONE
            }
            binding.itemAppNameTv.text = "应用名 : ${value.appName}"
            binding.itemAppVersionCodeTv.text = "版本号 ：${value.versionCode}"
            binding.itemAppVersionNameTv.text = "版本名 ：${value.versionName}"
            binding.itemAppPackageTv.text = "包名 : $packageName"
            binding.itemAppDirTv.text = "路径 : ${value.sourceDir}"
            binding.itemAppSignTv.text = "签名 : ${value.signingInfo}"
            binding.itemAppTimeTv.text =
                "应用时间 : ${formatTime(value.firstInstallTime)}/${formatTime(value.lastUpdateTime)}"
//        Settings.ACTION_WIFI_SETTINGS
            binding.itemAppIv.setImageBitmap(value.getAppIcon())
            binding.root.setOnClickListener {
                AppUtil.getAppMetaData(context, packageName)?.let { bundle ->
                    bundle.keySet().forEach {
                        ZLog.i(ZTag.TAG, "$it >> ${bundle.get(it)}")
                    }
                }
                context.startActivity(Intent(context, AppDetailActivity::class.java))
//            AppUtil.startApplication(context, packageName)
            }
        }
    }

}
