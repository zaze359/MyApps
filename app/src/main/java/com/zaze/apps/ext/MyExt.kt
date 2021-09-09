package com.zaze.apps.ext

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.zaze.utils.ZOnClickHelper

fun <T : Any> MutableLiveData<T>.action() = postValue(null)


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.onClick(block: (View?) -> Unit) {
    ZOnClickHelper.setOnClickListener(this) {
        block(it)
    }
}
