package com.zaze.apps.ext

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) {
    supportFragmentManager.transact {
        replace(frameId, fragment)
    }
}

fun AppCompatActivity.addFragment(fragment: Fragment, tag: String) {
    supportFragmentManager.transact {
        add(fragment, tag)
    }
}

fun AppCompatActivity.hideFragment(fragment: Fragment) {
    supportFragmentManager.transact {
        hide(fragment)
    }
}

fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commit()
}

fun FragmentManager.transactAllowingStateLoss(action: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        action()
    }.commitAllowingStateLoss()
}

// --------------------------------------------------
fun ComponentActivity.obtainViewModelFactory(): ViewModelFactory {
    return object : ViewModelFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return super.create(modelClass).also { vm ->
                initAbsViewModel(this@obtainViewModelFactory, vm)
            }
        }
    }
}

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.myViewModels(): Lazy<VM> = viewModels {
    obtainViewModelFactory()
}
// --------------------------------------------------