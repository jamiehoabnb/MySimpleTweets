package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;
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

    private View getInflatedLayoutForType(int type, ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
    }

    public void add(int position, Tweet tweet) {
        objects.add(position, tweet);
        notifyDataSetChanged();
    }
}
