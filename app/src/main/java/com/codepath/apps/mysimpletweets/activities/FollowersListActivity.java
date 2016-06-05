package com.codepath.apps.mysimpletweets.activities;

import com.codepath.apps.mysimpletweets.fragments.BaseUserListFragment;
import com.codepath.apps.mysimpletweets.fragments.FollowersListFragment;

public class FollowersListActivity extends BaseUserListActivity {

    @Override
    public BaseUserListFragment getFragment() {
        return FollowersListFragment.newInstance(screenName, user);
    }
}
