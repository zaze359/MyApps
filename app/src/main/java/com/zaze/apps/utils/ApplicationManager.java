package com.zaze.apps.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.LruCache;

import androidx.core.content.res.ResourcesCompat;

import com.zaze.apps.R;
import com.zaze.apps.base.BaseApplication;
import com.zaze.utils.AppUtil;
import com.zaze.utils.BmpUtil;
import com.zaze.utils.cache.MemoryCacheManager;
import com.zaze.utils.log.ZLog;
import com.zaze.utils.log.ZTag;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

/**
 * Description : 对应用信息进行管理
 * 当应用发生变化时清空缓存, 否则有些从内存缓存中读取;
 * 仅当获取不到应用信息时尝试从文件缓存中读取(用于特殊场景).
 *
 * @author : ZAZE
 * @version : 2017-12-22 - 17:22
 */
public class ApplicationManager {
    private static InvariantDeviceProfile invariantDeviceProfile = new InvariantDeviceProfile();

    /**
     * 默认logo缓存
     */
    private static SoftReference<Bitmap> defaultLogoBmpRef;

    /**
     * 应用图标bitmap 缓存
     */
    private static final LruCache<String, Bitmap> BITMAP_CACHE = new LruCache<>(60);
    /**
     * 应用信息缓存
     */
    private static final Map<String, AppShortcut> SHORTCUT_CACHE = new HashMap<>();


    private static boolean allAppInitialized = false;
    // --------------------------------------------------

    private static void saveShortcutToCache(String packageName, AppShortcut appShortcut) {
        SHORTCUT_CACHE.put(packageName, appShortcut);
    }

    /**
     * 从引用中获取AppShortcut
     *
     * @param packageName packageName
     * @return appShortcut or null
     */
    private static AppShortcut getShortcutFromCache(String packageName) {
        return SHORTCUT_CACHE.get(packageName);
    }


    private static class Key {
        private static String getAppNameKey(String packageName) {
            return "am_name_" + packageName;
        }

        private static String getAppUidKey(int uid) {
            return "am_uid_" + uid;
        }

        private static String getAppVersionKey(String packageName) {
            return "am_version_" + packageName;
        }

        private static String getOtherAppPkgKey() {
            return "am_other_pkg";
        }
    }

    public static void clearAllCache() {
        BITMAP_CACHE.evictAll();
        SHORTCUT_CACHE.clear();
    }

    public static void clearCache(String packageName) {
        MemoryCacheManager.deleteCache(Key.getOtherAppPkgKey());
        clearAppCache(packageName);
    }

    private static void clearAppCache(String packageName) {
        AppShortcut appShortcut = getShortcutFromCache(packageName);
        if (appShortcut != null) {
            MemoryCacheManager.deleteCache(Key.getAppUidKey(appShortcut.getUid()));
        }
        MemoryCacheManager.deleteCache(Key.getAppNameKey(packageName));
        SHORTCUT_CACHE.remove(packageName);
        BITMAP_CACHE.remove(packageName);
    }

    // --------------------------------------------------

    /**
     * 根据包名获取应用信息
     * 优先读取内存缓存
     * 其次系统中读取
     *
     * @param packageName packageName
     * @return AppShortcut
     */
    public static @NotNull
    AppShortcut getAppShortcut(@NotNull String packageName) {
        AppShortcut appShortcut = getShortcutFromCache(packageName);
        if (appShortcut == null) {
            appShortcut = initAppShortcut(packageName);
        }
        return appShortcut;
    }

    /**
     * 根据包名获取应用信息
     * 优先读取内存缓存
     * 其次系统中读取
     *
     * @param apkFilePath apkFilePath
     * @return AppShortcut
     */
    public static AppShortcut getAppShortcutFormApk(@NotNull String apkFilePath) {
        PackageInfo packageInfo = AppUtil.getPackageArchiveInfo(BaseApplication.getInstance(), apkFilePath);
        if (packageInfo != null) {
            packageInfo.applicationInfo.sourceDir = apkFilePath;
            packageInfo.applicationInfo.publicSourceDir = apkFilePath;
            AppShortcut appShortcut = AppShortcut.transform(BaseApplication.getInstance(), packageInfo);
            appShortcut.setCopyEnable(false);
            return appShortcut;
        } else {
            return null;
        }
    }

    /**
     * 尝试将数据加载到缓存
     *
     * @param packageName packageName
     */
    public static void tryLoadAppShortcut(String packageName) {
        if (!TextUtils.isEmpty(packageName) && getShortcutFromCache(packageName) == null) {
            initAppShortcut(packageName);
        }
    }

