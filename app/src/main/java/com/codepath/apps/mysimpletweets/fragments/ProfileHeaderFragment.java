package com.codepath.apps.mysimpletweets.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.TwitterClient;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.util.DeviceDimensionsHelper;
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

    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    private SmoothProgressBar progressBar;

    public static final String ARG_USER = "user";

    private static final int ROUNDED_CORNER_CONST = 10;

    private TwitterClient client;

    public static ProfileHeaderFragment newInstance(SmoothProgressBar progressBar) {
        ProfileHeaderFragment fragment = new ProfileHeaderFragment();
        fragment.setProgressBar(progressBar);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_header, parent, false);
        ButterKnife.bind(this, v);

        User user = (User) Parcels.unwrap(getArguments().getParcelable(ARG_USER));
        client = TwitterApplication.getRestClient();
        populateProfileHeader(user);
        loadBackgroundBanner(user.getScreenName());

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

        Picasso.with(getContext())
                .load(user.getBiggerProfileImageUrl())
                .resize(300,0)
                .transform(
                        new RoundedCornersTransformation(ROUNDED_CORNER_CONST, ROUNDED_CORNER_CONST))
                .into(ivProfileImage);
    }

    private void loadBackgroundBanner(String screenName) {
        final Activity activity = getActivity();
        progressBar.setVisibility(View.VISIBLE);
        client.getUserProfileBanner(screenName, new JsonHttpResponseHandler() {
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
                            int width = DeviceDimensionsHelper.getDisplayWidth(getContext());
                            Picasso.with(activity)
                                    .load(url)
                                    .resize(width, 0)
                                    .into(ivBanner);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                super.onFailure(statusCode, headers, throwable, response);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}
