package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

public class HomeTimelineFragment extends TweetsListFragment {

    public static HomeTimelineFragment newInstance(TweetsArrayAdapter.OnProfileImageClickListener listener) {
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.setListener(listener);
        return fragment;
    }

    protected void populateTimeLineWithREST(final boolean nextPage, long maxId, long minId) {
        twitterClient.getHomeTimeLine(getResponseHandler(nextPage), maxId, minId);
    }

    protected Tweet.Type getTweetType() {
        return Tweet.Type.HOME;
    }
}
