package com.codepath.apps.mysimpletweets.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;

import com.codepath.apps.mysimpletweets.R;
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
    public static final String ARG_USER = "user";
    public static final String ARG_DISABLE_CACHE = "disable_cache";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        User user = Parcels.unwrap(getIntent().getParcelableExtra(ARG_USER));
        boolean disableCache = getIntent().getBooleanExtra(ARG_DISABLE_CACHE, false);

        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(
                    user.getScreenName(), progressBar, user);
            ProfileHeaderFragment fragmentProfileHeader = ProfileHeaderFragment.newInstance(progressBar, user);

            if (disableCache) {
                fragmentUserTimeline.disableCache();
            }

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
