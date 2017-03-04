package com.joy.tweetit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joy.tweetit.model.Tweet;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by joy0520 on 2017/3/3.
 */

public class HomeTimelineActivity extends AppCompatActivity {
    private static final String TAG = "HomeTimelineActivity.";
    public static final boolean DEBUG = true;

    private RecyclerView mList;
    private Toolbar mToolbar;
    private ProgressBar mProgressBottom;

    private TweetsAdapter mAdapter;
    private int mCurrentPage = 0;
    TweetsAdapter.EndlessScrollListener mScrollListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_viewer);

        // Init views
        mList = (RecyclerView) findViewById(R.id.list);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mProgressBottom = (ProgressBar) findViewById(R.id.progrss_bottom);

        // Setup toolbar
        setSupportActionBar(mToolbar);

        // Adapter
        mAdapter = new TweetsAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mScrollListener = new TweetsAdapter.EndlessScrollListener(manager) {
            @Override
            public void onLoadMore() {
                Log.i("onLoadMore", "load more!");
                populateHomeTimeline(mCurrentPage + 1);
            }
        };

        mList.setAdapter(mAdapter);
        mList.setLayoutManager(manager);
        mList.addOnScrollListener(mScrollListener);

        // Load first page
        populateHomeTimeline();
        mProgressBottom.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();// TODO finish app
    }

    private void populateHomeTimeline() {
        // Reset
        mCurrentPage = 0;
        mAdapter.clearAll();
        populateHomeTimeline(0);
    }

    private void populateHomeTimeline(final int page) {
        //TODO DEBUG prevent load too much!!
        if (DEBUG && page >= 2) {
            return;
        }

        TweetItApplication.getRestClient().getHomeTimeline(page, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                mProgressBottom.setVisibility(View.VISIBLE);
                Toast.makeText(HomeTimelineActivity.this, "failed to load home timeline:\n" + responseString,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                Log.i(TAG + "onSuccess(1)", "" + responseString);

                Type listType = new TypeToken<List<Tweet>>() {
                }.getType();
                List<Tweet> tweets = gson.fromJson(responseString, listType);
                Log.i(TAG + "onSuccess(2)", "" + tweets);
                mAdapter.addTweets(tweets);

                // Hide the progress bar
                mProgressBottom.setVisibility(View.GONE);

                mCurrentPage = page;
                if (mScrollListener != null) {
                    mScrollListener.finishLoading();
                }
            }
        });
    }
}
