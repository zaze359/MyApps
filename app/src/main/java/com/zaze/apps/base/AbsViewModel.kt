package com.zaze.apps.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zaze.apps.utils.SingleLiveEvent

/**
 * Description :
 * @author : ZAZE
 * @version : 2020-05-06 - 11:27
 */
open class AbsViewModel : ViewModel() {
    /**
     * 拖拽刷新loading
     */
    val dragLoading = SingleLiveEvent<Boolean>()

    /**
     * 数据加载状态
     */
    val dataLoading = SingleLiveEvent<Boolean>()

    // --------------------------------------------------
    // obtain时已默认 observe
    /**
     * 提示信息
     */
    internal val _showMessage = SingleLiveEvent<String>()
    protected val showMessage = _showMessage

    /**
     * 是否退出
     * finish()
     */
    internal val _finish = SingleLiveEvent<Void>()
    protected val finish = _finish

    // --------------------------------------------------

    fun toastMessage(message: String) {
        showMessage.postValue(message)
    }

    /**
     * 是否处于刷新中
     */
    open fun isLoading(): Boolean {
        return dataLoading.value == true || dragLoading.value == true
    }

    override fun onCleared() {
        super.onCleared()
    }
}