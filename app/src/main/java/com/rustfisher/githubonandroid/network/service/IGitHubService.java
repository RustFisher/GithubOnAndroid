package com.rustfisher.githubonandroid.network.service;

import com.rustfisher.githubonandroid.network.NetworkCenter;
import com.rustfisher.githubonandroid.network.bean.GitHubContributor;
import com.rustfisher.githubonandroid.network.bean.Repo;
import com.rustfisher.githubonandroid.network.bean.UserRepo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface IGitHubService {
    @GET(NetworkCenter.GITHUB_CONTRIBUTORS_URL)
    Call<List<GitHubContributor>> contributors(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @GET(NetworkCenter.GITHUB_CONTRIBUTORS_URL)
    Observable<List<GitHubContributor>> rxContributors(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @GET(NetworkCenter.GITHUB_USER_REPO_URL)
    Observable<List<UserRepo>> userRepo(
            @Path("owner") String owner);

    @GET(NetworkCenter.GITHUB_REPO_URL)
    Observable<Repo> repo(
            @Path("owner") String owner,
            @Path("repo") String repo);

}
