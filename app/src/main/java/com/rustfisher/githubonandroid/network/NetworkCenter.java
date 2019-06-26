package com.rustfisher.githubonandroid.network;

import android.util.Log;

import com.rustfisher.githubonandroid.network.service.IGitHubService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Deal with all network task.
 * Created by Rust Fisher on 2017/2/6.
 */
public class NetworkCenter {
    private static final String TAG = "rustAppNetworkCenter ";
    public static final String K_REPO_NAME = "repo_name";
    public static final String K_OWNER = "owner";

    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    private static volatile NetworkCenter center = new NetworkCenter();

    public static NetworkCenter getCenter() {
        if (center == null) {
            synchronized (NetworkCenter.class) {
                if (center == null) {
                    center = new NetworkCenter();
                }
            }
        }
        return center;
    }

    private NetworkCenter() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(doNothingInterceptor)
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Log.d(TAG, "Interceptor2: intercept");
                        return chain.proceed(chain.request());
                    }
                })
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Log.d(TAG, "NetworkInterceptor1: intercept");
                        return chain.proceed(chain.request());
                    }
                })
                .build();
        Retrofit githubRetrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        gitHubService = githubRetrofit.create(IGitHubService.class);
        Log.d(TAG, "NetworkCenter 初始化完毕");
    }

    private IGitHubService gitHubService;

    public IGitHubService getGitHubService() {
        return gitHubService;
    }

    // 示例
    private Interceptor doNothingInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Log.d(TAG, "Interceptor1: intercept");
            return chain.proceed(chain.request());
        }
    };
}
