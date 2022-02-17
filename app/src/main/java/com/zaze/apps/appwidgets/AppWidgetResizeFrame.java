package com.zaze.apps.appwidgets;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;

public class AppWidgetResizeFrame {
    private static Point[] sCellSize;

    public static Rect getWidgetSizeRanges(Context context, int spanX, int spanY, Rect rect) {
        if (sCellSize == null) {
            // Initiate cell sizes.
            sCellSize = new Point[2];
//            InvariantDeviceProfile inv = ApplicationManager.INSTANCE.getInvariantDeviceProfile();
//            sCellSize[0] = inv.landscapeProfile.getCellSize();
//            sCellSize[1] = inv.portraitProfile.getCellSize();
            sCellSize[0] = new Point(411, 261);
            sCellSize[1] = new Point(269, 376);
        }
        if (rect == null) {
            rect = new Rect();
        }
        final float density = context.getResources().getDisplayMetrics().density;

        // Compute landscape size
        int landWidth = (int) ((spanX * sCellSize[0].x) / density);
        int landHeight = (int) ((spanY * sCellSize[0].y) / density);

        // Compute portrait size
        int portWidth = (int) ((spanX * sCellSize[1].x) / density);
        int portHeight = (int) ((spanY * sCellSize[1].y) / density);
        rect.set(portWidth, landHeight, landWidth, portHeight);
        return rect;
    }
}
