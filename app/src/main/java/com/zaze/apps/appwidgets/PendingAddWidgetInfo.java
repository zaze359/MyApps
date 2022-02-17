/*
 * Copyright (C) 2015 The Android Open Source Project
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
package com.zaze.apps.appwidgets;

import android.os.UserHandle;


/**
 * Meta data used for late binding of {@link LauncherAppWidgetProviderInfo}.
 *
 * @see {@link PendingAddItemInfo}
 */
public class PendingAddWidgetInfo extends PendingAddItemInfo {
    public int previewImage;
    public int icon;
    public LauncherAppWidgetProviderInfo info;
    //    public AppWidgetHostView boundWidget;
//    public Bundle bindOptions = null;
    public UserHandle user;

    public PendingAddWidgetInfo(LauncherAppWidgetProviderInfo i) {
        this.info = i;
        user = i.getProfile();
        componentName = i.provider;
        previewImage = i.previewImage;
        icon = i.icon;
    }

    public boolean needsConfigure() {
        return info.configure != null;
    }
}
