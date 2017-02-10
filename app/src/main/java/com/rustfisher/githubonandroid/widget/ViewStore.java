package com.rustfisher.githubonandroid.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.rustfisher.githubonandroid.widget.recyclerview.DividerLine;

/**
 * Some views config
 */
public final class ViewStore {

    public static void decorateRecyclerView(Context context, RecyclerView recyclerView) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        DividerLine dividerLine = new DividerLine(DividerLine.VERTICAL);
        dividerLine.setSize(2);
        dividerLine.setColor(Color.TRANSPARENT);
        recyclerView.addItemDecoration(dividerLine);
    }

    /**
     * @param context Need Activity instance
     */
    public static ProgressDialog getProgressDialog1(Activity context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("loading");
        progressDialog.setCancelable(true);
        return progressDialog;
    }

}
