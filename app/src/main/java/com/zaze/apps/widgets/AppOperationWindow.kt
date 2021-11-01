package com.zaze.apps.widgets

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.PopupWindow
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zaze.apps.HomePagerFragmentDirections
import com.zaze.apps.R
import com.zaze.apps.base.adapter.BaseRecyclerAdapter
import com.zaze.apps.data.OperationBean
import com.zaze.apps.databinding.AppOperationLayoutBinding
import com.zaze.apps.databinding.ItemAppOperationBinding
import com.zaze.apps.ext.onClick
import com.zaze.apps.utils.AppShortcut
import com.zaze.apps.utils.ApplicationManager
import com.zaze.apps.utils.SystemSettings
import com.zaze.utils.DisplayUtil
import com.zaze.utils.FileUtil
import com.zaze.utils.ToastUtil
import java.io.File

/**
 * Description :
 * @author : zaze
 * @version : 2021-10-27 - 16:39
 */
class AppOperationWindow(private val context: Context, private val app: AppShortcut) {

    companion object {
        private const val DURATION = 300L
    }

    private val slideInAnimation: AnimationSet by lazy {
        val animationSet = AnimationSet(false)
//            animationSet.addAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_bottom_in))
        animationSet.addAnimation(
            TranslateAnimation(
                Animation.RELATIVE_TO_PARENT,
                0F,
                Animation.RELATIVE_TO_PARENT,
                0F,
                Animation.RELATIVE_TO_PARENT,
                1F,
                Animation.RELATIVE_TO_PARENT,
                0F
            ).apply {
                interpolator = LinearInterpolator()
                duration = DURATION
            })
        animationSet
    }

    private val slideOutAnimation: AnimationSet by lazy {
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(TranslateAnimation(
            Animation.RELATIVE_TO_PARENT,
            0F,
            Animation.RELATIVE_TO_PARENT,
            0F,
            Animation.RELATIVE_TO_PARENT,
            0F,
            Animation.RELATIVE_TO_PARENT,
            1F
        ).apply {
            interpolator = LinearInterpolator()
            duration = DURATION
        })
        animationSet
    }

    private val alphaInAnimation by lazy {
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(AlphaAnimation(0F, 1F).apply {
            interpolator = LinearInterpolator()
            duration = DURATION
        })
        animationSet
    }

    private val alphaOutAnimation by lazy {
        val animationSet = AnimationSet(false)
        animationSet.addAnimation(AlphaAnimation(1F, 0F).apply {
            interpolator = LinearInterpolator()
            duration = DURATION
        })
        animationSet
    }

    private val binding = AppOperationLayoutBinding.inflate(LayoutInflater.from(context))

    private val popupWindow = PopupWindow(
        binding.root,
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT,
        false
    )

    private var enable = true

    init {
        // --------------------------------------------------
        // --------------------------------------------------
        binding.appOperationRecyclerView.layoutManager = GridLayoutManager(context, 3)
//        popupWindow.animationStyle = R.style.pop_anim

        binding.root.onClick {
            dismiss()
        }
        popupWindow.isClippingEnabled = false
//        popupWindow.contentView.fitsSystemWindows = true
//        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
//            popupWindow.isAttachedInDecor = true
//        }
    }

    // --------------------------------------------------
    fun show(parentView: View) {
        val operations = listOf(
            OperationBean(
                title = "启动",
                iconRes = R.drawable.ic_baseline_open_in_new_36,
                action = ::openApp
            ),
            OperationBean(
                title = "卸载",
                iconRes = R.drawable.ic_baseline_delete_36,
                action = ::uninstallApp
            ),
            OperationBean(
                title = "详情",
                iconRes = R.drawable.ic_baseline_assignment_36,
                action = {
                    parentView.findNavController()
                        .navigate(HomePagerFragmentDirections.appDetailAction(app.packageName))
                    dismiss()
                }
            ),
            OperationBean(
                title = "管理",
                iconRes = R.drawable.ic_baseline_settings_36,
                action = ::setting
            ),
            OperationBean(
                title = "提取apk",
                iconRes = R.drawable.ic_baseline_archive_36,
                action = ::archiveApp
            ),
//            OperationBean(title = "提取图标", iconRes = R.drawable.ic_baseline_image_36, action = {}),
//            OperationBean(title = "分享", iconRes = R.drawable.ic_baseline_apps_24, action = {}),
        )

        binding.appOperationRecyclerView.adapter = AppOperationAdapter().apply {
            setDataList(operations, false)
        }

        if (!enable) {
            return
        }
        if (!popupWindow.isShowing) {
            alphaInAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    enable = false
                }

                override fun onAnimationEnd(animation: Animation?) {
                    slideInAnimation.setAnimationListener(null)
                    enable = true
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            binding.root.startAnimation(alphaInAnimation)
            binding.appOperationRecyclerView.startAnimation(slideInAnimation)
            val displayProfile = DisplayUtil.getDisplayProfile()
            popupWindow.showAtLocation(
                parentView, Gravity.BOTTOM,
                0,
                displayProfile.realHeightPixels - displayProfile.heightPixels
            )
        }
    }

    fun dismiss() {
        if (!enable) {
            return
        }
        if (popupWindow.isShowing) {
            slideOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    enable = false
                }

                override fun onAnimationEnd(animation: Animation?) {
                    popupWindow.dismiss()
                    slideOutAnimation.setAnimationListener(null)
                    enable = true
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            binding.root.startAnimation(alphaOutAnimation)
            binding.appOperationRecyclerView.startAnimation(slideOutAnimation)
        }
    }
    // --------------------------------------------------

    private fun openApp() {
        ApplicationManager.openApp(context, app.packageName)
        dismiss()
    }

    private fun uninstallApp() {
        ApplicationManager.uninstallApp(context, app.packageName)
        dismiss()
    }

    private fun setting() {
        context.startActivity(SystemSettings.applicationDetailsSettings(app.packageName))
        dismiss()
    }

    private fun archiveApp() {
        val path =
            "${context.getExternalFilesDir(null)}/apks/${app.appName}_${app.versionName}.apk"
        val sourceDir = app.sourceDir
        if (sourceDir.isNullOrEmpty()) {
            ToastUtil.toast(context, "提取失败, 未找到${app.appName}的apk文件")
            return
        }
        FileUtil.copy(File(sourceDir), File(path))
        ToastUtil.toast(context, "已提取到: $path")
        dismiss()
    }

    // --------------------------------------------------
    private class AppOperationAdapter :
        BaseRecyclerAdapter<OperationBean, AppOperationAdapter.AppOperationHolder>() {

        override fun onBindView(holder: AppOperationHolder, value: OperationBean, position: Int) {
            holder.binding.appOperationTv.apply {
                text = value.title
                setCompoundDrawablesWithIntrinsicBounds(
                    0,
                    value.iconRes,
                    0,
                    0
                )
            }
            holder.binding.root.onClick {
                value.action()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppOperationHolder {
            return AppOperationHolder(
                ItemAppOperationBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }

        // --------------------------------------------------
        class AppOperationHolder(val binding: ItemAppOperationBinding) :
            RecyclerView.ViewHolder(binding.root)
    }
}