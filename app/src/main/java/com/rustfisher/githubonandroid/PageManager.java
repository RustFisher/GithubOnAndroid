package com.rustfisher.githubonandroid;


import android.app.Activity;

import java.util.ArrayList;

public class PageManager {

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

}