    /**
     * 初始化应用缓存信息
     *
     * @param packageName packageName
     * @return AppShortcut
     */
    private static @NotNull
    AppShortcut initAppShortcut(@NotNull final String packageName) {
        return initAppShortcut(packageName, AppUtil.getPackageInfo(BaseApplication.getInstance(), packageName, 0));
    }

    private static @NotNull
    AppShortcut initAppShortcut(@NotNull String packageName, @Nullable PackageInfo packageInfo) {
        AppShortcut appShortcut;
        if (packageInfo == null) {
            appShortcut = AppShortcut.Companion.empty(packageName);
        } else {
            appShortcut = AppShortcut.transform(BaseApplication.getInstance(), packageInfo);
        }
        saveShortcutToCache(appShortcut.getPackageName(), appShortcut);
        return appShortcut;
    }

    // --------------------------------------------------
    // --------------------------------------------------

    /**
     * [packageName] packageName
     *
     * @return 应用图标
     */
    public static Bitmap getAppIconHasDefault(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        Bitmap bitmap = BITMAP_CACHE.get(packageName);
        if (bitmap != null) {
            return bitmap;
        }
        AppShortcut appShortcut = getAppShortcut(packageName);
        Drawable appIcon = null;
        ApplicationInfo applicationInfo = appShortcut.getApplicationInfo();
        if (applicationInfo != null) {
            Resources resources = getAppResources(applicationInfo);
            if (resources != null) {
                appIcon = getFullResIcon(resources, applicationInfo.icon);
            }
        }
        if (appIcon == null) {
            bitmap = ApplicationManager.getAppDefaultLogo();
        } else {
            bitmap = makeDefaultIcon(appIcon);
            BITMAP_CACHE.put(packageName, bitmap);
        }
        return bitmap;
    }

    public static Resources getAppResources(ApplicationInfo application) {
        if (application == null) {
            return null;
        }
        try {
            return BaseApplication.getInstance().getPackageManager().getResourcesForApplication(application);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 获取应用默认logo
     *
     * @return Bitmap
     */
    public static Bitmap getAppDefaultLogo() {
        Bitmap bitmap = null;
        if (defaultLogoBmpRef != null) {
            bitmap = defaultLogoBmpRef.get();
        }
        if (bitmap == null) {
            bitmap = makeDefaultIcon(getFullResIcon(BaseApplication.getInstance().getResources(),
                    R.mipmap.ic_launcher)
            );
            defaultLogoBmpRef = new SoftReference<>(bitmap);
        }
        return bitmap;
    }

    private static Drawable getFullResIcon(Resources resources, int iconId) {
        try {
            return ResourcesCompat.getDrawableForDensity(resources, iconId, invariantDeviceProfile.getFillResIconDpi(), null);
        } catch (Exception e) {
            return null;
        }
    }

    private static Bitmap makeDefaultIcon(Drawable drawable) {
        return BmpUtil.drawable2Bitmap(drawable, invariantDeviceProfile.getIconBitmapSize());
    }

    // --------------------------------------------------

    /**
     * [packageName] packageName packageName
     *
     * @return 应用图标（默认ic_launcher）
     */
    public static String getAppNameHasDefault(@Nullable String packageName, String defaultName) {
        if (packageName == null || packageName.isEmpty()) {
            return defaultName;
        }
        String name = getAppShortcut(packageName).getAppName();
        if (TextUtils.isEmpty(name)) {
            ZLog.e(ZTag.TAG_DEBUG, "未获取到应用名(%s), 使用默认(%s)", packageName, defaultName);
            name = defaultName;
        }
//        XHLog.i(LcTag.TAG_DEBUG, "获取到应用名(%s) : (%s)", packageName, name);
        return name;
    }

    public static Map<String, AppShortcut> getInstalledApps() {
        synchronized (SHORTCUT_CACHE) {
            if (SHORTCUT_CACHE.isEmpty() || !allAppInitialized) {
                List<PackageInfo> list = AppUtil.getInstalledPackages(BaseApplication.getInstance(), 0);
                for (PackageInfo packageInfo : list) {
                    AppShortcut shortcut = initAppShortcut(packageInfo.packageName, packageInfo);
                    SHORTCUT_CACHE.put(shortcut.getPackageName(), shortcut);
                }
                allAppInitialized = true;
            }
            return SHORTCUT_CACHE;
        }
    }

    // --------------------------------------------------
}
