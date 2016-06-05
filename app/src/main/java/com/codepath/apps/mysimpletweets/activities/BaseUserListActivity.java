package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.BaseUserListFragment;
import com.codepath.apps.mysimpletweets.fragments.FriendsListFragment;
import com.codepath.apps.mysimpletweets.models.User;

import org.parceler.Parcels;

import butterknife.ButterKnife;

public abstract class BaseUserListActivity extends AppCompatActivity {

    public static final String ARG_USER = "user";
    public static final String ARG_SCREEN_NAME = "screenName";

    //The screen name of the user whose friends list is being viewed.
    protected String screenName;

    //The current user logged in.
    protected User user;

    public abstract BaseUserListFragment getFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);

        screenName = getIntent().getStringExtra(ARG_SCREEN_NAME);
        user = Parcels.unwrap(getIntent().getParcelableExtra(ARG_USER));

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flUserListContainer, getFragment());
            ft.commit();
        }
    }
}
