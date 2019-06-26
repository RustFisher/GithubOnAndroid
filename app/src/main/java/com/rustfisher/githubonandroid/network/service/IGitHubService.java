package com.rustfisher.githubonandroid.network.service;

import com.rustfisher.githubonandroid.network.bean.GitHubContributor;
import com.rustfisher.githubonandroid.network.bean.Repo;
import com.rustfisher.githubonandroid.network.bean.UserInfo;
import com.rustfisher.githubonandroid.network.bean.UserRepo;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface IGitHubService {
    @GET("repos/{owner}/{repo}/contributors")
    Call<List<GitHubContributor>> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @GET("repos/{owner}/{repo}/contributors")
    Observable<List<GitHubContributor>> rxContributors(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @GET("users/{owner}/repos")
    Observable<List<UserRepo>> userRepo(
            @Path("owner") String owner,
            @Query("sort") String sortType);

    @GET("repos/{owner}/{repo}")
    Observable<Repo> repo(
            @Path("owner") String owner,
            @Path("repo") String repo);

    // Get user information
    @GET("users/{username}")
    Observable<UserInfo> userInfo(@Path("username") String username);

}
