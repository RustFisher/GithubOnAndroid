package com.rustfisher.githubonandroid;


import android.app.Activity;
import android.content.Context;

import java.util.ArrayList;

public class PageManager {

    public static final String GIA_CONFIG = "gia_config";
    public static final String K_USER_NAME = "key_user_name";

    private static ArrayList<Activity> repoActList = new ArrayList<>();
    private static ArrayList<String> ownerHistoryTextList = new ArrayList<>();

    public static ArrayList<String> getOwnerHistoryTextListCp() {
        return new ArrayList<>(ownerHistoryTextList);
    }

    public static void setOwnerHistoryTextList(ArrayList<String> ownerHistoryTextList) {
        PageManager.ownerHistoryTextList = ownerHistoryTextList;
    }

    public static int getRepoActListSize() {
        return repoActList.size();
    }

    public static boolean addRepoActivity(Activity activity) {
        return repoActList.add(activity);
    }

    public static boolean removeRepoAct(Activity activity) {
        return repoActList.remove(activity);
    }

    public static void finishRepoAct() {
        for (Activity activity : repoActList) {
            activity.finish();
        }
        repoActList.clear();
    }

    /**
     * The last Github login name
     */
    public static void saveUserName(String userName) {
        GApp.getApp().getSharedPreferences(GIA_CONFIG, Context.MODE_PRIVATE)
                .edit().putString(K_USER_NAME, userName).apply();
    }

    public static String getLastUserName() {
        return GApp.getApp().getSharedPreferences(GIA_CONFIG, Context.MODE_PRIVATE)
                .getString(K_USER_NAME, "");
    }

}
