package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;

import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

public class UserTimelineFragment extends TweetsListFragment {

    public static final String PARAM_SCREEN_NAME = "screenName";

    public static UserTimelineFragment newInstance(String screenName,
                                                   TweetsArrayAdapter.OnProfileImageClickListener listener) {
        UserTimelineFragment fragment = new UserTimelineFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_SCREEN_NAME, screenName);
        fragment.setArguments(args);
        fragment.setListener(listener);
        return fragment;
    }

    protected void populateTimeLineWithREST(final boolean nextPage, final long maxId, long minId) {
        String screenName = getArguments().getString(PARAM_SCREEN_NAME);
        twitterClient.getUserTimeline(screenName, getResponseHandler(nextPage), maxId, minId);
    }

    protected Tweet.Type getTweetType() {
        return Tweet.Type.PROFILE;
    }
}