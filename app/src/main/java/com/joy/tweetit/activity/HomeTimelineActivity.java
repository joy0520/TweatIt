package com.joy.tweetit.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joy.tweetit.R;
import com.joy.tweetit.TweetItApplication;
import com.joy.tweetit.TwitterClient;
import com.joy.tweetit.adapter.TweetsAdapter;
import com.joy.tweetit.dialog.ComposeDialog;
import com.joy.tweetit.model.Tweet;
import com.loopj.android.http.TextHttpResponseHandler;

import java.lang.reflect.Type;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by joy0520 on 2017/3/3.
 */

public class HomeTimelineActivity extends AppCompatActivity implements ComposeDialog.Callback {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.toolbar_compose:
                showComposeDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostNewTweet(String newTweetBody) {
        Log.i("onPostNewTweet()", "newTweetBody=" + newTweetBody);
        TweetItApplication.getRestClient().postTweet(newTweetBody,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i(TAG + "onFailure()", "" + responseString);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.i(TAG + "onSuccess()", "" + responseString);
                        Gson gson = new Gson();
                        Tweet tweet = gson.fromJson(responseString, Tweet.class);
                        mAdapter.postTweeting(tweet);
                        // Move current watching position to the top most.
                        mList.scrollToPosition(0);
                        //TODO trigger a refresh after one minute maybe.
                    }
                });
    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialog dialog = ComposeDialog.newInstance(getString(R.string.compose_dialog));
        dialog.setCallback(this);
        dialog.show(fm, "fragment_setting_dialog");
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
                //Log.i(TAG + "onSuccess(1)", "" + responseString);

                Type listType = new TypeToken<List<Tweet>>() {
                }.getType();
                List<Tweet> tweets = gson.fromJson(responseString, listType);
                //Log.i(TAG + "onSuccess(2)", "" + tweets);
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
