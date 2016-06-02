package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;

public class MentionsTimelineFragment extends TweetsListFragment {

    public static MentionsTimelineFragment newInstance(TweetsArrayAdapter.OnProfileImageClickListener listener) {
        MentionsTimelineFragment fragment = new MentionsTimelineFragment();
        fragment.setListener(listener);
        return fragment;
    }

    protected void populateTimeLine(final boolean nextPage, final long maxId) {
        twitterClient.getMentionsTimeline(getResponseHandler(nextPage), maxId);
    }
}
