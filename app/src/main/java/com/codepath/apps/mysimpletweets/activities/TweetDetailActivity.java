package com.codepath.apps.mysimpletweets.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.TwitterApplication;
import com.codepath.apps.mysimpletweets.fragments.ComposeTweetFragment;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.network.TwitterClient;
import com.codepath.apps.mysimpletweets.storage.DBWriteAsyncTask;
import com.codepath.apps.mysimpletweets.util.DeviceDimensionsHelper;
import com.codepath.apps.mysimpletweets.util.InternetCheckUtil;
import com.codepath.apps.mysimpletweets.util.MySimpleTweetsConstants;
import com.codepath.apps.mysimpletweets.util.VideoPlayerUtil;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetDetailActivity extends AppCompatActivity implements
        ComposeTweetFragment.ComposeTweetDialogListener {

    public static final String ARG_USER = "user";
    public static final String ARG_TWEET = "tweet";

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

    private Tweet tweet;
    private User user;
    private TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet_detail);
        ButterKnife.bind(this);

        tweet = Parcels.unwrap(getIntent().getParcelableExtra(ARG_TWEET));
        user = Parcels.unwrap(getIntent().getParcelableExtra(ARG_USER));
        client = TwitterApplication.getRestClient();

        tvUserName.setText(tweet.getUser().getName());
        tvScreenName.setText("@" + tweet.getUser().getScreenName());
        tvCreatedAt.setText(tweet.getFullCreateAt());
        tvBody.setText(tweet.getText());

        ivProfileImage.setImageResource(android.R.color.transparent);
        int width = DeviceDimensionsHelper.getDisplayWidth(this);

        Picasso.with(this)
                .load(tweet.getUser().getProfileImageUrl())
                .resize((int) (width* MySimpleTweetsConstants.PROFILE_PHOTO_WIDTH_FACTOR), 0)
                .transform(
                        new RoundedCornersTransformation(MySimpleTweetsConstants.ROUNDED_CORNER_CONST,
                                MySimpleTweetsConstants.ROUNDED_CORNER_CONST))
                .into(ivProfileImage);

        if (Tweet.MediaType.photo.name().equals(tweet.getMediaType())) {
            ivTweetImage.setImageResource(android.R.color.transparent);
            Picasso.with(this)
                    .load(tweet.getMediaUrl())
                    .resize(width, 0)
                    .transform(
                            new RoundedCornersTransformation(MySimpleTweetsConstants.ROUNDED_CORNER_CONST,
                                    MySimpleTweetsConstants.ROUNDED_CORNER_CONST))
                    .into(ivTweetImage);

        } else {
            ivTweetImage.setVisibility(View.INVISIBLE);
            ivTweetImage.setMaxHeight(0);
            ivTweetImage.setMaxWidth(0);
        }

        if (Tweet.MediaType.video.name().equals(tweet.getMediaType())) {
            VideoPlayerManager<MetaData> videoPlayerManager = VideoPlayerUtil.getVideoPlayerManager();
            videoPlayerManager.playNewVideo(new CurrentItemMetaData(0, getWindow().getDecorView()),
                    vvTweetVideo, tweet.getMediaUrl());
        } else {
            vvTweetVideo.setVisibility(View.INVISIBLE);
        }

        final TweetDetailActivity activity = this;
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

        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentManager fm = getFragmentManager();
                ComposeTweetFragment fragment = ComposeTweetFragment.newInstance(activity, user,
                        tweet.getUser().getScreenName(), tweet.getUser().getName());
                fragment.show(fm, "fragment_compose_tweet_reply");
            }
        });

        ivRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRetweet();
            }
        });

        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickFavorite();
            }
        });

        tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
        tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));

        ivRetweet.setImageDrawable(this.getDrawable(
                tweet.isRetweeted() ? R.drawable.retweet_highlight : R.drawable.retweet));
        ivFavorite.setImageDrawable(this.getDrawable(
                tweet.isFavorited() ? R.drawable.favorite_highlight : R.drawable.favorite));
    }

    private void onClickFavorite() {
        progressBar.setVisibility(View.VISIBLE);
        client.postFavoritesCreate(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);

                if (! tweet.isFavorited()) {
                    tweet.setFavoriteCount(tweet.getFavoriteCount() + 1);
                    tweet.setFavorited(true);
                } else {
                    tweet.setFavoriteCount(tweet.getFavoriteCount() - 1);
                    tweet.setFavorited(false);
                }
                List<Tweet> tweets = new LinkedList<Tweet>();
                tweets.add(tweet);
                DBWriteAsyncTask.newInstance(progressBar).execute(tweets);

                tvFavoriteCount.setText(String.valueOf(tweet.getFavoriteCount()));
                ivFavorite.setImageDrawable(getDrawable(
                        tweet.isFavorited() ? R.drawable.favorite_highlight : R.drawable.favorite));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(getWindow().getDecorView(), errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickFavorite();
                            }
                        })
                        .show();
            }
        });
    }

    private void onClickRetweet() {
        client.postStatusRetweet(tweet, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                progressBar.setVisibility(View.INVISIBLE);

                if (! tweet.isRetweeted()) {
                    tweet.setRetweetCount(tweet.getRetweetCount() + 1);
                    tweet.setRetweeted(true);
                } else {
                    tweet.setRetweetCount(tweet.getRetweetCount() - 1);
                    tweet.setRetweeted(false);
                }
                List<Tweet> tweets = new LinkedList<Tweet>();
                tweets.add(tweet);
                DBWriteAsyncTask.newInstance(progressBar).execute(tweets);

                tvRetweetCount.setText(String.valueOf(tweet.getRetweetCount()));
                ivRetweet.setImageDrawable(getDrawable(
                        tweet.isRetweeted() ? R.drawable.retweet_highlight : R.drawable.retweet));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                progressBar.setVisibility(View.INVISIBLE);
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("ERROR", errorResponse.toString(), throwable);

                int errorMsgId = InternetCheckUtil.isOnline() ?
                        R.string.twitter_api_error : R.string.internet_connection_error;

                Snackbar.make(getWindow().getDecorView(), errorMsgId, Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                onClickRetweet();
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void onFinishComposeTweetDialog(String tweetBody) {
        client.postStatusUpdate(tweetBody,
                this.tweet.getUid(),
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Log.e("ERROR", errorResponse.toString(), throwable);
                    }
                });
    }
}
