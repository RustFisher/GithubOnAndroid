package com.rustfisher.githubonandroid.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rustfisher.githubonandroid.PageManager;
import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.NetworkCenter;
import com.rustfisher.githubonandroid.network.bean.Repo;
import com.rustfisher.githubonandroid.widget.ViewStore;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * show specific repo
 */
public class RepoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "rustApp";

    @BindView(R.id.repoOwnerTv)
    TextView mRepoOwnerTv;
    @BindView(R.id.repoNameTv)
    TextView mRepoNameTv;
    @BindView(R.id.forkInfoTv)
    TextView mForkInfoTv;
    @BindView(R.id.repoToolbar)
    Toolbar mToolbar;
    @BindView(R.id.errorTv)
    TextView mErrorTv;
    @BindView(R.id.repoTypeIv)
    ImageView mRepoTypeIv;
    @BindView(R.id.descriptionTv)
    TextView mDescriptionTv;
    @BindView(R.id.lastPushTv)
    TextView mLastPushTv;
    @BindView(R.id.starCountTv)
    TextView mStarCountTv;
    @BindView(R.id.watcherCountTv)
    TextView mWatcherCountTv;
    @BindView(R.id.forkCountTv)
    TextView mForkCountTv;

    private ProgressDialog mProgressDialog;
    private String mRepoName;
    private String mOwnerName;
    private Repo.ParentBean mParentBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_repo);
        checkInputIntent();
        initUI();
        downloadRepoInfo(mOwnerName, mRepoName);
        PageManager.addRepoActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PageManager.removeRepoAct(this);
    }

    @OnClick({R.id.repoOwnerTv, R.id.forkInfoTv, R.id.errorTv})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.repoOwnerTv:
                Intent mainIntent = new Intent(NetworkCenter.K_OWNER);
                mainIntent.putExtra(NetworkCenter.K_OWNER, mRepoOwnerTv.getText());
                sendBroadcast(mainIntent);
                PageManager.finishRepoAct();
                break;
            case R.id.forkInfoTv:
                Intent intent = new Intent(getApplicationContext(), RepoActivity.class);
                intent.putExtra(NetworkCenter.K_OWNER, mParentBean.getOwner().getLogin());
                intent.putExtra(NetworkCenter.K_REPO_NAME, mParentBean.getName());
                startActivity(intent);
                break;
            case R.id.errorTv:
                downloadRepoInfo(mOwnerName, mRepoName);
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

        mProgressDialog = ViewStore.getLoadingProgressDialog(this);

        mToolbar.setTitle(mOwnerName);
        mToolbar.setSubtitle(mRepoName);
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setSubtitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.mipmap.ic_arrow_left);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PageManager.finishRepoAct();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                return false;
            }
        });

    }

    private void downloadRepoInfo(String owner, String repo) {
        mErrorTv.setVisibility(View.GONE);
        mErrorTv.setText("");
        mProgressDialog.show();
        NetworkCenter.getRepoDetailObs(owner, repo)
                .subscribeOn(Schedulers.newThread())
                .cacheWithInitialCapacity(5)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Repo>() {
                    @Override
                    public void onCompleted() {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        mErrorTv.setVisibility(View.VISIBLE);
                        mErrorTv.append(e.getMessage());
                        Log.e(TAG, "RepoActivity onError", e);
                    }

                    @Override
                    public void onNext(Repo repo) {
                        if (repo.isFork()) {
                            mParentBean = repo.getParent();
                        }
                        loadRepoUI(repo);
                    }
                });
    }

    private void loadRepoUI(Repo repo) {
        mRepoNameTv.setText(repo.getName());
        mRepoOwnerTv.setText(repo.getOwner().getLogin());
        mToolbar.setTitle(repo.getOwner().getLogin());
        mToolbar.setSubtitle(repo.getName());
        mLastPushTv.setText(repo.getPushed_at());
        if (repo.isFork()) {
            mRepoTypeIv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_fork));
            mForkInfoTv.setVisibility(View.VISIBLE);
            mForkInfoTv.setText(String.format(Locale.ENGLISH, "Fork from %s", mParentBean.getFull_name()));
        } else {
            mRepoTypeIv.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.ic_source_code));
            mForkInfoTv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(repo.getDescription())) {
            mDescriptionTv.setText(repo.getDescription());
        }
        mStarCountTv.setText(String.format(Locale.ENGLISH, "%d", repo.getStargazers_count()));
        mWatcherCountTv.setText(String.format(Locale.ENGLISH, "%d", repo.getWatchers_count()));
        mForkCountTv.setText(String.format(Locale.ENGLISH, "%d", repo.getForks_count()));

    }

}
