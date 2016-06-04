package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class HomeTimelineFragment extends TweetsListFragment {

    public static HomeTimelineFragment newInstance(TweetsArrayAdapter.OnProfileImageClickListener listener,
                                                   SmoothProgressBar progressBar) {
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.setListener(listener);
        fragment.setProgressBar(progressBar);
        return fragment;
    }

    @Override
    protected void populateTimeLineWithREST(final boolean nextPage, long maxId, long minId) {
        twitterClient.getHomeTimeLine(getResponseHandler(nextPage), maxId, minId);
    }

    @Override
    protected Tweet.Type getTweetType() {
        return Tweet.Type.HOME;
    }
}
