package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;

import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class UserTimelineFragment extends BaseTweetsListFragment {

    //The screen name for the user whose time line being viewed.  NOT always the same as logged in user.
    public static final String PARAM_SCREEN_NAME = "screenName";

    public static UserTimelineFragment newInstance(String screenName,
                                                   SmoothProgressBar progressBar,
                                                   User loggedInUser) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_SCREEN_NAME, screenName);
        fragment.setArguments(args);
        fragment.setUser(loggedInUser);
        fragment.setProgressBar(progressBar);
        return fragment;
    }

    @Override
    protected void populateTimeLineWithREST(final boolean nextPage, long maxId, long minId) {
        String screenName = getArguments().getString(PARAM_SCREEN_NAME);
        twitterClient.getUserTimeline(screenName, getResponseHandler(nextPage, maxId, minId), maxId, minId);
    }

    @Override
    protected Tweet.Type getTweetType() {
        return Tweet.Type.PROFILE;
    }

    @Override
    public void onFinishComposeTweetDialogSuccess(Tweet newTweet) {
        add(newTweet);
    }
    @Override
    public void onClickProfileImage(User user) {
        //No Op since we don't want to jump from one profile to another.
    }
}