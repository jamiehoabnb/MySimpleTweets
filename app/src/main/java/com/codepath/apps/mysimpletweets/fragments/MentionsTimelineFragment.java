package com.codepath.apps.mysimpletweets.fragments;

public class MentionsTimelineFragment extends TweetsListFragment {

    protected void populateTimeLine(final boolean nextPage, final long maxId) {
        twitterClient.getMentionsTimeline(getResponseHandler(nextPage), maxId);
    }
}
