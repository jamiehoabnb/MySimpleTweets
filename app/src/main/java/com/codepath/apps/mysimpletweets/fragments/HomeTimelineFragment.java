package com.codepath.apps.mysimpletweets.fragments;

public class HomeTimelineFragment extends TweetsListFragment {
    protected void populateTimeLine() {
        twitterClient.getHomeTimeLine(getResponseHandler());
    }
}
