package com.rustfisher.githubonandroid.network;

import com.rustfisher.githubonandroid.network.bean.GitHubContributor;
import com.rustfisher.githubonandroid.network.service.GitHubService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;

/**
 * Deal with all network task.
 * Created by Rust Fisher on 2017/2/6.
 */
public class NetworkCenter {
    public static final String GITHUB_BASE_URL = "https://api.github.com/";
    public static final String GITHUB_CONTRIBUTORS_URL = "repos/{owner}/{repo}/contributors";

    private static Retrofit githubRetrofit = new Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    public static Retrofit getGithubRetrofit() {
        return githubRetrofit;
    }

    /**
     * @deprecated use rx
     */
    public static void getContributors(String owner, String repo, Callback callback) {
        GitHubService githubService = githubRetrofit.create(GitHubService.class);
        Call<List<GitHubContributor>> callSquare = githubService.contributors(owner, repo);
        callSquare.enqueue(callback);
    }


    public static Observable getContributorsObs(String owner, String repo) {
        return getGithubRetrofit().create(GitHubService.class).rxContributors(owner, repo);
    }
}
