package com.rustfisher.githubonandroid.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rustfisher.githubonandroid.R;
import com.rustfisher.githubonandroid.network.bean.UserRepo;
import com.rustfisher.githubonandroid.widget.UserRepoInfo;


import java.util.ArrayList;
import java.util.Locale;

public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.ViewHolder> {

    private ArrayList<UserRepoInfo> mDataList;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;// Set this listener to item view

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mOnItemClickListener = l;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rootLayout;
        TextView nameTv;
        TextView descriptionTv;
        TextView repoItemForkFromTv;
        TextView languageTv;
        TextView repoItemUpdatedTv;
        TextView repoItemStarTv;

        ViewHolder(View view) {
            super(view);
        }
    }

    public RepoListAdapter() {
        this(new ArrayList<UserRepoInfo>());
    }

    public RepoListAdapter(ArrayList<UserRepoInfo> entities) {
        if (null != entities) {
            mDataList = entities;
        } else {
            mDataList = new ArrayList<>();
        }
    }

    public void updateList(ArrayList<UserRepoInfo> userRepos) {
        this.mDataList = userRepos;
    }

    @Override
    public RepoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_repo, parent, false);
        ViewHolder vh = new ViewHolder(v);
        vh.rootLayout = (RelativeLayout) v.findViewById(R.id.repoItemRoot);
        vh.nameTv = (TextView) v.findViewById(R.id.repoItemNameTv);
        vh.descriptionTv = (TextView) v.findViewById(R.id.repoItemDescriptionTv);
        vh.repoItemForkFromTv = (TextView) v.findViewById(R.id.repoItemForkFromTv);
        vh.languageTv = (TextView) v.findViewById(R.id.repoItemLanguageTv);
        vh.repoItemUpdatedTv = (TextView) v.findViewById(R.id.repoItemUpdatedTv);
        vh.repoItemStarTv = (TextView) v.findViewById(R.id.repoItemStarTv);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        UserRepo userRepo = mDataList.get(position).getUserRepo();
        holder.nameTv.setText(userRepo.getName());
        holder.descriptionTv.setText(userRepo.getDescription());

        holder.repoItemForkFromTv.setVisibility(userRepo.isFork() ? View.VISIBLE : View.GONE);
        if (userRepo.isFork()) {
            holder.repoItemForkFromTv.setText("Fork from other");
        }

        holder.languageTv.setText(userRepo.getLanguage());
        holder.repoItemUpdatedTv.setText(String.format(Locale.ENGLISH, "Updated: %s", userRepo.getUpdated_at()));

        int starCount = userRepo.getStargazers_count();
        if (starCount > 0) {
            holder.repoItemStarTv.setText(String.format(Locale.ENGLISH, "Stars %d", starCount));
        } else {
            holder.repoItemStarTv.setText("");
        }

        if (mOnItemClickListener != null) {
            holder.rootLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            holder.rootLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onItemLongClick(v, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public UserRepoInfo getRepoItem(int position) {
        return mDataList.get(position);
    }

}
