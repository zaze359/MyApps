package com.zaze.apps.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.zaze.apps.ext.FragmentExtKt;

public abstract class AbsViewModelFragment extends AbsPermissionFragment {
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
