package com.codepath.apps.mysimpletweets.fragments;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.util.DeviceDimensionsHelper;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ComposeTweetFragment extends DialogFragment {

    public static final String ARG_USER = "user";

    private User user;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.etTweet)
    EditText etTweet;

    @BindView(R.id.tvTweetLength)
    TextView tvTweetLength;

    private static final int TWEET_MAX_LENGTH = 140;

    public interface ComposeTweetDialogListener {
        void onFinishDialog(String tweet);
    }

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    public static ComposeTweetFragment newInstance(User user) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, Parcels.wrap(user));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) Parcels.unwrap(getArguments().getParcelable(ARG_USER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_compose_tweet, container, false);
        ButterKnife.bind(this, view);

        etTweet.addTextChangedListener(new TextWatcher(){

            @Override
            public void afterTextChanged(Editable s) {
                tvTweetLength.setText(String.valueOf(TWEET_MAX_LENGTH - etTweet.getText().toString().length()));
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        Picasso.with(getActivity()).load(user.getProfileImageUrl()).into(ivProfileImage);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        int width = DeviceDimensionsHelper.getDisplayWidth(getActivity());
        int height = DeviceDimensionsHelper.getDisplayHeight(getActivity());
        Window window = getDialog().getWindow();
        window.setLayout(width, height);
        window.setGravity(Gravity.CENTER);
    }

    @OnClick(R.id.btTweet)
    public void save(View view) {
        ((ComposeTweetDialogListener) getActivity()).onFinishDialog(etTweet.getText().toString());
        dismiss();
    }

    @OnClick(R.id.btCancel)
    public void cancel(View view) {
        dismiss();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
