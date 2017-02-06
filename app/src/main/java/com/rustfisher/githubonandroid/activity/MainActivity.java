package com.rustfisher.githubonandroid.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.NetworkCenter;
import com.rustfisher.githubonandroid.network.bean.GitHubContributor;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Subscriber;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "rustApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        NetworkCenter.getContributors("square", "retrofit", new Callback<List<GitHubContributor>>() {
            @Override
            public void onResponse(Call<List<GitHubContributor>> call, Response<List<GitHubContributor>> response) {
                Log.d(TAG, "github onResponse: " + response.raw().toString());
                String res = "Square retrofit\n";
                if (response.isSuccessful()) {
                    for (GitHubContributor gitHubContributor : response.body()) {
                        res += (gitHubContributor.getLogin() + " commits " + gitHubContributor.getContributions() + "\n");
                    }
                    Log.d(TAG, "retrofit onResponse: " + res);
                }
            }

            @Override
            public void onFailure(Call<List<GitHubContributor>> call, Throwable t) {
                Log.e(TAG, "request square onFailure");
            }
        });

        NetworkCenter.rxGetContributors("square", "retrofit")
                .subscribeOn(Schedulers.newThread())// 请求在新的线程中执行
                .observeOn(Schedulers.io())         // 请求完成后在io线程中执行
//                .doOnNext(new Action1<GitHubContributor>() {
//                    @Override
//                    public void call(GitHubContributor contributor) {
//
//                    }
//                })
//                .observeOn(Schedulers.())// 最后在主线程中执行
                .subscribe(new Subscriber<ArrayList<GitHubContributor>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "rx onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<GitHubContributor> gitHubContributors) {
                        Log.d(TAG, "onNext: " + gitHubContributors.size());
                    }

                });
    }
}
