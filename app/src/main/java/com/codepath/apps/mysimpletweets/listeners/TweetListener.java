package com.codepath.apps.mysimpletweets.listeners;

import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import java.util.List;

public interface TweetListener {
    public void onClickProfileImage(User user);

    public void onClickReply(Tweet tweet);

    public void onClickRetweet(Tweet tweet);

    public void onClickFavorite(Tweet tweet);

    public void onClickTweetDetails(Tweet tweet, List<Pair<View, String>> pairs);
}
