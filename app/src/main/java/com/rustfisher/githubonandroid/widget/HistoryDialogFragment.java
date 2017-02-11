package com.rustfisher.githubonandroid.widget;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.NetworkCenter;

import java.util.ArrayList;

public class HistoryDialogFragment extends DialogFragment {
    public static final String K_HISTORY_LIST = "k_history_list";
    private static final String TAG = "rustApp";

    private HistoryListAdapter historyListAdapter;
    private ListView listView;
    private ArrayList<String> historyTextList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View root = inflater.inflate(R.layout.dialog_history, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            historyTextList = bundle.getStringArrayList(K_HISTORY_LIST);
        }
        listView = (ListView) root.findViewById(R.id.historyLv);
        historyListAdapter = new HistoryListAdapter(inflater);
        historyListAdapter.setList(historyTextList);
        listView.setAdapter(historyListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String history = historyListAdapter.getTextItem(position);
                Intent intent = new Intent(NetworkCenter.K_OWNER);
                intent.putExtra(NetworkCenter.K_OWNER, history);
                getActivity().sendBroadcast(intent);
                dismiss();
            }
        });
        return root;
    }
}
