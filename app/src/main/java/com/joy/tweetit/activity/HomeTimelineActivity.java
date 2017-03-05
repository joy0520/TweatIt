package com.joy.tweetit.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.joy.tweetit.NetworkCheck;
import com.joy.tweetit.R;
import com.joy.tweetit.TweetItApplication;
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
    private static final int INTERVAL_CHECK_NET_MS = 30000;
    private static final int INTERVAL_AUTO_COLLAPSE_APPBAR_MS = 5000;
    private static final String SHARED_PREFS_TWEET_DRAFT_KEY = "tweet_draft";

    public static final boolean DEBUG = true;

    private SwipeRefreshLayout mSwipeRefresh;
    private RecyclerView mList;
    private AppBarLayout mAppbarLayout;
    private Toolbar mToolbar;
    private TextView mNoNetwork;
    private ProgressBar mProgressBottom;
    private FloatingActionButton mFloatingButton;

    private TweetsAdapter mAdapter;
    private LinearLayoutManager mManager;
    private int mCurrentMaxPage = 0;
    private TweetsAdapter.EndlessScrollListener mScrollListener;
    private boolean mScrolling;
    private Runnable mCollapseAppbarRunnable = new Runnable() {
        @Override
        public void run() {
            mScrolling = false;
            if (mAppbarLayout != null && !mScrolling) {
                mAppbarLayout.setExpanded(false);
            }
        }
    };

    private boolean mPreNetworkState;
    private Handler mHandler;
    private Runnable mCheckNetRunnable = new Runnable() {
        @Override
        public void run() {
            if (NetworkCheck.isNetworkAvailable(getBaseContext())) {
                if (!mPreNetworkState
                        && mManager != null && mManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    Log.d("mCheckNetRunnable", "network recovered and on top and every 30 seconds, do refresh");
                    populateHomeTimeline();
                }
                mNoNetwork.setVisibility(View.GONE);
                mPreNetworkState = true;
            } else {
                mNoNetwork.setVisibility(View.VISIBLE);
                mPreNetworkState = false;
            }
            mHandler.postDelayed(mCheckNetRunnable, INTERVAL_CHECK_NET_MS);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_viewer);

        // Init views
        mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mList = (RecyclerView) findViewById(R.id.list);
        mAppbarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mNoNetwork = (TextView) findViewById(R.id.no_network);
        mProgressBottom = (ProgressBar) findViewById(R.id.progrss_bottom);
        mFloatingButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

        // Setup SwipeRefreshLayout
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                populateHomeTimeline();
                mHandler.removeCallbacks(mCollapseAppbarRunnable);
                mHandler.postDelayed(mCollapseAppbarRunnable, INTERVAL_AUTO_COLLAPSE_APPBAR_MS);
            }
        });
        // Configure the refreshing colors
        mSwipeRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Setup toolbar
        setSupportActionBar(mToolbar);

        // Adapter
        mAdapter = new TweetsAdapter(this);
        mManager = new LinearLayoutManager(this);
        mScrollListener = new TweetsAdapter.EndlessScrollListener(mManager) {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mScrolling = true;
                mHandler.removeCallbacks(mCollapseAppbarRunnable);
                if (mManager != null && mManager.findFirstCompletelyVisibleItemPosition() == 0) {
                    // On top most of the list, hide app bar immediately.
                    mAppbarLayout.setExpanded(false);
                } else {
                    mHandler.postDelayed(mCollapseAppbarRunnable, INTERVAL_AUTO_COLLAPSE_APPBAR_MS);
                }
            }

            @Override
            public void onLoadMore() {
                Log.i("onLoadMore", "load more!");
                populateHomeTimeline(mCurrentMaxPage + 1);
            }
        };
        mList.setAdapter(mAdapter);
        mList.setLayoutManager(mManager);
        mList.addOnScrollListener(mScrollListener);

        mHandler = new Handler();
        mHandler.post(mCheckNetRunnable);

        // Floating Action Button
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showComposeDialog();
            }
        });

        // Load first page
        populateHomeTimeline();
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
//        switch (item.getItemId()) {
//            case R.id.toolbar_compose:
//                showComposeDialog();
//                return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostNewTweet(String newTweetBody) {
        if (DEBUG) Log.i("onPostNewTweet()", "newTweetBody=" + newTweetBody);
        if (newTweetBody == null || newTweetBody.isEmpty()) return;
        TweetItApplication.getRestClient().postTweet(newTweetBody,
                new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.i(TAG + "onFailure()", "" + responseString);
                        mSwipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        Log.i(TAG + "onSuccess()", "" + responseString);
                        mSwipeRefresh.setRefreshing(false);
                        Gson gson = new Gson();
                        Tweet tweet = gson.fromJson(responseString, Tweet.class);
                        mAdapter.postTweeting(tweet);
                        // Move current watching position to the top most.
                        mList.scrollToPosition(0);
                        //TODO trigger a refresh after one minute maybe.
                    }
                });
    }

    @Override
    public void onCancelNewTweet(String newTweet) {
        saveTweetDraft(newTweet);
    }

    private void showComposeDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ComposeDialog dialog = ComposeDialog.newInstance(getString(R.string.compose_dialog), getTweetDraft());
        dialog.setCallback(this);
        dialog.show(fm, "fragment_setting_dialog");
    }

    private void populateHomeTimeline() {
        // Reset
        mCurrentMaxPage = 0;
        mAdapter.clearAll();
        if (NetworkCheck.isOnlineAndAvailable(this)) {
            mProgressBottom.setVisibility(View.VISIBLE);
            populateHomeTimeline(0);
        } else {
            mNoNetwork.setVisibility(View.VISIBLE);
            mSwipeRefresh.setRefreshing(false);
            mAdapter.applyLocalTweets();
        }
    }

    private void populateHomeTimeline(final int page) {
        //TODO DEBUG prevent load too much!!
        if (DEBUG && page >= 2) {
            return;
        }
        // No network hint
        if (NetworkCheck.isOnlineAndAvailable(this)) {
            mNoNetwork.setVisibility(View.GONE);
        } else {
            mNoNetwork.setVisibility(View.VISIBLE);
            return;
        }
        // Show the progress bar
        mProgressBottom.setVisibility(View.VISIBLE);

        TweetItApplication.getRestClient().getHomeTimeline(page, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mProgressBottom.setVisibility(View.GONE);
                Toast.makeText(HomeTimelineActivity.this, "failed to load home timeline:\n" + responseString,
                        Toast.LENGTH_SHORT).show();
                mSwipeRefresh.setRefreshing(false);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                mSwipeRefresh.setRefreshing(false);
                Gson gson = new Gson();
                //Log.i(TAG + "onSuccess(1)", "" + responseString);

                Type listType = new TypeToken<List<Tweet>>() {
                }.getType();
                List<Tweet> tweets = gson.fromJson(responseString, listType);
                //Log.i(TAG + "onSuccess(2)", "" + tweets);
                mAdapter.addTweets(tweets);

                // Hide the progress bar
                mProgressBottom.setVisibility(View.GONE);

                mCurrentMaxPage = page;
                if (mScrollListener != null) {
                    mScrollListener.finishLoading();
                }
            }
        });
    }

    private void saveTweetDraft(String tweetBody) {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(SHARED_PREFS_TWEET_DRAFT_KEY, tweetBody);
        edit.apply();
    }

    private String getTweetDraft() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        return pref.getString(SHARED_PREFS_TWEET_DRAFT_KEY, "");
    }

    private void clearTweetDraft() {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString(SHARED_PREFS_TWEET_DRAFT_KEY, "");
        edit.apply();
    }
}
