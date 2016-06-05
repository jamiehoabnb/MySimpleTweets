package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.view.View;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;

public class FollowersListFragment extends BaseUserListFragment {

    public static FollowersListFragment newInstance(String screenName,
                                                    User loggedInUser) {
        FollowersListFragment fragment = new FollowersListFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_SCREEN_NAME, screenName);
        fragment.setArguments(args);
        fragment.setUser(loggedInUser);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_followers_list;
    }

    protected void populateUserList() {
        String screenName = getArguments().getString(PARAM_SCREEN_NAME);
        progressBar.setVisibility(View.VISIBLE);
        twitterClient.getFollowersList(screenName, cursor, getResponseHandler());
    }

}
