package com.codepath.apps.mysimpletweets.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.listeners.TweetListener;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class UsersArrayAdapter extends ArrayAdapter<User> {

    private TweetListener tweetListener;

    private List<User> objects;

    static class ViewHolder {
        @BindView(R.id.tvUserName)
        TextView tvUserName;

        @BindView(R.id.tvScreenName)
        TextView tvScreenName;

        @BindView(R.id.ivProfileImage)
        ImageView ivProfileImage;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public UsersArrayAdapter(Context context,
                             List<User> objects,
                             TweetListener tweetListener) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.tweetListener = tweetListener;
        this.objects = objects;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final User user = getItem(position);

        ViewHolder viewHolder = convertView == null ? null : (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            int type = getItemViewType(position);

            convertView = getInflatedLayoutForType(type, parent);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvUserName.setText(user.getName());
        viewHolder.tvScreenName.setText("@" + user.getScreenName());

        //Clear our recycled image.
        viewHolder.ivProfileImage.setImageResource(android.R.color.transparent);
        int width = DeviceDimensionsHelper.getDisplayWidth(getContext());

        Picasso.with(getContext())
                .load(user.getProfileImageUrl())
                .resize((int) (width* MySimpleTweetsConstants.PROFILE_PHOTO_WIDTH_FACTOR), 0)
                .transform(
                        new RoundedCornersTransformation(MySimpleTweetsConstants.ROUNDED_CORNER_CONST,
                                MySimpleTweetsConstants.ROUNDED_CORNER_CONST))
                .into(viewHolder.ivProfileImage);

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetListener.onClickProfileImage(user);
                }
            });

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private View getInflatedLayoutForType(int type, ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.item_user, parent, false);
    }
}
