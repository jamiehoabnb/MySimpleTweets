package com.codepath.apps.mysimpletweets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.activities.TweetDetailActivity;
import com.codepath.apps.mysimpletweets.listeners.ComposeTweetDialogListener;
import com.codepath.apps.mysimpletweets.listeners.TweetListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.storage.DBWriteAsyncTask;
import com.codepath.apps.mysimpletweets.util.InternetCheckUtil;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Any fragment that displays a tweet should extend this fragment.  All action handlers for opertions
 * on a tweet are implemented here.
 */
public abstract class BaseTweetFragment
        extends Fragment
        implements TweetListener, ComposeTweetDialogListener {

    protected TwitterClient twitterClient = TwitterApplication.getRestClient();

    protected SmoothProgressBar progressBar;

    //The current logged in user.
    protected User user;

    public abstract void onFinishComposeTweetDialogSuccess(Tweet newTweet);

    @Override
    public void onFinishComposeTweetDialog(final String tweet, final Tweet replyTweet) {
        progressBar.setVisibility(View.VISIBLE);
        final Activity activity = getActivity();
        twitterClient.postStatusUpdate(tweet,
                replyTweet == null ? null : replyTweet.getUid(),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        progressBar.setVisibility(View.INVISIBLE);

                        Tweet newTweet = Tweet.fromJSON(response, Tweet.Type.HOME);
                        onFinishComposeTweetDialogSuccess(newTweet);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.e("ERROR", errorResponse.toString(), throwable);

                        int errorMsgId = InternetCheckUtil.isOnline() ?
                                R.string.twitter_api_error : R.string.internet_connection_error;

                        Snackbar.make(activity.getWindow().getDecorView(), errorMsgId, Snackbar.LENGTH_LONG)
                                .setAction(activity.getString(R.string.retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onFinishComposeTweetDialog(tweet, replyTweet);
                                    }
                                })
                                .show();
                    }
                });
    }

    @Override
    public void onClickProfileImage(User profileUser) {
        Intent intent = new Intent(getContext(), ProfileActivity.class);
        intent.putExtra(ProfileActivity.ARG_USER, Parcels.wrap(profileUser));
        intent.putExtra(ProfileActivity.ARG_LOGGED_IN_USER, Parcels.wrap(user));
        //We don't cache tweet's for profile page of other users.
        intent.putExtra(ProfileActivity.ARG_DISABLE_CACHE, true);
        getContext().startActivity(intent);
    }

    @Override
    public void onClickReply(Tweet tweet) {
        android.app.FragmentManager fm = getActivity().getFragmentManager();
        ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(this, user,
                tweet);
        fragment.show(fm, "fragment_compose_tweet_reply");
    }

    @Override
    public void onClickRetweet(final Tweet tweet) {
        progressBar.setVisibility(View.VISIBLE);
        final Activity activity = getActivity();
        twitterClient.postStatusRetweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(activity.getWindow().getDecorView(), errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(activity.getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickRetweet(tweet);
                            }
                        })
                        .show();
            }
        });
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
    public void onClickFavorite(final Tweet tweet) {
        progressBar.setVisibility(View.VISIBLE);
        final Activity activity = getActivity();
        twitterClient.postFavoritesCreate(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(activity.getWindow().getDecorView(), errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(activity.getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickFavorite(tweet);
                            }
                        })
                        .show();
            }
        });
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
    public void onClickTweetDetails(Tweet tweet, List<Pair<View, String>> pairs) {
        Intent intent = new Intent(getActivity(), TweetDetailActivity.class);
        intent.putExtra(TweetDetailActivity.ARG_TWEET, Parcels.wrap(tweet));
        intent.putExtra(TweetDetailActivity.ARG_USER, Parcels.wrap(user));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(getActivity(), (Pair<View, String>[]) pairs.toArray(new Pair[]{}));

        startActivity(intent, options.toBundle());
    }

    public void setProgressBar(SmoothProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void setUser(User user) { this.user = user;}
}
