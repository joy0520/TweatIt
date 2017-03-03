package com.joy.tweetit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_viewer);
        populateHomeTimeline(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void populateHomeTimeline(int page) {
        TweetItApplication.getRestClient().getHomeTimeline(page, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Gson gson = new Gson();
                Log.i(TAG+"onSuccess(1)", ""+responseString);

                Type listType = new TypeToken<List<Tweet>>(){}.getType();
                List<Tweet> posts = (List<Tweet>) gson.fromJson(responseString, listType);
                Log.i(TAG+"onSuccess(2)", ""+posts);


//                mArticlesGson = gson.fromJson(responseString, TweetsGson.class);
//                Log.i("onArticleSearch().onSuccess()", "mArticlesGson=" + mArticlesGson);
//                mArticles.addAll(mArticlesGson.getArticles());
//                mAdapter.addArticles(mArticlesGson.getArticles());
//                mAdapter.notifyDataSetChanged();

                // Hide the progress bar
//                mProgress.setVisibility(GONE);
                // If there are more articles, load them!
//                if (mArticlesGson.getHits() / NUM_ARTICLES_PER_PAGE > mCurrentLoadingPage + 1
//                        && mCurrentLoadingPage + 1 < NUM_PAGES_LIMIT) {
//                    // But with a delay, to reduce query times
//                    mHandler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            onArticleSearch(query, page + 1);
//                        }
//                    }, DELAY_LOADING_NEXT_PAGE_MS);
//                }

            }
        });
    }
}
