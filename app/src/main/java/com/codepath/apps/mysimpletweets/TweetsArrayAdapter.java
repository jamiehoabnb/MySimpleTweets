package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.graphics.Movie;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
import com.codepath.apps.mysimpletweets.util.DeviceDimensionsHelper;
import com.squareup.picasso.Picasso;

import org.parceler.Parcel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    private OnProfileImageClickListener profileImageClickListener;
    private static final int ROUNDED_CORNER_CONST = 3;

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
        VideoView vvTweetVideo;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public TweetsArrayAdapter(Context context, List<Tweet> objects, OnProfileImageClickListener profileImageClickListener) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.profileImageClickListener = profileImageClickListener;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Tweet tweet = getItem(position);

        ViewHolder viewHolder;
        if (convertView == null) {
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
                .transform(
                        new RoundedCornersTransformation(ROUNDED_CORNER_CONST, ROUNDED_CORNER_CONST))
                .into(viewHolder.ivProfileImage);

        if (viewHolder.ivTweetImage != null) {
            viewHolder.ivTweetImage.setImageResource(android.R.color.transparent);
            int width = DeviceDimensionsHelper.getDisplayWidth(getContext());
            Picasso.with(getContext())
                    .load(tweet.getMediaUrl())
                    .resize(width, 0)
                    .transform(
                            new RoundedCornersTransformation(ROUNDED_CORNER_CONST, ROUNDED_CORNER_CONST))
                    .into(viewHolder.ivTweetImage);
        } else if (viewHolder.vvTweetVideo != null) {
            viewHolder.vvTweetVideo.setVideoPath("http://techslides.com/demos/sample-videos/small.mp4");
            MediaController mediaController = new MediaController(getContext());
            mediaController.setAnchorView(viewHolder.vvTweetVideo);
            viewHolder.vvTweetVideo.setMediaController(mediaController);
            viewHolder.vvTweetVideo.requestFocus();
            final VideoView videoView = viewHolder.vvTweetVideo;

            viewHolder.vvTweetVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                }
            });
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

        if (Tweet.MediaType.photo.equals(tweet.getMediaType())) {
            return Type.TWEET_PHOTO.val;
        } else if (Tweet.MediaType.video.equals(tweet.getMediaType())) {
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
