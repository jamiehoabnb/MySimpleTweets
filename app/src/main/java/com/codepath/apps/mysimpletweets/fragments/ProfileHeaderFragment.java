package com.codepath.apps.mysimpletweets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.activities.FollowersListActivity;
import com.codepath.apps.mysimpletweets.activities.FriendsListActivity;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.util.DeviceDimensionsHelper;
import com.codepath.apps.mysimpletweets.util.InternetCheckUtil;
import com.codepath.apps.mysimpletweets.util.MySimpleTweetsConstants;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ProfileHeaderFragment extends Fragment {

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.ivBanner)
    ImageView ivBanner;

    @BindView(R.id.tvFullName)
    TextView tvFullName;

    @BindView(R.id.tvScreenName)
    TextView tvScreenName;

    @BindView(R.id.tvTagLine)
    TextView tvTagLine;

    @BindView(R.id.tvLocation)
    TextView tvLocation;

    @BindView(R.id.tvFollowers)
    TextView tvFollowers;

    @BindView(R.id.tvFollowersLabel)
    TextView tvFollowersLabel;

    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    @BindView(R.id.tvFollowingLabel)
    TextView tvFollowingLabel;

    private SmoothProgressBar progressBar;

    public static final String ARG_USER = "user";
    public static final String ARG_LOGGED_IN_USER = "loggedInUser";

    private TwitterClient client;

    public static ProfileHeaderFragment newInstance(SmoothProgressBar progressBar, User user, User loggedInUser) {
        ProfileHeaderFragment fragment = new ProfileHeaderFragment();
        fragment.setProgressBar(progressBar);
        Bundle args = new Bundle();
        args.putParcelable(ProfileHeaderFragment.ARG_USER, Parcels.wrap(user));
        args.putParcelable(ProfileHeaderFragment.ARG_LOGGED_IN_USER, Parcels.wrap(loggedInUser));
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_header, parent, false);
        ButterKnife.bind(this, v);

        final User user = (User) Parcels.unwrap(getArguments().getParcelable(ARG_USER));
        final User loggedInUser = (User) Parcels.unwrap(getArguments().getParcelable(ARG_LOGGED_IN_USER));

        client = TwitterApplication.getRestClient();
        populateProfileHeader(user);
        loadBackgroundBanner(user.getScreenName());

        tvFollowingLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FriendsListActivity.class);
                intent.putExtra(FriendsListActivity.ARG_USER, Parcels.wrap(loggedInUser));
                intent.putExtra(FriendsListActivity.ARG_SCREEN_NAME, user.getScreenName());
                startActivity(intent);
            }
        });

        tvFollowersLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersListActivity.class);
                intent.putExtra(FollowersListActivity.ARG_USER, Parcels.wrap(loggedInUser));
                intent.putExtra(FollowersListActivity.ARG_SCREEN_NAME, user.getScreenName());
                startActivity(intent);
            }
        });

        return v;
    }

    private void setProgressBar(SmoothProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    private String abbreviateCount(int count) {
        if (count > 1000000) {
            String millionCount = String.valueOf(((double) count)/1000000.0);
            return (millionCount.indexOf(".") == -1 ?
                    millionCount : millionCount.substring(0, millionCount.indexOf(".")+2)) + "M";
        } else {
            return String.valueOf(count);
        }
    }

    private void populateProfileHeader(User user) {
        tvFullName.setText(user.getName());
        tvScreenName.setText("@" + user.getScreenName());
        tvTagLine.setText(user.getTagline());
        tvLocation.setText(user.getLocation());
        tvFollowers.setText(abbreviateCount(user.getFollowersCount()));
        tvFollowing.setText(abbreviateCount(user.getFriendsCount()));
        int width = DeviceDimensionsHelper.getDisplayWidth(getContext());

        Picasso.with(getContext())
                .load(user.getBiggerProfileImageUrl())
                .resize((int) (width* MySimpleTweetsConstants.PROFILE_LARGE_PHOTO_WIDTH_FACTOR), 0)
                .transform(
                        new RoundedCornersTransformation(MySimpleTweetsConstants.ROUNDED_CORNER_CONST,
                                MySimpleTweetsConstants.ROUNDED_CORNER_CONST))
                .into(ivProfileImage);
    }

    private void loadBackgroundBanner(final String screenName) {
        final Activity activity = getActivity();
        progressBar.setVisibility(View.VISIBLE);
        client.getUserProfileBanner(screenName, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                progressBar.setVisibility(View.INVISIBLE);
                JSONObject sizes = response.optJSONObject("sizes");
                if (sizes != null) {
                    JSONObject banner = sizes.optJSONObject(MySimpleTweetsConstants.BANNER_SIZE);
                    if (banner != null) {
                        try {
                            String url = banner.getString("url");
                            int width = DeviceDimensionsHelper.getDisplayWidth(getContext());
                            Picasso.with(activity)
                                    .load(url)
                                    .resize(width, 0)
                                    .into(ivBanner);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(ivBanner, R.string.internal_error, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                super.onFailure(statusCode, headers, throwable, response);
                progressBar.setVisibility(View.INVISIBLE);
                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(ivBanner, errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(getContext().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                loadBackgroundBanner(screenName);
                            }
                        })
                        .show();
            }
        });
    }
}
