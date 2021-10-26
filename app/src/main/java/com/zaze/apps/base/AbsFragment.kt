package com.zaze.apps.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ArrayRes
import androidx.annotation.DimenRes
import androidx.fragment.app.Fragment
import com.zaze.utils.ToastUtil
import com.zaze.utils.log.ZLog
import com.zaze.utils.log.ZTag

/**
 * Description :
 * @author : ZAZE
 * @version : 2018-11-30 - 00:00
 */
abstract class AbsFragment : Fragment() {

    companion object {
        var globalLog = true
        private const val TAG = "${ZTag.TAG}LifeCycle"
    }

    open fun showLifeCycle(): Boolean {
        return globalLog
    }

    private val fragmentName = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onCreateView")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onViewCreated")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onResume")
    }

    override fun onStart() {
        super.onStart()
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onStart")
    }

    override fun onStop() {
        super.onStop()
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onStop")
    }

    override fun onPause() {
        super.onPause()
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onPause")
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onAttach")
    }

    override fun onDetach() {
        super.onDetach()
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onDetach")
    }

    override fun onDestroyView() {
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onDestroyView")
        super.onDestroyView()
    }

    override fun onDestroy() {
        if (showLifeCycle())
            ZLog.i(TAG, "$fragmentName onDestroy")
        super.onDestroy()
    }
    // --------------------------------------------------

    fun showToast(resId: Int) {
        showToast(getString(resId))
    }

    fun showToast(content: String?) {
        ToastUtil.toast(context, content)
    }

    // --------------------------------------------------

    /**
     * 读取dimen 转 px
     */
    fun getDimen(@DimenRes resId: Int): Int {
        return this.resources.getDimensionPixelSize(resId)
    }

    /**
     * arrays.xml 转数据
     */
    fun getStringArray(@ArrayRes resId: Int): Array<String> {
        return this.resources.getStringArray(resId)
    }
}