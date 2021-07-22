package com.zaze.apps.base

import androidx.annotation.ArrayRes
import androidx.annotation.DimenRes
import com.zaze.utils.ToastUtil

/**
 * Description :
 *
 * @author : ZAZE
 * @version : 2018-11-30 - 15:48
 */
abstract class AbsActivity : AbsPermissionsActivity() {

    // --------------------------------------------------
    fun showToast(resId: Int) {
        showToast(getString(resId))
    }

    fun showToast(content: String?) {
        ToastUtil.toast(this, content)
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
    // --------------------------------------------------

}

