package com.codepath.apps.mysimpletweets.fragments;

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
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class ProfileHeaderFragment extends Fragment {

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

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

    public static final String ARG_USER = "user";

    private static final int ROUNDED_CORNER_CONST = 10;

    private TwitterClient client;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_header, parent, false);
        ButterKnife.bind(this, v);

        User user = (User) Parcels.unwrap(getArguments().getParcelable(ARG_USER));
        client = TwitterApplication.getRestClient();
        populateProfileHeader(user);

        return v;
    }

    private void populateProfileHeader(User user) {
        tvFullName.setText(user.getName());
        tvScreenName.setText("@" + user.getScreenName());
        tvTagLine.setText(user.getTagline());
        tvLocation.setText(user.getLocation());
        tvFollowers.setText(String.valueOf(user.getFollowersCount()));
        tvFollowing.setText(String.valueOf(user.getFriendsCount()));

        Picasso.with(getContext())
                .load(user.getBiggerProfileImageUrl())
                .resize(300,0)
                .transform(
                        new RoundedCornersTransformation(ROUNDED_CORNER_CONST, ROUNDED_CORNER_CONST))
                .into(ivProfileImage);
    }
}
