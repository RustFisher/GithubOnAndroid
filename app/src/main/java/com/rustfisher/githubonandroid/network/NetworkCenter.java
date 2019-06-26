package com.rustfisher.githubonandroid.network;

import android.text.TextUtils;
import android.util.Log;

import com.rustfisher.githubonandroid.network.service.IGitHubService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    public static final String HEADER_ACT_NAME = "Activity-Name"; // 标记Activity界面名字

    private static final String GITHUB_BASE_URL = "https://api.github.com/";

    private static volatile NetworkCenter center = new NetworkCenter();
    private IGitHubService gitHubService;
    private OkHttpClient githubOkHttpClient;

    private static ConcurrentHashMap<String, Boolean> actLiveMap = new ConcurrentHashMap<>(); // 标记Activity是否存活

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
        githubOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logInterceptor)
                .addInterceptor(lifeInterceptor)
                .build();
        Retrofit githubRetrofit = new Retrofit.Builder()
                .baseUrl(GITHUB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(githubOkHttpClient)
                .build();
        gitHubService = githubRetrofit.create(IGitHubService.class);
        Log.d(TAG, "NetworkCenter 初始化完毕");
    }

    public IGitHubService getGitHubService() {
        return gitHubService;
    }

    public OkHttpClient getGithubOkHttpClient() {
        return githubOkHttpClient;
    }

    // 用来打log
    private Interceptor logInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Call call = chain.call();
            Log.d(TAG, "logInterceptor: chain: " + chain);
            Log.d(TAG, "              request: " + request);
            Log.d(TAG, "                 call: " + call);
            return chain.proceed(chain.request());
        }
    };

    private Interceptor lifeInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String actName = request.header(HEADER_ACT_NAME);
            if (!TextUtils.isEmpty(actName)) {
                Log.d(TAG, "lifeInterceptor: actName: " + actName);
                Boolean actLive = actLiveMap.get(actName);
                if (actLive == null || !actLive) {
                    chain.call().cancel();
                    Log.d(TAG, "lifeInterceptor: 取消请求, actName: " + actName);
                } else {
                    Log.d(TAG, "lifeInterceptor: 发起请求, actName: " + actName);
                }
            }
            Request newRequest = request.newBuilder().removeHeader(HEADER_ACT_NAME).build();
            return chain.proceed(newRequest);
        }
    };

    public static void markPageAlive(String actName) {
        actLiveMap.put(actName, true);
    }

    public static void markPageDestroy(String actName) {
        actLiveMap.put(actName, false);
    }
}
