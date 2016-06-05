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
import com.codepath.apps.mysimpletweets.listeners.ComposeTweetDialogListener;
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
    public static final String ARG_REPLY_TWEET = "replyTweet";

    private User user;
    private Tweet replyTweet;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @BindView(R.id.etTweet)
    EditText etTweet;

    @BindView(R.id.tvTweetLength)
    TextView tvTweetLength;

    private ComposeTweetDialogListener listener;

    private static final int TWEET_MAX_LENGTH = 140;

    public ComposeTweetFragment() {
        // Required empty public constructor
    }

    public static ComposeTweetFragment newInstance(ComposeTweetDialogListener listener,
                                                   User user, Tweet replyTweet) {
        ComposeTweetFragment fragment = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, Parcels.wrap(user));
        args.putParcelable(ARG_REPLY_TWEET, Parcels.wrap(replyTweet));
        fragment.setArguments(args);
        fragment.listener = listener;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        user = (User) Parcels.unwrap(getArguments().getParcelable(ARG_USER));
        replyTweet = (Tweet) Parcels.unwrap(getArguments().getParcelable(ARG_REPLY_TWEET));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                replyTweet == null ?
                        R.layout.fragment_compose_tweet : R.layout.fragment_compose_tweet_reply,
                container, false);
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

        if (replyTweet != null) {
            etTweet.clearFocus();
            etTweet.setHint(getString(R.string.reply_to) + " " + replyTweet.getUser().getName());

            //Initialize the reply with the users screen name when clicked.
            etTweet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus && etTweet.getText().length() == 0) {
                        etTweet.setText("@" + replyTweet.getUser().getScreenName());
                    }
                }
            });
        }

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
        listener.onFinishComposeTweetDialog(etTweet.getText().toString(), replyTweet);
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
