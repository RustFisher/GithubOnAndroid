package com.rustfisher.githubonandroid.widget;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.rustfisher.githubonandroid.R;

import java.util.ArrayList;

public final class HistoryListAdapter extends BaseAdapter {
    private ArrayList<String> textList;
    private LayoutInflater mInflater;

    public HistoryListAdapter(LayoutInflater inflater) {
        super();
        textList = new ArrayList<>();
        mInflater = inflater;
    }

    public void setList(ArrayList<String> list) {
        textList = new ArrayList<>(list);
    }

    public String getTextItem(int position) {
        return textList.get(position);
    }

    public void clear() {
        textList.clear();
    }

    @Override
    public int getCount() {
        return textList.size();
    }

    @Override
    public Object getItem(int i) {
        return textList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // General ListView optimization code.
        if (view == null) {
            view = mInflater.inflate(R.layout.list_item_text_1, null);
            viewHolder = new ViewHolder();
            viewHolder.nameTv = (TextView) view.findViewById(R.id.nameTv);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.nameTv.setText(textList.get(i));
        return view;
    }

    private static class ViewHolder {
        TextView nameTv;
    }
}