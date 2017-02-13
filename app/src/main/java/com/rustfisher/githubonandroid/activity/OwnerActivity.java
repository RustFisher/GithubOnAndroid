package com.rustfisher.githubonandroid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rustfisher.githubonandroid.PageManager;
import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.NetworkCenter;
import com.rustfisher.githubonandroid.network.bean.UserInfo;
import com.rustfisher.githubonandroid.network.bean.UserRepo;
import com.rustfisher.githubonandroid.widget.HistoryDialogFragment;
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

public class OwnerActivity extends Activity {

    private static final String TAG = "rustApp";

    @BindView(R.id.act_root)
    CoordinatorLayout mRoot;
    @BindView(R.id.infoReView)
    RecyclerView mReView;
    @BindView(R.id.inputField1)
    InputField mOwnerInputField;
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.ownerToolbar)
    Toolbar mToolbar;
    @BindView(R.id.contentReLayout)
    RelativeLayout mContentReLayout;
    @BindView(R.id.bioTv)
    TextView mBioTv;
    @BindView(R.id.avatarIv)
    ImageView mAvatarIv;
    @BindView(R.id.locationTv)
    TextView mLocationTv;
    @BindView(R.id.emailTv)
    TextView mEmailTv;
    @BindView(R.id.locationField)
    RelativeLayout mLocationField;
    @BindView(R.id.emailField)
    RelativeLayout mEmailField;

    private ProgressDialog mProgressDialog;
    private RepoListAdapter mRepoListAdapter;
    HistoryDialogFragment mHistoryDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_owner);
        initUI();
        initUtils();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Main act, repo act list size == " + PageManager.getRepoActListSize());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PageManager.finishRepoAct();
        unregisterReceiver(mBroadcastReceiver);
        mProgressDialog.dismiss();
        mProgressDialog = null;
    }

    private void initUI() {
        ButterKnife.bind(this);
        mProgressDialog = ViewStore.getLoadingProgressDialog(this);
        mHistoryDialogFragment = new HistoryDialogFragment();
        mRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
            }
        });

        mOwnerInputField.setEtText(PageManager.getLastUserName());
        mOwnerInputField.setText("Owner ", getText(R.string.owner));
        mOwnerInputField.setFarRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();
                clearEtFocus();
                loadOwnerRepos(mOwnerInputField.getEtText());
            }
        });
        mOwnerInputField.setRightOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList(HistoryDialogFragment.K_HISTORY_LIST, PageManager.getOwnerHistoryTextListCp());
                mHistoryDialogFragment.setArguments(bundle);
                mHistoryDialogFragment.show(getFragmentManager(), "a");
            }
        });
        mOwnerInputField.setFarRightDrawable(ContextCompat.getDrawable(getApplication(), R.mipmap.ic_refresh));
        mOwnerInputField.setRightIvDrawable(ContextCompat.getDrawable(getApplication(), R.mipmap.ic_history));

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
        hideOwnerField();
    }

    private void initUtils() {
        registerReceiver(mBroadcastReceiver, makeIFilter());
        loadOwnerInfoAndRepo(mOwnerInputField.getEtText());
    }

    private void clearEtFocus() {
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

    private void loadOwnerInformation(final String userName) {
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        hideOwnerField();
        NetworkCenter.getUserInformationObs(userName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "getUserInformation onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "getUserInformation onError", e);
                    }

                    @Override
                    public void onNext(UserInfo userInfo) {
                        loadUserInfoUI(userInfo);
                    }
                });

    }

    private void hideOwnerField() {
        mBioTv.setVisibility(View.GONE);
        mLocationField.setVisibility(View.GONE);
        mEmailField.setVisibility(View.GONE);
    }

    private void loadUserInfoUI(UserInfo userInfo) {
        Glide.with(OwnerActivity.this).load(userInfo.getAvatar_url()).into(mAvatarIv);
        mOwnerInputField.setEtText(userInfo.getLogin());
        if (!TextUtils.isEmpty(userInfo.getBio())) {
            mBioTv.setVisibility(View.VISIBLE);
            mBioTv.setText(userInfo.getBio());
        } else {
            mBioTv.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(userInfo.getEmail())) {
            mEmailField.setVisibility(View.VISIBLE);
            mEmailTv.setText(userInfo.getEmail());
        } else {
            mEmailField.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(userInfo.getLocation())) {
            mLocationTv.setText(userInfo.getLocation());
            mLocationField.setVisibility(View.VISIBLE);
        } else {
            mLocationField.setVisibility(View.GONE);
        }
    }

    private void loadOwnerRepos(final String owner) {
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
                        mCollapsingToolbarLayout.setTitle(owner);
                        PageManager.saveUserName(owner);
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
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Get repos onError:" + e.getMessage(), e);
                    }

                    @Override
                    public void onNext(ArrayList<UserRepo> userRepos) {
                        mRepoListAdapter.updateList(UserRepoInfo.packUserRepo(userRepos));
                        mRepoListAdapter.notifyDataSetChanged();
                    }

                });
    }

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case NetworkCenter.K_OWNER:
                    String owner = intent.getStringExtra(NetworkCenter.K_OWNER);
                    loadOwnerInfoAndRepo(owner);
                    break;
            }
        }
    };

    private void loadOwnerInfoAndRepo(String owner) {
        loadOwnerInformation(owner);
        loadOwnerRepos(owner);
    }

    private IntentFilter makeIFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NetworkCenter.K_OWNER);
        return intentFilter;
    }

    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (null != imm) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }

}
