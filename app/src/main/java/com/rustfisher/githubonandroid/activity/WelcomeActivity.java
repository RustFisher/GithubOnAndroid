package com.rustfisher.githubonandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.rustfisher.githubonandroid.GApp;
import com.rustfisher.githubonandroid.PageManager;
import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.db.DBManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WelcomeActivity extends Activity {
    private static final String TAG = "rustAppWelcomeAct";

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
        ArrayList<DBManager.OwnerEntity> historyList = DBManager.getManager().queryAllHistory();
        if (historyList.size() == 0) {
            DBManager.getManager().insertOneRecord("Facebook");
            DBManager.getManager().insertOneRecord("Google");
            DBManager.getManager().insertOneRecord("Microsoft");
            DBManager.getManager().insertOneRecord("Bilibili");
            DBManager.getManager().insertOneRecord("RustFisher");
        }
        PageManager.setOwnerHistoryTextList(DBManager.getManager().queryHistoryStr());
        List<DBManager.OwnerEntity> list = DBManager.getManager().queryAllHistory();
        for (DBManager.OwnerEntity entity : list) {
            Log.d(TAG, entity.toString());
        }
    }

    private void initUI() {
        mVersionTv = findViewById(R.id.versionTv);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        mVersionTv.setText(String.format(Locale.ENGLISH, "%s", GApp.getVersionName()));
    }

}
