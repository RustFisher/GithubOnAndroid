package com.rustfisher.githubonandroid.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.NetworkCenter;
import com.rustfisher.githubonandroid.network.bean.GitHubContributor;
import com.rustfisher.githubonandroid.network.service.GitHubService;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "rustApp";

    @BindView(R.id.ownerEt)
    EditText mOwnerEt;
    @BindView(R.id.repoEt)
    EditText mRepoEt;
    @BindView(R.id.infoTv)
    TextView mInfoTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        initUI();
    }

    @OnClick({R.id.loadBt1})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loadBt1:
                clickLoadBtn();
                break;
            default:

                break;
        }
    }

    private void initUI() {
        ButterKnife.bind(this);

        mOwnerEt.setText("square");
        mRepoEt.setText("retrofit");
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
                    public void call(ArrayList<GitHubContributor> forks) {
                        Log.d(TAG, "forks " + forks.size());
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
