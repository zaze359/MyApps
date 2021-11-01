package com.zaze.apps.widgets.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment

class CustomDialogFragment : DialogFragment() {
    private var builder: DialogProvider.Builder? = null
    private val customDialogHolder = CustomDialogHolder()

    fun init(builder: DialogProvider.Builder): CustomDialogFragment {
        this.builder = builder
        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        customDialogHolder.onCreateView(inflater, container)
        return customDialogHolder.binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        builder?.let {
            customDialogHolder.onViewCreated(dialog, it)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        builder?.let {
            isCancelable = it.cancelable
            customDialogHolder.onCreateDialog(dialog, it)
        }
        return dialog
    }

    override fun getTheme(): Int {
        return builder?.theme ?: super.getTheme()
    }

    override fun onStart() {
        super.onStart()
        customDialogHolder.measure(dialog)
    }

    fun show(manager: FragmentManager) {
        super.show(manager, builder?.tag)
    }
}