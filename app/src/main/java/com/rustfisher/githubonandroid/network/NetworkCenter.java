package com.rustfisher.githubonandroid.network;

import com.rustfisher.githubonandroid.network.bean.GitHubContributor;
import com.rustfisher.githubonandroid.network.service.IGitHubService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Deal with all network task.
 * Created by Rust Fisher on 2017/2/6.
 */
public class NetworkCenter {
    public static final String K_REPO_NAME = "repo_name";
    public static final String K_OWNER = "owner";

    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    private static OkHttpClient githubOKClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    private static Retrofit githubRetrofit = new Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(githubOKClient)
            .build();

    public static Retrofit getGithubRetrofit() {
        return githubRetrofit;
    }

    /**
     * @deprecated use rx
     */
    public static void getContributors(String owner, String repo, Callback callback) {
        IGitHubService githubServiceI = githubRetrofit.create(IGitHubService.class);
        Call<List<GitHubContributor>> callSquare = githubServiceI.contributors(owner, repo);
        callSquare.enqueue(callback);
    }


    public static Observable getContributorsObs(String owner, String repo) {
        return getGithubRetrofit().create(IGitHubService.class).rxContributors(owner, repo);
    }

    // Get repos by owner name
    public static Observable getUserRepoObs(String owner) {
        return getGithubRetrofit().create(IGitHubService.class).userRepo(owner, "pushed");
    }

    // Get repo by repo full name
    public static Observable getRepoDetailObs(String owner, String repo) {
        return getGithubRetrofit().create(IGitHubService.class).repo(owner, repo);
    }

    public static Observable getUserInformationObs(String username) {
        return getGithubRetrofit().create(IGitHubService.class).userInfo(username);
    }
}
