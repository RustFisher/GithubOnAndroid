package com.rustfisher.githubonandroid.network;

import com.rustfisher.githubonandroid.network.bean.GitHubContributor;
import com.rustfisher.githubonandroid.network.service.IGitHubService;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
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
    public static final String K_REPO_FULL_NAME = "repo_full_name";
    public static final String K_REPO_NAME = "repo_name";
    public static final String K_OWNER = "owner";
    public static final String K_REPO_IS_FORK_FROM = "repo_is_fork_from"; // fork from other

    public static final String GITHUB_BASE_URL = "https://api.github.com/";
    public static final String GITHUB_CONTRIBUTORS_URL = "repos/{owner}/{repo}/contributors";
    public static final String GITHUB_REPO_URL = "repos/{owner}/{repo}";
    public static final String GITHUB_USER_REPO_URL = "users/{owner}/repos";

    private static OkHttpClient githubOKClient = new OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();

    private static Retrofit githubRetrofit = new Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
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

    public static Observable getUserRepoObs(String owner) {
        return getGithubRetrofit().create(IGitHubService.class).userRepo(owner);
    }

    // Get repo by repo full name
    public static Observable getRepoDetailObs(String owner, String repo) {
        return getGithubRetrofit().create(IGitHubService.class).repo(owner, repo);
    }
}
