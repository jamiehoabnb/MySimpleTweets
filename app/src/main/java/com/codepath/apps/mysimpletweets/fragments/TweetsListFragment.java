package com.codepath.apps.mysimpletweets.fragments;

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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
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

    protected abstract void populateTimeLine(final boolean nextPage, final long maxId);

    protected JsonHttpResponseHandler getResponseHandler(final boolean nextPage) {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                swipeContainer.setRefreshing(false);

                if (! nextPage) {
                    adapter.clear();
                }
                adapter.addAll(Tweet.fromJSONArray(response));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                swipeContainer.setRefreshing(false);
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
                populateTimeLine(false, Long.MAX_VALUE);
            }
        });

        lvTweets.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                long sinceId = list.isEmpty() ? Long.MAX_VALUE : list.get(list.size()-1).getUid()-1;
                populateTimeLine(true, sinceId);
                return true;
            }

            @Override
            public int getFooterViewType() {
                return -1;
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        adapter = new TweetsArrayAdapter(getActivity(), list);
        twitterClient = TwitterApplication.getRestClient();
        populateTimeLine(false, Long.MAX_VALUE);
    }
}
