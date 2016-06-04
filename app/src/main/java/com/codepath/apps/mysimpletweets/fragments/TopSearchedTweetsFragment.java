package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class TopSearchedTweetsFragment extends TweetsListFragment {

    private String query;

    public static TopSearchedTweetsFragment newInstance(String query,
                                                        TweetsArrayAdapter.OnProfileImageClickListener listener,
                                                        SmoothProgressBar progressBar) {
        TopSearchedTweetsFragment fragment = new TopSearchedTweetsFragment();
        fragment.query = query;
        fragment.setListener(listener);
        fragment.setProgressBar(progressBar);
        fragment.disableCache();
        fragment.disableEndlessScroll();
        return fragment;
    }

    @Override
    protected void populateTimeLineWithREST(final boolean nextPage, long maxId, long minId) {
        twitterClient.getSearchResults(getResponseHandler(nextPage), query,
                TwitterClient.SearchResultType.popular, maxId, minId);
    }

    @Override
    protected Tweet.Type getTweetType() {
        return Tweet.Type.HOME;
    }
}
