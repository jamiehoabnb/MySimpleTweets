package com.codepath.apps.mysimpletweets.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.TweetDetailActivity;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.adapters.UsersArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.util.EndlessScrollListener;
import com.codepath.apps.mysimpletweets.util.InternetCheckUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


/**
 * Display a list of associated users for the given user.  Extend BaseTweetFragment to inherit
 * onClickProfileImage code.
 */
public abstract class BaseUserListFragment extends BaseTweetFragment {

    List<User> list;
    UsersArrayAdapter adapter;

    @BindView(R.id.progressBar)
    SmoothProgressBar progressBar;

    @BindView(R.id.lvUsers)
    ListView lvUsers;

    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    //The screen name for the user whose friends list is being viewed.
    public static final String PARAM_SCREEN_NAME = "screenName";

    protected Long cursor;

    public abstract int getLayoutId();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list = new ArrayList<>();
        adapter = new UsersArrayAdapter(getActivity(), list, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(getLayoutId(), parent, false);
        ButterKnife.bind(this, v);

        setProgressBar(progressBar);

        lvUsers.setAdapter(adapter);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //Swipe means refresh the whole list.
                cursor = null;
                populateUserList();
            }
        });

        lvUsers.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                populateUserList();
                return true;
            }

            @Override
            public int getFooterViewType() {
                return -1;
            }
        });
        populateUserList();

        return v;
    }

    protected abstract void populateUserList();

    @Override
    public void onFinishComposeTweetDialogSuccess(Tweet newTweet) {
        //No op.
    }

    public JsonHttpResponseHandler getResponseHandler() {
        return new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.INVISIBLE);
                swipeContainer.setRefreshing(false);

                if (cursor == null) {
                    adapter.clear();
                }
                adapter.addAll(User.fromJSONArray(response.optJSONArray("users")));
                cursor = response.optLong("next_cursor");

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressBar.setVisibility(View.INVISIBLE);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(lvUsers, errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(getContext().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                populateUserList();
                            }
                        })
                        .show();
            }
        };
    }
}