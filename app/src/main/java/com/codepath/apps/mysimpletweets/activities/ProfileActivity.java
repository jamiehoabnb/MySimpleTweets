package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.fragments.HomeTimelineFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.ProfileHeaderFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.storage.DBWriteAsyncTask;
import com.codepath.apps.mysimpletweets.util.InternetCheckUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ProfileActivity extends AppCompatActivity implements
        TweetsArrayAdapter.TweetListener, ComposeTweetFragment.ComposeTweetDialogListener {

    @BindView(R.id.progressBar)
    SmoothProgressBar progressBar;

    User user;

    public static final String ARG_USER = "user";
    public static final String ARG_DISABLE_CACHE = "disable_cache";

    private TwitterClient twitterClient;

    private Tweet replyTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        this.user = Parcels.unwrap(getIntent().getParcelableExtra(ARG_USER));
        boolean disableCache = getIntent().getBooleanExtra(ARG_DISABLE_CACHE, false);
        twitterClient = TwitterApplication.getRestClient();

        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(
                    user.getScreenName(), this, progressBar, user);
            ProfileHeaderFragment fragmentProfileHeader = ProfileHeaderFragment.newInstance(progressBar);

            if (disableCache) {
                fragmentUserTimeline.disableCache();
            }

            Bundle args = new Bundle();
            args.putParcelable(ProfileHeaderFragment.ARG_USER, Parcels.wrap(user));
            fragmentProfileHeader.setArguments(args);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flHeaderContainer, fragmentProfileHeader);
            ft.replace(R.id.flUserTimeLineContainer, fragmentUserTimeline);
            ft.commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public void onClickProfileImage(User user) {
        //No Op since we don't want to jump from one profile to another.
    }

    @Override
    public void onClickReply(Tweet tweet) {
        android.app.FragmentManager fm = getFragmentManager();
        ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(this, user,
                tweet.getUser().getScreenName(), tweet.getUser().getName());
        this.replyTweet = tweet;
        fragment.show(fm, "fragment_compose_tweet_reply");
    }

    @Override
    public void onClickRetweet(final Tweet tweet) {
        progressBar.setVisibility(View.VISIBLE);
        final ProfileActivity activity = this;
        twitterClient.postStatusRetweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);

                if (! tweet.isRetweeted()) {
                    tweet.setRetweetCount(tweet.getRetweetCount() + 1);
                    tweet.setRetweeted(true);
                } else {
                    tweet.setRetweetCount(tweet.getRetweetCount() - 1);
                    tweet.setRetweeted(false);
                }
                List<Tweet> tweets = new LinkedList<Tweet>();
                tweets.add(tweet);
                DBWriteAsyncTask.newInstance(progressBar).execute(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(progressBar, errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(activity.getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                activity.onClickRetweet(tweet);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onClickTweetDetails(Tweet tweet) {
        Intent intent = new Intent(this, TweetDetailActivity.class);
        intent.putExtra(TweetDetailActivity.ARG_TWEET, Parcels.wrap(tweet));
        intent.putExtra(TweetDetailActivity.ARG_USER, Parcels.wrap(user));
        startActivity(intent);
    }

    @Override
    public void onClickFavorite(final Tweet tweet) {
        progressBar.setVisibility(View.VISIBLE);
        final ProfileActivity activity = this;
        twitterClient.postFavoritesCreate(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);

                if (! tweet.isFavorited()) {
                    tweet.setFavoriteCount(tweet.getFavoriteCount() + 1);
                    tweet.setFavorited(true);
                } else {
                    tweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
                    tweet.setFavorited(false);
                }
                List<Tweet> tweets = new LinkedList<Tweet>();
                tweets.add(tweet);
                DBWriteAsyncTask.newInstance(progressBar).execute(tweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(progressBar, errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(activity.getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                activity.onClickFavorite(tweet);
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onFinishComposeTweetDialog(String tweet) {
        twitterClient.postStatusUpdate(tweet,
                this.replyTweet == null ? null : this.replyTweet.getUid(),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        Tweet newTweet = Tweet.fromJSON(response, Tweet.Type.HOME);
                        UserTimelineFragment fragment = (UserTimelineFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.flUserTimeLineContainer);
                        fragment.add(newTweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.e("ERROR", errorResponse.toString(), throwable);
                    }
                });
    }
}
