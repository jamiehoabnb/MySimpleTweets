package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;

public class AllSearchedTweetsFragment extends TweetsListFragment {

    private String query;

    public static AllSearchedTweetsFragment newInstance(String query,
                                                        TweetsArrayAdapter.OnProfileImageClickListener listener) {
        AllSearchedTweetsFragment fragment = new AllSearchedTweetsFragment();
        fragment.query = query;
        fragment.setListener(listener);
        fragment.disableCache();
        return fragment;
    }

    @Override
    protected void populateTimeLineWithREST(final boolean nextPage, long maxId, long minId) {
        twitterClient.getSearchResults(getResponseHandler(nextPage), query,
                TwitterClient.SearchResultType.recent, maxId, minId);
    }

    @Override
    protected Tweet.Type getTweetType() {
        return Tweet.Type.HOME;
    }
}
