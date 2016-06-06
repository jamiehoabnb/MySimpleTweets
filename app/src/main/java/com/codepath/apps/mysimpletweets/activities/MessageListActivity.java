package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.fragments.BaseUserListFragment;
import com.codepath.apps.mysimpletweets.fragments.MessageListFragment;
import com.codepath.apps.mysimpletweets.models.User;

import org.parceler.Parcels;

import butterknife.ButterKnife;

public class MessageListActivity extends AppCompatActivity {

    public static final String ARG_USER = "user";

    //The current user logged in.
    protected User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        ButterKnife.bind(this);

        user = Parcels.unwrap(getIntent().getParcelableExtra(ARG_USER));

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flMessageListContainer, MessageListFragment.newInstance(user));
            ft.commit();
        }
    }
}
