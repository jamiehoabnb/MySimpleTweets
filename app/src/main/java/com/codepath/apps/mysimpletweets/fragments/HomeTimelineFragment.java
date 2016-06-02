package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;

public class HomeTimelineFragment extends TweetsListFragment {

    public static HomeTimelineFragment newInstance(TweetsArrayAdapter.OnProfileImageClickListener listener) {
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.setListener(listener);
        return fragment;
    }

    protected void populateTimeLine(final boolean nextPage, long maxId) {
        twitterClient.getHomeTimeLine(getResponseHandler(nextPage), maxId);
    }
}
