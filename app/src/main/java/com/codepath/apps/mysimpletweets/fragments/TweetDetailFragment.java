package com.codepath.apps.mysimpletweets.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.activities.ProfileActivity;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.util.DeviceDimensionsHelper;
import com.codepath.apps.mysimpletweets.util.MySimpleTweetsConstants;
import com.codepath.apps.mysimpletweets.util.VideoPlayerUtil;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import org.parceler.Parcels;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetDetailFragment extends BaseTweetFragment {

    public static final String ARG_TWEET = "tweet";

    private Tweet tweet;

    @BindView(R.id.progressBar)
    SmoothProgressBar progressBar;

    @BindView(R.id.tvUserName)
    TextView tvUserName;

    @BindView(R.id.tvScreenName)
    TextView tvScreenName;

    @BindView(R.id.tvCreatedAt)
    TextView tvCreatedAt;

    @BindView(R.id.tvBody)
    TextView tvBody;

    @BindView(R.id.ivProfileImage)
    ImageView ivProfileImage;

    @Nullable
    @BindView(R.id.ivTweetImage)
    ImageView ivTweetImage;

    @Nullable
    @BindView(R.id.vvTweetVideo)
    VideoPlayerView vvTweetVideo;

    @BindView(R.id.ivReply)
    ImageView ivReply;

    @BindView(R.id.ivRetweet)
    ImageView ivRetweet;

    @BindView(R.id.tvRetweetCount)
    TextView tvRetweetCount;

    @BindView(R.id.ivFavorite)
    ImageView ivFavorite;

    @BindView(R.id.tvFavoriteCount)
    TextView tvFavoriteCount;

    public static TweetDetailFragment newInstance(User user, Tweet tweet) {
        TweetDetailFragment fragment = new TweetDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_TWEET, Parcels.wrap(tweet));
        fragment.setArguments(args);
        fragment.setUser(user);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweet_detail, parent, false);
        ButterKnife.bind(this, v);

        setProgressBar(progressBar);
        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvCreatedAt.setText(tweet.getFullCreateAt());
        tvBody.setText(tweet.getText());

        ivProfileImage.setImageResource(android.R.color.transparent);
        int width = DeviceDimensionsHelper.getDisplayWidth(getContext());

        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .resize((int) (width* MySimpleTweetsConstants.PROFILE_PHOTO_WIDTH_FACTOR), 0)
                .transform(
                        new RoundedCornersTransformation(MySimpleTweetsConstants.ROUNDED_CORNER_CONST,
                                MySimpleTweetsConstants.ROUNDED_CORNER_CONST))
                .into(ivProfileImage);

        if (Tweet.MediaType.photo.name().equals(tweet.getMediaType())) {
            ivTweetImage.setImageResource(android.R.color.transparent);
            Picasso.with(getContext())
                    .load(tweet.getMediaUrl())
                    .resize(width, 0)
                    .transform(
                            new RoundedCornersTransformation(MySimpleTweetsConstants.ROUNDED_CORNER_CONST,
                                    MySimpleTweetsConstants.ROUNDED_CORNER_CONST))
                    .into(ivTweetImage);

        } else {
            ivTweetImage.setVisibility(View.GONE);
        }

        if (Tweet.MediaType.video.name().equals(tweet.getMediaType())) {
            VideoPlayerManager<MetaData> videoPlayerManager = VideoPlayerUtil.getVideoPlayerManager();
            videoPlayerManager.playNewVideo(new CurrentItemMetaData(0, parent),
                    vvTweetVideo, tweet.getMediaUrl());
        } else {
            vvTweetVideo.setVisibility(View.GONE);
        }

        final Activity activity = getActivity();
        ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ProfileActivity.class);
                intent.putExtra(ProfileActivity.ARG_USER, Parcels.wrap(tweet.getUser()));
                //We don't cache tweet's for profile page of other users.
                intent.putExtra(ProfileActivity.ARG_DISABLE_CACHE, true);
                activity.startActivity(intent);
            }
        });

        final TweetDetailFragment tweetDetailFragment = this;
        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager fm = activity.getFragmentManager();
                ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(tweetDetailFragment,
                        user, tweet);
                fragment.show(fm, "fragment_compose_tweet_reply");
            }
        });

        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweetDetailFragment.onClickRetweet(tweet);
                tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
                ivRetweet.setImageDrawable(getContext().getDrawable(
                        tweet.isRetweeted() ? R.drawable.retweet_highlight : R.drawable.retweet));
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweetDetailFragment.onClickFavorite(tweet);
                tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
                ivFavorite.setImageDrawable(getContext().getDrawable(
                        tweet.isFavorited() ? R.drawable.favorite_highlight : R.drawable.favorite));
            }
        });

        tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));

        ivRetweet.setImageDrawable(activity.getDrawable(
                tweet.isRetweeted() ? R.drawable.retweet_highlight : R.drawable.retweet));
        ivFavorite.setImageDrawable(activity.getDrawable(
                tweet.isFavorited() ? R.drawable.favorite_highlight : R.drawable.favorite));
        return v;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        tweet = (Tweet) Parcels.unwrap(getArguments().getParcelable(ARG_TWEET));
    }

    @Override
    public void onFinishComposeTweetDialogSuccess(Tweet newTweet) {
        //No op
    }
}
