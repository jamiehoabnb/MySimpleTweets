package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;

public class MentionsTimelineFragment extends TweetsListFragment {

    public static MentionsTimelineFragment newInstance(TweetsArrayAdapter.OnProfileImageClickListener listener) {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        fragment.setListener(listener);
        return fragment;
    }

    protected void populateTimeLineWithREST(final boolean nextPage, final long maxId, long minId) {
        twitterClient.getMentionsTimeline(getResponseHandler(nextPage), maxId, minId);
    }

    protected Tweet.Type getTweetType() {
        return Tweet.Type.MENTIONS;
    }
}
