package com.codepath.apps.mysimpletweets.activities;

import com.codepath.apps.mysimpletweets.fragments.BaseUserListFragment;
import com.codepath.apps.mysimpletweets.fragments.FriendsListFragment;

public class FriendsListActivity extends BaseUserListActivity {

    @Override
    public BaseUserListFragment getFragment() {
        return FriendsListFragment.newInstance(screenName, user);
    }
}
