package com.codepath.apps.mysimpletweets.fragments;

import com.codepath.apps.mysimpletweets.adapters.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.models.Tweet;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class AllSearchedTweetsFragment extends BaseTweetsListFragment {

    private String query;

    public static AllSearchedTweetsFragment newInstance(String query,
                                                        SmoothProgressBar progressBar,
                                                        User user) {
        AllSearchedTweetsFragment fragment = new AllSearchedTweetsFragment();
        fragment.query = query;
        fragment.setProgressBar(progressBar);
        fragment.setUser(user);
        fragment.disableCache();
        return fragment;
    }

    @Override
    protected void populateTimeLineWithREST(final boolean nextPage, long maxId, long minId) {
        twitterClient.getSearchResults(getResponseHandler(nextPage, maxId, minId), query,
                TwitterClient.SearchResultType.recent, maxId, minId);
    }

    @Override
    protected Tweet.Type getTweetType() {
        return Tweet.Type.HOME;
    }

    @Override
    public void onFinishComposeTweetDialogSuccess(Tweet newTweet) {
        //No op
    }
}
