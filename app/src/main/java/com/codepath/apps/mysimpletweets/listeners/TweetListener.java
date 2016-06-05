package com.codepath.apps.mysimpletweets.listeners;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

public interface TweetListener {
    public void onClickProfileImage(User user);

    public void onClickReply(Tweet tweet);

    public void onClickRetweet(Tweet tweet);

    public void onClickFavorite(Tweet tweet);

    public void onClickTweetDetails(Tweet tweet);
}
