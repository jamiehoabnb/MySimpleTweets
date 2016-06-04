package com.codepath.apps.mysimpletweets.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.util.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.util.ListProgressBar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public abstract class TweetsListFragment extends Fragment {

    List<Tweet> list;
    TweetsArrayAdapter adapter;

    @BindView(R.id.lvTweets)
    ListView lvTweets;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    protected TwitterClient twitterClient;

    private boolean cacheTweets = true;

    private boolean endlessScroll = true;

    //If we don't want the profile image to be clickable, set this to null.  Use case is profile page.
    public TweetsArrayAdapter.OnProfileImageClickListener listener;

    private class DBReadAsyncTask extends AsyncTask<String, Void, List<Tweet>> {
        protected void onPreExecute() {
            ListProgressBar.showProgressBar();
        }

        protected List<Tweet> doInBackground(String... strings) {
            return Tweet.recentItems(getTweetType());
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute(List<Tweet> tweets) {
            ListProgressBar.hideProgressBar();
            //Add old tweets cached from DB.
            adapter.addAll(tweets);

            //Get new tweets from twitter.
            populateTimeLineWithREST(false, Long.MAX_VALUE,
                    tweets.isEmpty() ? 1 : tweets.get(0).getUid());
        }
    }

    private class DBWriteAsyncTask extends AsyncTask<List<Tweet>, Void, Void> {
        protected void onPreExecute() {
            ListProgressBar.showProgressBar();
        }

        protected Void doInBackground(List<Tweet>... tweets) {
            for (List<Tweet> tweetList: tweets) {
                for (Tweet tweet: tweetList) {
                    tweet.getUser().save();
                    tweet.save();
                }
            }
            return null;
        }

        protected void onProgressUpdate() {
        }

        protected void onPostExecute() {
            ListProgressBar.hideProgressBar();
        }
    }

    protected void populateTimeLine(final boolean nextPage, final long maxId, long minId) {

        if (cacheTweets) {
            //Read from DB asynchronously.
            new DBReadAsyncTask().execute();
        } else {
            //No tweets are cached so we are querying for fresh new list of tweets from twitter.
            populateTimeLineWithREST(false, Long.MAX_VALUE, 1);
        }
    }

    public void disableCache() {
        this.cacheTweets = false;
    }

    public void disableEndlessScroll() {this.endlessScroll = false;}

    protected abstract void populateTimeLineWithREST(final boolean nextPage, final long maxId, long minId);

    protected abstract Tweet.Type getTweetType();

    protected JsonHttpResponseHandler getResponseHandler(final boolean nextPage) {
        ListProgressBar.showProgressBar();
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                ListProgressBar.hideProgressBar();
                swipeContainer.setRefreshing(false);

                if (nextPage) {
                    if (response.length() > 0) {
                        adapter.addAll(Tweet.fromJSONArray(response, getTweetType()));
                    }
                } else {
                    //Add to top of list and save to DB
                    List<Tweet> newTweets = Tweet.fromJSONArray(response, getTweetType());
                    for(int i = newTweets.size()-1; i >= 0; i--) {
                        Tweet newTweet = newTweets.get(i);

                        //Add new tweet at beginning of list.
                        adapter.add(0, newTweet);
                    }

                    if (cacheTweets && ! newTweets.isEmpty()) {
                        new DBWriteAsyncTask().execute(newTweets);
                    }

                    //Delete old tweets if table is too big.
                    if (adapter.getCount() > Tweet.TABLE_MAX_SIZE) {
                        Tweet.deleteOldTweets(getTweetType(), adapter.getItem(Tweet.TABLE_MAX_SIZE-1).getUid());
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray arrayResponse = response.getJSONArray("statuses");
                    onSuccess(statusCode, headers, arrayResponse);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
                ListProgressBar.hideProgressBar();
                Log.e("ERROR", errorResponse.toString(), throwable);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        ButterKnife.bind(this, v);
        lvTweets.setAdapter(adapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Query for new tweets because user swiped to refresh.
                long minId = list.isEmpty() ? 1 : list.get(0).getUid();
                populateTimeLineWithREST(false, Long.MAX_VALUE, minId);
            }
        });

        if (endlessScroll) {
            lvTweets.setOnScrollListener(new EndlessScrollListener() {
                @Override
                public boolean onLoadMore(int page, int totalItemsCount) {
                    //Query for older tweets because user is scrolling down.
                    long maxId = list.isEmpty() ? Long.MAX_VALUE : list.get(list.size() - 1).getUid() - 1;
                    populateTimeLineWithREST(true, maxId, 1);
                    return true;
                }

                @Override
                public int getFooterViewType() {
                    return -1;
                }
            });
        }

        return v;
    }

    public void setListener(TweetsArrayAdapter.OnProfileImageClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        adapter = new TweetsArrayAdapter(getActivity(), list, listener);
        twitterClient = TwitterApplication.getRestClient();
        populateTimeLine(false, Long.MAX_VALUE, 1);
    }

    public void add(Tweet tweet) {
        list.add(0, tweet);
        adapter.notifyDataSetChanged();

        //Save the new tweet that was just composed.
        List<Tweet> newTweets = new LinkedList<>();
        new DBWriteAsyncTask().execute(newTweets);
    }
}
