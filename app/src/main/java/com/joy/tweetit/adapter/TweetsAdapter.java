package com.joy.tweetit.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.joy.tweetit.R;
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

    public void postTweeting(Tweet tweet) {
        mList.add(0, tweet);
        notifyItemInserted(0);
    }

    public void addTweets(List<Tweet> tweets) {
        mList.addAll(tweets);
        notifyDataSetChanged();
    }

    public void clearAll() {
        mList.clear();
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
        Tweet tweet = mList.get(position);
        if (holder instanceof TweetHolder) {
            TweetHolder tweetHolder = (TweetHolder) holder;
            // Setup view content
            Glide.with(mContext)
                    .load(tweet.getProfileImageUrl())
                    .fitCenter()
//                    .placeholder(R.drawable.ic_image_placeholder)
//                    .override(mContext.getResources().getDimensionPixelSize(R.dimen.item_tweet_image_size),
//                            mContext.getResources().getDimensionPixelSize(R.dimen.item_tweet_image_size))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(tweetHolder.image);

            tweetHolder.name.setText(tweet.getName());
            tweetHolder.userName.setText(String.format("@%s", tweet.getScreenName()));
            tweetHolder.text.setText(tweet.getText());
            tweetHolder.timeStamp.setText(tweet.getCreated_at());
            // Setup click event
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    static class TweetHolder extends RecyclerView.ViewHolder {
        private ImageView image;
        private TextView name, userName, text, timeStamp;

        public TweetHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            name = (TextView) itemView.findViewById(R.id.name);
            userName = (TextView) itemView.findViewById(R.id.user_name);
            text = (TextView) itemView.findViewById(R.id.text);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
        }
    }

    public abstract static class EndlessScrollListener extends RecyclerView.OnScrollListener {
        // True if we are still waiting for the last set of data to load.
        private boolean loading = false;

        int lastVisibleItem, visibleItemCount, totalItemCount;

        private LinearLayoutManager mManager;

        public EndlessScrollListener(LinearLayoutManager manager) {
            mManager = manager;
        }

        public abstract void onLoadMore();

        public void finishLoading() {
            this.loading = false;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            //check for scroll down
            if (dy > 0) {
                visibleItemCount = mManager.getChildCount();
                totalItemCount = mManager.getItemCount();
                lastVisibleItem = mManager.findLastVisibleItemPosition();
                Log.i("onScrolled()", "visibleItemCount=" + visibleItemCount
                        + ", totalItemCount=" + totalItemCount
                        + ", lastVisibleItem=" + lastVisibleItem);

                if (!loading) {
                    if ((visibleItemCount + lastVisibleItem) >= totalItemCount) {
                        loading = true;
                        //Do pagination.. i.e. fetch new data
                        onLoadMore();
                    }
                }
            }
        }
    }
}
