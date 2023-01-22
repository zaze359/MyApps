package com.zaze.apps.ext

import android.app.Application
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import com.zaze.apps.base.AbsActivity
import com.zaze.apps.base.AbsAndroidViewModel
import com.zaze.apps.base.BaseApplication
import com.zaze.apps.base.AbsViewModel
import kotlin.reflect.KClass

class ViewModelFactory : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (AbsAndroidViewModel::class.java.isAssignableFrom(
                modelClass
            )
        ) {
            try {
                modelClass.getConstructor(Application::class.java)
                    .newInstance(BaseApplication.getInstance())
            } catch (e: Exception) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        } else super.create(modelClass)
    }
}

fun obtainViewModelFactory(): ViewModelFactory {
    return ViewModelFactory()
}

class MyViewModelLazy<VM : ViewModel>(
    private val activity: () -> ComponentActivity?,
    private val viewModelClass: KClass<VM>,
    private val storeProducer: () -> ViewModelStore,
    private val factoryProducer: () -> ViewModelProvider.Factory
) : Lazy<VM> {
    private var cached: VM? = null
    private var observed = false

    override val value: VM
        get() {
            val viewModel = cached
            return if (viewModel == null) {
                val factory = factoryProducer()
                val store = storeProducer()
                ViewModelProvider(store, factory).get(viewModelClass.java).also {
                    cached = it
                }
            } else {
                viewModel
            }.apply {
                observe(activity(), this)
            }
        }

    private fun observe(owner: ComponentActivity?, viewModel: ViewModel) {
        if (observed) {
            return
        }
        if (owner == null) {
            // Fragment not attached to an activity.
            observed = false
        } else {
            observed = true
            initAbsViewModel(owner, viewModel)
        }
    }

    override fun isInitialized(): Boolean = cached != null
}


fun initAbsViewModel(owner: ComponentActivity?, viewModel: ViewModel) {
    if (owner is AbsActivity && viewModel is AbsViewModel) {
        viewModel._showMessage.observe(owner, Observer {
            owner.showToast(it)
        })
        viewModel._finish.observe(owner, Observer {
            owner.finish()
        })
    }
}