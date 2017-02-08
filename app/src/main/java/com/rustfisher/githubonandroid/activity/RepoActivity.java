package com.rustfisher.githubonandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.NetworkCenter;
import com.rustfisher.githubonandroid.network.bean.Repo;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RepoActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "rustApp";

    @BindView(R.id.repoOwnerTv)
    TextView mRepoOwnerTv;
    @BindView(R.id.repoNameTv)
    TextView mRepoNameTv;
    @BindView(R.id.forkInfoTv)
    TextView mForkInfoTv;

    private String mRepoName;
    private String mOwnerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_repo);
        checkInputIntent();
        initUI();
        downloadRepoInfo();
    }

    @OnClick({R.id.repoOwnerTv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.repoOwnerTv:
                finish();
                break;
        }
    }

    private void checkInputIntent() {
        Intent inputIntent = getIntent();
        if (null == inputIntent) {
            finish();
            return;
        }
        mRepoName = inputIntent.getStringExtra(NetworkCenter.K_REPO_NAME);
        mOwnerName = inputIntent.getStringExtra(NetworkCenter.K_OWNER);
    }

    private void initUI() {
        ButterKnife.bind(this);
    }

    private void downloadRepoInfo() {
        NetworkCenter.getRepoDetailObs(mOwnerName, mRepoName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Repo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "RepoActivity onError", e);
                    }

                    @Override
                    public void onNext(Repo repo) {
                        loadRepoUI(repo);
                    }
                });
    }

    private void loadRepoUI(Repo repo) {
        mRepoNameTv.setText(repo.getName());
        mRepoOwnerTv.setText(repo.getOwner().getLogin());

        if (repo.isFork()) {
            mForkInfoTv.setVisibility(View.VISIBLE);
            mForkInfoTv.setText(String.format(Locale.ENGLISH, "Fork from %s", repo.getParent().getFull_name()));
        } else {
            mForkInfoTv.setVisibility(View.INVISIBLE);
        }
    }

}
