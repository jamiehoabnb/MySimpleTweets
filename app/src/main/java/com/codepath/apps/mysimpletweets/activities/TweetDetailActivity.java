package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.TweetDetailFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import org.parceler.Parcels;

import butterknife.ButterKnife;

public class TweetDetailActivity extends AppCompatActivity {

    public static final String ARG_USER = "user";
    public static final String ARG_TWEET = "tweet";

    //The current tweet being viewed.
    private Tweet tweet;

    //The current user logged in.
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(ARG_TWEET));
        user = Parcels.unwrap(getIntent().getParcelableExtra(ARG_USER));

        if (savedInstanceState == null) {
            TweetDetailFragment fragment = TweetDetailFragment.newInstance(user, tweet);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flTweetDetailContainer, fragment);
            ft.commit();
        }
    }
}
