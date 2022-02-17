package com.zaze.apps.appwidgets;

import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.UserHandle;

import java.text.Collator;

/**
 * An wrapper over various items displayed in a widget picker,
 * {@link LauncherAppWidgetProviderInfo} & {@link ActivityInfo}. This provides easier access to
 * common attributes like spanX and spanY.
 */
public class WidgetItem extends ComponentKey {

    private static UserHandle sMyUserHandle;
    private static Collator sCollator;

    public final LauncherAppWidgetProviderInfo widgetInfo;
//    public final ShortcutConfigActivityInfo activityInfo;

    public final String label;

    public WidgetItem(LauncherAppWidgetProviderInfo info, PackageManager pm) {
        super(info.provider, info.getProfile());
        label = info.getLabel(pm);
        widgetInfo = info;
    }

//    public WidgetItem(ShortcutConfigActivityInfo info) {
//        super(info.getComponent(), info.getUser());
//        label = Utilities.trim(info.getLabel());
//        widgetInfo = null;
//        activityInfo = info;
//        spanX = spanY = 1;
//    }
}
