package com.zaze.apps.core.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.zaze.apps.core.ext.FragmentExtKt;

public abstract class AbsViewModelFragment extends AbsLogFragment {
    public AbsViewModelFragment() {
    }

    public AbsViewModelFragment(int contentLayoutId) {
        super(contentLayoutId);
    }

    @NonNull
    @Override
    public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
        return FragmentExtKt.obtainViewModelFactory(this, super.getDefaultViewModelProviderFactory());
    }
}
