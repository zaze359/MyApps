/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zaze.apps.appwidgets.compat;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.UserHandle;
import android.os.UserManager;

import androidx.annotation.Nullable;

import com.zaze.apps.appwidgets.ComponentKey;
import com.zaze.apps.appwidgets.LauncherAppWidgetProviderInfo;
import com.zaze.apps.appwidgets.PackageUserKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

class AppWidgetManagerCompatVL extends AppWidgetManagerCompat {

    private final UserManager mUserManager;

    AppWidgetManagerCompatVL(Context context) {
        super(context);
        mUserManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
    }

    @Override
    public List<AppWidgetProviderInfo> getAllProviders(@Nullable PackageUserKey packageUser) {
        if (packageUser == null) {
            ArrayList<AppWidgetProviderInfo> providers = new ArrayList<>();
            for (UserHandle user : mUserManager.getUserProfiles()) {
                providers.addAll(mAppWidgetManager.getInstalledProvidersForProfile(user));
            }
            return providers;
        }
        // Only get providers for the given package/user.
        List<AppWidgetProviderInfo> providers = new ArrayList<>(mAppWidgetManager
                .getInstalledProvidersForProfile(packageUser.mUser));
        Iterator<AppWidgetProviderInfo> iterator = providers.iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().provider.getPackageName().equals(packageUser.mPackageName)) {
                iterator.remove();
            }
        }
        return providers;
    }

    @Override
    public boolean bindAppWidgetIdIfAllowed(int appWidgetId, AppWidgetProviderInfo info,
                                            Bundle options) {
        return mAppWidgetManager.bindAppWidgetIdIfAllowed(
                appWidgetId, info.getProfile(), info.provider, options);
    }

    @Override
    public LauncherAppWidgetProviderInfo findProvider(ComponentName provider, UserHandle user) {
        for (AppWidgetProviderInfo info :
                getAllProviders(new PackageUserKey(provider.getPackageName(), user))) {
            if (info.provider.equals(provider)) {
                return LauncherAppWidgetProviderInfo.fromProviderInfo(info);
            }
        }
        return null;
    }

    @Override
    public HashMap<ComponentKey, AppWidgetProviderInfo> getAllProvidersMap() {
        HashMap<ComponentKey, AppWidgetProviderInfo> result = new HashMap<>();
        for (UserHandle user : mUserManager.getUserProfiles()) {
            for (AppWidgetProviderInfo info :
                    mAppWidgetManager.getInstalledProvidersForProfile(user)) {
                result.put(new ComponentKey(info.provider, user), info);
            }
        }
        return result;
    }
}
