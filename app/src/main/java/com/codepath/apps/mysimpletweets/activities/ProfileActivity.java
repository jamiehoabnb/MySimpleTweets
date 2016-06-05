package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

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
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;
import org.parceler.Parcels;

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

    private TwitterClient client = TwitterApplication.getRestClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        this.user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        boolean disableCache = getIntent().getBooleanExtra(ARG_DISABLE_CACHE, false);
        twitterClient = TwitterApplication.getRestClient();

        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(
                    user.getScreenName(), this, progressBar);
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
    public void onFinishComposeTweetDialog(String tweet) {
        client.postStatusUpdate(tweet,
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
