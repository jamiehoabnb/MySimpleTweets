package com.codepath.apps.mysimpletweets.fragments;

public class MentionsTimelineFragment extends TweetsListFragment {

    protected void populateTimeLine() {
        twitterClient.getMentionsTimeline(getResponseHandler());
    }
}
