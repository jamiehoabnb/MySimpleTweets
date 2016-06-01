package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;

public class UserTimelineFragment extends TweetsListFragment {

    public static final String PARAM_SCREEN_NAME = "screenName";

    public static UserTimelineFragment newInstance(String screenName) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_SCREEN_NAME, screenName);
        fragment.setArguments(args);
        return fragment;
    }

    protected void populateTimeLine(final boolean nextPage, final long maxId) {
        String screenName = getArguments().getString(PARAM_SCREEN_NAME);
        twitterClient.getUserTimeline(screenName, getResponseHandler(nextPage), maxId);
    }
}