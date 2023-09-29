package com.zaze.apps.ext

import android.content.pm.ApplicationInfo
import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.zaze.apps.base.AbsActivity
import com.zaze.apps.base.AbsViewModel
import com.zaze.utils.ZOnClickHelper

// ---------------------------------

fun initAbsViewModel(owner: ComponentActivity?, viewModel: ViewModel) {
    if (owner is AbsActivity && viewModel is AbsViewModel) {
        viewModel._showMessage.observe(owner, Observer {
            owner.showToast(it)
        })
        viewModel._progress.observe(owner, Observer {
            owner.progress(it)
        })
        viewModel._finish.observe(owner, Observer {
            owner.finish()
        })
    }
}
// ---------------------------------

fun <T : Any> MutableLiveData<T>.action() = postValue(null)

// ---------------------------------
fun View.onClick(block: (View) -> Unit) {
    ZOnClickHelper.setOnClickListener(this) {
        block(it)
    }
}

// ---------------------------------
fun ApplicationInfo.isSystemApp(): Boolean {
    return this.flags and ApplicationInfo.FLAG_SYSTEM > 0
}