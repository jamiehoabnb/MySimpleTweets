package com.codepath.apps.mysimpletweets.listeners;

import com.codepath.apps.mysimpletweets.models.Tweet;

public interface ComposeTweetDialogListener {
    void onFinishComposeTweetDialog(String tweet, Tweet replyTweet);
}
