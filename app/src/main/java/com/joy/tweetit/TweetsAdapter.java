package com.joy.tweetit;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joy.tweetit.model.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joy0520 on 2017/3/3.
 */

public class TweetsAdapter extends RecyclerView.Adapter {
    private List<Tweet> mList;
    private Context mContext;

    public TweetsAdapter(Context context) {
        super();
        mContext = context;
        mList = new ArrayList<>();
    }

    public void addTweets(List<Tweet> tweets) {
        mList.addAll(tweets);
        notifyDataSetChanged();
    }

    public void addAllTweets(List<Tweet> tweets) {
        mList.clear();
        mList.addAll(tweets);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_tweet, parent, false);
        return new TweetHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TweetHolder) {
            TweetHolder tweetHolder = (TweetHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class TweetHolder extends RecyclerView.ViewHolder {
        public TweetHolder(View itemView) {
            super(itemView);
        }
    }
}
