package com.codepath.apps.mysimpletweets.fragments;

public class HomeTimelineFragment extends TweetsListFragment {
    protected void populateTimeLine(final boolean nextPage, long maxId) {
        twitterClient.getHomeTimeLine(getResponseHandler(nextPage), maxId);
    }
}
