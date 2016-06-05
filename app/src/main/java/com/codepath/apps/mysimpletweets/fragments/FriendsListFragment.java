package com.codepath.apps.mysimpletweets.fragments;

import android.os.Bundle;
import android.view.View;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.User;

public class FriendsListFragment extends BaseUserListFragment {

    public static FriendsListFragment newInstance(String screenName,
                                                   User loggedInUser) {
        FriendsListFragment fragment = new FriendsListFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_SCREEN_NAME, screenName);
        fragment.setArguments(args);
        fragment.setUser(loggedInUser);
        return fragment;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_friends_list;
    }

    protected void populateUserList() {
        String screenName = getArguments().getString(PARAM_SCREEN_NAME);
        progressBar.setVisibility(View.VISIBLE);
        twitterClient.getFriendsList(screenName, cursor, getResponseHandler());
    }

}
