package com.rustfisher.githubonandroid;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class GApp extends Application {

    private static volatile GApp mApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    public static GApp getApp() {
        if (null == mApp) {
            synchronized (GApp.class) {
                if (mApp == null) {
                    mApp = new GApp();
                }
            }
        }
        return mApp;
    }

    public static String getVersionName() {
        try {
            PackageManager manager = getApp().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getApp().getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown version";
        }
    }

}
