package com.codepath.apps.mysimpletweets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {



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

    public TweetsArrayAdapter(Context context, List<Tweet> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);

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
        viewHolder.tvCreatedAt.setText(tweet.getCreateAt());
        viewHolder.tvBody.setText(tweet.getText());

        //Clear our recycled image.
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);

        Picasso.with(getContext()).load(tweet.getUser().getProfileImageUrl()).into(viewHolder.ivProfileImage);
        return convertView;
    }

    private View getInflatedLayoutForType(int type, ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
    }
}
