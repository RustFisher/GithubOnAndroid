package com.rustfisher.githubonandroid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.rustfisher.githubonandroid.PageManager;
import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.NetworkCenter;
import com.rustfisher.githubonandroid.network.bean.UserRepo;
import com.rustfisher.githubonandroid.widget.InputField;
import com.rustfisher.githubonandroid.widget.UserRepoInfo;
import com.rustfisher.githubonandroid.widget.ViewStore;
import com.rustfisher.githubonandroid.widget.recyclerview.RepoListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private static final String TAG = "rustApp";

    @BindView(R.id.act_main)
    CoordinatorLayout mRoot;
    @BindView(R.id.infoReView)
    RecyclerView mReView;
    @BindView(R.id.inputField1)
    InputField mOwnerInputField;
    @BindView(R.id.inputField2)
    InputField mRepoInputField;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.mainToolbar)
    Toolbar mToolbar;

    private ProgressDialog mProgressDialog;
    private RepoListAdapter mRepoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        initUI();
        initUtils();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PageManager.finishRepoAct();
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    private void initUI() {
        ButterKnife.bind(this);
        mProgressDialog = ViewStore.getProgressDialog1(this);
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });

        mOwnerInputField.setEtText("RustFisher");
        mOwnerInputField.setText("Owner:", getText(R.string.owner), getText(R.string.load_repos));
        mOwnerInputField.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                clearEtFocus();
                loadOwnerRepos(mOwnerInputField.getEtText());
            }
        });

        mRepoInputField.setEtText("GithubOnAndroid");
        mRepoInputField.setText("Repo:", getText(R.string.repo), getText(R.string.load_repo_info));
        mRepoInputField.setRightBtnOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                clearEtFocus();
                goToRepoPage(mOwnerInputField.getEtText().trim(), mRepoInputField.getEtText().trim());
            }
        });

        mRepoListAdapter = new RepoListAdapter();
        ViewStore.decorateRecyclerView(getApplicationContext(), mReView);
        mRepoListAdapter.setOnItemClickListener(new RepoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                UserRepo userRepo = mRepoListAdapter.getRepoItem(position).getUserRepo();
                goToRepoPage(userRepo.getOwner().getLogin(), userRepo.getName());
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mReView.setAdapter(mRepoListAdapter);

        mCollapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        mCollapsingToolbarLayout.setTitle(mOwnerInputField.getEtText());
        mToolbar.setNavigationIcon(R.mipmap.ic_close);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initUtils() {
        loadOwnerRepos(mOwnerInputField.getEtText());
    }

    private void clearEtFocus() {
        mRepoInputField.clearEtFocus();
        mOwnerInputField.clearEtFocus();
    }

    private void goToRepoPage(String owner, String repo) {
        if (TextUtils.isEmpty(owner) || TextUtils.isEmpty(repo)) {
            Toast.makeText(getApplicationContext(), "Please check input information!", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getApplicationContext(), RepoActivity.class);
        intent.putExtra(NetworkCenter.K_OWNER, owner);
        intent.putExtra(NetworkCenter.K_REPO_NAME, repo);
        startActivity(intent);
    }

    private void loadOwnerRepos(String owner) {
        if (TextUtils.isEmpty(owner)) {
            Toast.makeText(getApplicationContext(), "Info error! Please check owner name", Toast.LENGTH_SHORT).show();
            return;
        }
        mProgressDialog.show();
        NetworkCenter.getUserRepoObs(owner)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Action1() {
                    @Override
                    public void call(Object o) {
                        Log.d(TAG, "on next " + mOwnerInputField.getEtText());
                        mCollapsingToolbarLayout.setTitle(mOwnerInputField.getEtText());
                    }
                })
                .subscribe(new Subscriber<ArrayList<UserRepo>>() {
                    @Override
                    public void onCompleted() {
                        mProgressDialog.dismiss();
                        Log.d(TAG, "Get repo onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        mProgressDialog.dismiss();
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Get repos onError:" + e.getMessage(), e);
                    }

                    @Override
                    public void onNext(ArrayList<UserRepo> userRepos) {
                        mRepoListAdapter.updateList(UserRepoInfo.packUserRepo(userRepos));
                        mRepoListAdapter.notifyDataSetChanged();
                    }

                });
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}
