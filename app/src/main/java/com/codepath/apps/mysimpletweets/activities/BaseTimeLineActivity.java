package com.codepath.apps.mysimpletweets.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.listeners.ComposeTweetDialogListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.util.InternetCheckUtil;
import com.codepath.apps.mysimpletweets.util.SmartFragmentStatePagerAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public abstract class BaseTimeLineActivity
        extends AppCompatActivity
        implements ComposeTweetDialogListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.progressBar)
    SmoothProgressBar progressBar;

    TwitterClient client;

    User user;

    SearchView searchView;

    TweetsPagerAdapter adapter;

    private Context context = this;

    public abstract int getLayoutId();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        adapter = new TweetsPagerAdapter(getSupportFragmentManager(), getOnFragmentCreateListener(), getTabTitles());

        client = TwitterApplication.getRestClient();

        if (InternetCheckUtil.isOnline()) {
            progressBar.setVisibility(View.VISIBLE);
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    progressBar.setVisibility(View.INVISIBLE);
                    user = User.fromJSON(response);
                    viewPager.setAdapter(adapter);
                    tabs.setViewPager(viewPager);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.e("ERROR", errorResponse == null ? "" : errorResponse.toString(), throwable);
                }
            });
        } else {
            //Don't bother querying for logged in user since we are offline.  Just launch with cached
            //data.
            viewPager.setAdapter(adapter);
            tabs.setViewPager(viewPager);
        }
    }

    public void onMessageView(MenuItem mi) {
        if (user == null) {
            //Logged in user will be null if there is no internet connection.
            Snackbar.make(getWindow().getDecorView(), R.string.internet_connection_error, Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        Intent intent = new Intent(this, MessageListActivity.class);
        intent.putExtra(MessageListActivity.ARG_USER, Parcels.wrap(user));
        startActivity(intent);
    }

    public void onProfileView(MenuItem mi) {
        if (user == null) {
            //Logged in user will be null if there is no internet connection.
            Snackbar.make(getWindow().getDecorView(), R.string.internet_connection_error, Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.ARG_USER, Parcels.wrap(user));
        intent.putExtra(ProfileActivity.ARG_LOGGED_IN_USER, Parcels.wrap(user));
        startActivity(intent);
    }

    public void onComposeView(MenuItem mi) {
        if (user == null) {
            //Logged in user will be null if there is no internet connection.
            Snackbar.make(getWindow().getDecorView(), R.string.internet_connection_error, Snackbar.LENGTH_LONG)
                    .show();
            return;
        }

        android.app.FragmentManager fm = getFragmentManager();
        ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(this, user, null);
        fragment.show(fm, "fragment_compose_tweet");
    }

    @Override
    public void onFinishComposeTweetDialog(String tweet, Tweet replyTweet) {
        client.postStatusUpdate(tweet,
                replyTweet == null ? null : replyTweet.getUid(),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Tweet newTweet = Tweet.fromJSON(response, Tweet.Type.HOME);
                        HomeTimelineFragment fragment = (HomeTimelineFragment) adapter.getRegisteredFragment(0);
                        fragment.add(newTweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.e("ERROR", errorResponse.toString(), throwable);
                    }
                });
    }

    public abstract String[] getTabTitles();

    public abstract OnFragmentCreateListener getOnFragmentCreateListener();

    protected interface OnFragmentCreateListener {
        public Fragment getItem(int position);
    }

    class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

        private String[] tabTitles;

        private OnFragmentCreateListener fragmentCreateListener;

        public TweetsPagerAdapter(FragmentManager fm,
                                  OnFragmentCreateListener fragmentCreateListener,
                                  String[] tabTitles) {
            super(fm);
            this.fragmentCreateListener = fragmentCreateListener;
            this.tabTitles = tabTitles;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentCreateListener.getItem(position);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

    //Source: http://stackoverflow.com/questions/27156680/change-textcolor-in-searchview-using-android-toolbar
    protected void changeSearchViewTextColor(View view) {
        if (view != null) {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(Color.WHITE);
                return;
            } else if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    changeSearchViewTextColor(viewGroup.getChildAt(i));
                }
            }
        }
    }
}
