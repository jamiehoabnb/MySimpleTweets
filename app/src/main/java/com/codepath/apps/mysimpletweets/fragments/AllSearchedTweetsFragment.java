package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class AllSearchedTweetsFragment extends TweetsListFragment {

    private String query;

    public static AllSearchedTweetsFragment newInstance(String query,
                                                        TweetsArrayAdapter.OnProfileImageClickListener listener,
                                                        SmoothProgressBar progressBar) {
        AllSearchedTweetsFragment fragment = new AllSearchedTweetsFragment();
        fragment.query = query;
        fragment.setListener(listener);
        fragment.setProgressBar(progressBar);
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
