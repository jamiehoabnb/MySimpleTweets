package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.listeners.TweetListener;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class HomeTimelineFragment extends BaseTweetsListFragment {

    public static HomeTimelineFragment newInstance(SmoothProgressBar progressBar,
                                                   User user) {
        HomeTimelineFragment fragment = new HomeTimelineFragment();
        fragment.setProgressBar(progressBar);
        fragment.setUser(user);
        return fragment;
    }

    @Override
    public void onFinishComposeTweetDialogSuccess(Tweet newTweet) {
        add(newTweet);
    }

    @Override
    protected void populateTimeLineWithREST(final boolean nextPage, long maxId, long minId) {
        twitterClient.getHomeTimeLine(getResponseHandler(nextPage, maxId, minId), maxId, minId);
    }

    @Override
    protected Tweet.Type getTweetType() {
        return Tweet.Type.HOME;
    }
}
