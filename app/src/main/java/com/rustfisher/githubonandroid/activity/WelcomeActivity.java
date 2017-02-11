package com.rustfisher.githubonandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.rustfisher.githubonandroid.PageManager;
import com.rustfisher.githubonandroid.R;

import java.util.ArrayList;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_welcome);
        initUI();
    }

    @Override
    protected void onStart() {
        super.onStart();

        new Thread() {
            @Override
            public void run() {
                super.run();
                checkDatabase();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        }.start();
    }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
