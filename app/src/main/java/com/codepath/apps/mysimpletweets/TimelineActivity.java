package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.fragments.MentionsTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.util.SmartFragmentStatePagerAdapter;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class TimelineActivity extends AppCompatActivity implements
        ComposeTweetFragment.ComposeTweetDialogListener,
        TweetsArrayAdapter.OnProfileImageClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    PagerSlidingTabStrip tabs;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    TwitterClient client;

    User user;

    TweetsPagerAdapter adapter;

    private Context context = this;

    @Override
    public void onClickProfileImage(User profileUser) {
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra(ProfileActivity.ARG_USER, Parcels.wrap(profileUser));
        //We don't cache tweet's for profile page of other users.
        intent.putExtra(ProfileActivity.ARG_DISABLE_CACHE, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        adapter = new TweetsPagerAdapter(getSupportFragmentManager(), this);
        viewPager.setAdapter(adapter);
        tabs.setViewPager(viewPager);

        client = TwitterApplication.getRestClient();
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                user = User.fromJSON(response);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_timeline, menu);
        return true;
    }

    public void onProfileView(MenuItem mi) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.ARG_USER, Parcels.wrap(user));
        startActivity(intent);
    }

    public void onComposeView(MenuItem mi) {
        android.app.FragmentManager fm = getFragmentManager();
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ComposeTweetFragment.ARG_USER, Parcels.wrap(user));
        fragment.setArguments(args);
        fragment.show(fm, "fragment_compose_tweet");
    }

    @Override
    public void onFinishDialog(String tweet) {
        client.postStatusUpdate(tweet, new JsonHttpResponseHandler() {
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

    class TweetsPagerAdapter extends SmartFragmentStatePagerAdapter {

        private String[] tabTitles = new String[] {getString(R.string.home), getString(R.string.mentions)};

        private TweetsArrayAdapter.OnProfileImageClickListener listener;

        public TweetsPagerAdapter(FragmentManager fm, TweetsArrayAdapter.OnProfileImageClickListener listener) {
            super(fm);
            this.listener = listener;
        }

        @Override
        public Fragment getItem(int position) {
            return position == 0 ?
                    HomeTimelineFragment.newInstance(listener) : MentionsTimelineFragment.newInstance(listener);
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
}
