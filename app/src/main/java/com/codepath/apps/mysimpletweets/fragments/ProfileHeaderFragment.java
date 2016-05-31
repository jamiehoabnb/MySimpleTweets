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
import com.codepath.apps.mysimpletweets.models.User;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileHeaderFragment extends Fragment {

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.tvFullName)
    TextView tvFullName;

    @BindView(R.id.tvTagLine)
    TextView tvTagLine;

    @BindView(R.id.tvLocation)
    TextView tvLocation;

    @BindView(R.id.tvFollowers)
    TextView tvFollowers;

    @BindView(R.id.tvFollowing)
    TextView tvFollowing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_header, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    public void populateProfileHeader(User user) {
        tvFullName.setText(user.getName());
        tvTagLine.setText(user.getTagline());
        tvLocation.setText(user.getLocation());
        tvFollowers.setText(String.valueOf(user.getFollowersCount()));
        tvFollowing.setText(String.valueOf(user.getFriendsCount()));
        Picasso.with(getContext()).load(user.getProfileImageUrl()).into(ivProfileImage);
    }
}
