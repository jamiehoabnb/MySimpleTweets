package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class MentionsTimelineFragment extends TweetsListFragment {

    public static MentionsTimelineFragment newInstance(TweetsArrayAdapter.TweetListener listener,
                                                       SmoothProgressBar progressBar,
                                                       User user) {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        fragment.setListener(listener);
        fragment.setProgressBar(progressBar);
        fragment.setUser(user);
        return fragment;
    }

    @Override
    protected void populateTimeLineWithREST(final boolean nextPage, final long maxId, long minId) {
        twitterClient.getMentionsTimeline(getResponseHandler(nextPage, maxId, minId), maxId, minId);
    }

    @Override
    protected Tweet.Type getTweetType() {
        return Tweet.Type.MENTIONS;
    }
}
