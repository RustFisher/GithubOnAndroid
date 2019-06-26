package com.rustfisher.githubonandroid.widget;

import com.rustfisher.githubonandroid.network.bean.UserRepo;

import java.util.ArrayList;
import java.util.List;

public class UserRepoInfo {
    private UserRepo userRepo;
    private String parentInfo;
    private boolean isFork;

    public UserRepoInfo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public UserRepo getUserRepo() {
        return userRepo;
    }

    public void setUserRepo(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public String getParentInfo() {
        if (userRepo.isFork()) {
            return parentInfo;
        }
        return "";
    }

    public void setParentInfo(String parentInfo) {
        this.parentInfo = parentInfo;
    }

    public boolean isFork() {
        return isFork;
    }

    public void setFork(boolean fork) {
        isFork = fork;
    }

    public static ArrayList<UserRepoInfo> packUserRepo(List<UserRepo> userRepoList) {
        ArrayList<UserRepoInfo> infoList = new ArrayList<>();
        for (UserRepo userRepo : userRepoList) {
            infoList.add(new UserRepoInfo(userRepo));
        }
        return infoList;
    }

}
