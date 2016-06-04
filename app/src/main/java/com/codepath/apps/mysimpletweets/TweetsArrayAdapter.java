package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.util.DeviceDimensionsHelper;
import com.codepath.apps.mysimpletweets.util.VideoPlayerUtil;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.CurrentItemMetaData;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private OnProfileImageClickListener profileImageClickListener;
    private static final int ROUNDED_CORNER_CONST = 10;

    private List<Tweet> objects;

    private enum Type {
        TWEET(0), TWEET_PHOTO(1), TWEET_VIDEO(2);

        final int val;

        Type(int val) {
            this.val = val;
        }
    }

    public interface OnProfileImageClickListener {
        public void onClickProfileImage(User user);
    }

    static class ViewHolder {
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

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public TweetsArrayAdapter(Context context,
                              List<Tweet> objects,
                              OnProfileImageClickListener profileImageClickListener) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.profileImageClickListener = profileImageClickListener;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);

        ViewHolder viewHolder = convertView == null ? null : (ViewHolder) convertView.getTag();
        if (viewHolder == null ||
                viewHolder.ivTweetImage == null && Tweet.MediaType.photo.name().equals(tweet.getMediaType()) ||
                        (viewHolder.vvTweetVideo == null && Tweet.MediaType.video.name().equals(tweet.getMediaType())) ||
                ((viewHolder.ivTweetImage != null || viewHolder.vvTweetVideo != null) && tweet.getMediaType() == null)) {
            int type = getItemViewType(position);

            convertView = getInflatedLayoutForType(type, parent);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUserName.setText(tweet.getUser().getName());
        viewHolder.tvScreenName.setText("@" + tweet.getUser().getScreenName());
        viewHolder.tvCreatedAt.setText(tweet.getRelativeCreateAt());
        viewHolder.tvBody.setText(tweet.getText());

        //Clear our recycled image.
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);

        Picasso.with(getContext())
                .load(tweet.getUser().getProfileImageUrl())
                .resize(200, 0)
                .transform(
                        new RoundedCornersTransformation(ROUNDED_CORNER_CONST, ROUNDED_CORNER_CONST))
                .into(viewHolder.ivProfileImage);

        if (Tweet.MediaType.photo.name().equals(tweet.getMediaType())) {
            viewHolder.ivTweetImage.setImageResource(android.R.color.transparent);
            int width = DeviceDimensionsHelper.getDisplayWidth(getContext());
            Picasso.with(getContext())
                    .load(tweet.getMediaUrl())
                    .resize(width, 0)
                    .transform(
                            new RoundedCornersTransformation(ROUNDED_CORNER_CONST, ROUNDED_CORNER_CONST))
                    .into(viewHolder.ivTweetImage);
        } else if (Tweet.MediaType.video.name().equals(tweet.getMediaType())) {
            VideoPlayerManager<MetaData> videoPlayerManager = VideoPlayerUtil.getVideoPlayerManager();
            videoPlayerManager.playNewVideo(new CurrentItemMetaData(position, parent),
                    viewHolder.vvTweetVideo, tweet.getMediaUrl());
        }

        if (profileImageClickListener != null) {
            viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profileImageClickListener.onClickProfileImage(tweet.getUser());
                }
            });
        }

        return convertView;
    }

    public void add(int position, Tweet tweet) {
        objects.add(position, tweet);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || position >= getCount()) {
            return Type.TWEET.val;
        }

        Tweet tweet = getItem(position);

        if (Tweet.MediaType.photo.name().equals(tweet.getMediaType())) {
            return Type.TWEET_PHOTO.val;
        } else if (Tweet.MediaType.video.name().equals(tweet.getMediaType())) {
            return Type.TWEET_VIDEO.val;
        } else {
            return Type.TWEET.val;
        }
    }

    private View getInflatedLayoutForType(int type, ViewGroup parent) {
        if (type == Type.TWEET_PHOTO.val) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_photo_tweet, parent, false);
        } else if (type == Type.TWEET_VIDEO.val) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_video_tweet, parent, false);
        } else {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
        }
    }
}
