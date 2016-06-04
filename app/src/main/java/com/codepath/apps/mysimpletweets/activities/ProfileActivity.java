package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.fragments.ProfileHeaderFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.progressBar)
    SmoothProgressBar progressBar;

    User user;

    public static final String ARG_USER = "user";
    public static final String ARG_DISABLE_CACHE = "disable_cache";

    private TwitterClient twitterClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        this.user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        boolean disableCache = getIntent().getBooleanExtra(ARG_DISABLE_CACHE, false);
        twitterClient = TwitterApplication.getRestClient();

        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(
                    user.getScreenName(), null, progressBar);
            ProfileHeaderFragment fragmentProfileHeader = ProfileHeaderFragment.newInstance(progressBar);

            if (disableCache) {
                fragmentUserTimeline.disableCache();
            }

            Bundle args = new Bundle();
            args.putParcelable(ProfileHeaderFragment.ARG_USER, Parcels.wrap(user));
            fragmentProfileHeader.setArguments(args);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flHeaderContainer, fragmentProfileHeader);
            ft.replace(R.id.flUserTimeLineContainer, fragmentUserTimeline);
            ft.commit();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }
}
