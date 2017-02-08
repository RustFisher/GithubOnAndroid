package com.rustfisher.githubonandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.NetworkCenter;
import com.rustfisher.githubonandroid.network.bean.GitHubContributor;
import com.rustfisher.githubonandroid.network.bean.Repo;
import com.rustfisher.githubonandroid.network.bean.UserRepo;
import com.rustfisher.githubonandroid.widget.UserRepoInfo;
import com.rustfisher.githubonandroid.widget.ViewStore;
import com.rustfisher.githubonandroid.widget.recyclerview.RepoListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "rustApp";

    @BindView(R.id.ownerEt)
    EditText mOwnerEt;
    @BindView(R.id.repoEt)
    EditText mRepoEt;
    @BindView(R.id.infoTv)
    TextView mInfoTv;
    @BindView(R.id.infoReView)
    RecyclerView mReView;

    private RepoListAdapter mRepoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        initUI();
        initUtils();
    }

    @OnClick({R.id.loadBt1, R.id.loadRepoBtn})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadBt1:
                clickLoadBtn();
                break;
            case R.id.loadRepoBtn:
                clickLoadRepoBtn();
                break;
            default:

                break;
        }
    }

    private void initUI() {
        ButterKnife.bind(this);
        mOwnerEt.setText("RustFisher");
        mRepoEt.setText("GithubOnAndroid");

        mRepoListAdapter = new RepoListAdapter();
        ViewStore.decorateRecyclerView(getApplicationContext(), mReView);
        mRepoListAdapter.setOnItemClickListener(new RepoListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                UserRepo userRepo = mRepoListAdapter.getRepoItem(position).getUserRepo();
                Intent intent = new Intent(getApplicationContext(), RepoActivity.class);
                intent.putExtra(NetworkCenter.K_OWNER, userRepo.getOwner().getLogin());
                intent.putExtra(NetworkCenter.K_REPO_NAME, userRepo.getName());
                intent.putExtra(NetworkCenter.K_REPO_FULL_NAME, userRepo.getFull_name());
                intent.putExtra(NetworkCenter.K_REPO_IS_FORK_FROM, userRepo.isFork());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mReView.setAdapter(mRepoListAdapter);
    }

    private void initUtils() {

    }

    private void clickLoadRepoBtn() {
        String owner = mOwnerEt.getText().toString().trim();
        if (TextUtils.isEmpty(owner)) {
            Toast.makeText(getApplicationContext(), "Info error! Please check owner name", Toast.LENGTH_SHORT).show();
            return;
        }
        NetworkCenter.getUserRepoObs(owner)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<UserRepo>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Get repo onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Get repos onError:" + e.getMessage(), e);
                    }

                    @Override
                    public void onNext(ArrayList<UserRepo> userRepos) {
                        mRepoListAdapter.updateList(UserRepoInfo.packUserRepo(userRepos));
                        mRepoListAdapter.notifyDataSetChanged();
                    }

                });
    }

    private void clickLoadBtn() {
        String owner = mOwnerEt.getText().toString().trim();
        String repo = mRepoEt.getText().toString().trim();
        if (TextUtils.isEmpty(owner) || TextUtils.isEmpty(repo)) {
            Toast.makeText(getApplicationContext(), "Info error! Please check owner and repo name", Toast.LENGTH_SHORT).show();
            return;
        }

        Observable observable1 = NetworkCenter.getContributorsObs(owner, repo);
        observable1.subscribeOn(Schedulers.newThread())
                .doOnNext(new Action1<ArrayList<GitHubContributor>>() {
                    @Override
                    public void call(ArrayList<GitHubContributor> mans) {
                        Log.d(TAG, "mans " + mans.size());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ArrayList<GitHubContributor>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "rx onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ArrayList<GitHubContributor> gitHubContributors) {
                        Log.d(TAG, "gitHubContributors.size: " + gitHubContributors.size());
                        StringBuilder sb = new StringBuilder();
                        for (GitHubContributor man : gitHubContributors) {
                            sb.append(man.getLogin()).append("   commit:")
                                    .append(man.getContributions())
                                    .append("\n");
                        }
                        mInfoTv.setText(sb.toString());
                    }

                });

    }
}
