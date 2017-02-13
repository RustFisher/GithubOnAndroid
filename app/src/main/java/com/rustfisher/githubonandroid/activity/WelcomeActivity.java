package com.rustfisher.githubonandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.rustfisher.githubonandroid.GApp;
import com.rustfisher.githubonandroid.PageManager;
import com.rustfisher.githubonandroid.R;

import java.util.ArrayList;
import java.util.Locale;

public class WelcomeActivity extends Activity {

    TextView mVersionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_welcome);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLoadingConfigAsyncTask.execute("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingConfigAsyncTask.cancel(true);
    }

    AsyncTask<String, Integer, String> mLoadingConfigAsyncTask = new AsyncTask<String, Integer, String>() {
        @Override
        protected String doInBackground(String... params) {
            checkDatabase();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            startActivity(new Intent(getApplicationContext(), OwnerActivity.class));
            finish();
        }
    };

    private void checkDatabase() {
        ArrayList<String> ownerHistoryTextList = new ArrayList<>();
        // fixme: use SQLite
        ownerHistoryTextList.add("Google");
        ownerHistoryTextList.add("Facebook");
        ownerHistoryTextList.add("Microsoft");
        ownerHistoryTextList.add("Bilibili");
        ownerHistoryTextList.add("RustFisher");
        PageManager.setOwnerHistoryTextList(ownerHistoryTextList);
    }

    private void initUI() {
        mVersionTv = (TextView) findViewById(R.id.versionTv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        mVersionTv.setText(String.format(Locale.ENGLISH, "%s", GApp.getVersionName()));
    }

}
