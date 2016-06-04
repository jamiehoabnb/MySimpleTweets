package com.codepath.apps.mysimpletweets;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.fragments.ProfileHeaderFragment;
import com.codepath.apps.mysimpletweets.fragments.UserTimelineFragment;
import com.codepath.apps.mysimpletweets.models.User;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
        setSupportActionBar(toolbar);

        this.user = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        boolean disableCache = getIntent().getBooleanExtra(ARG_DISABLE_CACHE, false);
        getSupportActionBar().setTitle("");
        twitterClient = TwitterApplication.getRestClient();
        loadBackgroundBanner();

        if (savedInstanceState == null) {
            UserTimelineFragment fragmentUserTimeline = UserTimelineFragment.newInstance(
                    user.getScreenName(), null, progressBar);
            ProfileHeaderFragment fragmentProfileHeader = new ProfileHeaderFragment();

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

    private void loadBackgroundBanner() {
        final Activity activity = this;
        progressBar.setVisibility(View.VISIBLE);
        twitterClient.getUserProfileBanner(user.getScreenName(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.INVISIBLE);
                JSONObject sizes = response.optJSONObject("sizes");
                if (sizes != null) {
                    JSONObject mobile = sizes.optJSONObject("600x200");
                    if (mobile != null) {
                        try {
                            String url = mobile.getString("url");
                            Target target = new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    toolbar.setBackground(new BitmapDrawable(activity.getResources(), bitmap));
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {
                                    Log.e("ERROR", "Failed to load banner image.");
                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {
                                    int i = 0;
                                }
                            };
                            toolbar.setTag(target);

                            Picasso.with(activity).load(url).into(target);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }
}
