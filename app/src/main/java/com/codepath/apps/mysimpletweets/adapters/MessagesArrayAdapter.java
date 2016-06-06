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
import com.codepath.apps.mysimpletweets.models.Message;
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

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MessagesArrayAdapter extends ArrayAdapter<Message> {

    private List<Message> objects;

    private User loggedInUser;

    static class ViewHolder {

        @BindView(R.id.ivOtherProfileImage)
        ImageView ivOtherProfileImage;

        @BindView(R.id.ivMyProfileImage)
        ImageView ivMyProfileImage;

        @BindView(R.id.tvMessage)
        TextView tvMessage;

        @BindView(R.id.tvDate)
        TextView tvDate;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public MessagesArrayAdapter(Context context,
                                List<Message> objects,
                                User loggedInUser) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.objects = objects;
        this.loggedInUser = loggedInUser;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Message message = getItem(position);

        ViewHolder viewHolder = convertView == null ? null : (ViewHolder) convertView.getTag();
        if (viewHolder == null) {
            int type = getItemViewType(position);
            convertView = getInflatedLayoutForType(type, parent);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvMessage.setText(message.getText());
        viewHolder.tvDate.setText(MySimpleTweetsConstants.longDateOutputFormat.format(message.getCreatedAt()));

        //Clear our recycled image.
        viewHolder.ivOtherProfileImage.setImageResource(android.R.color.transparent);
        viewHolder.ivMyProfileImage.setImageResource(android.R.color.transparent);
        int width = DeviceDimensionsHelper.getDisplayWidth(getContext());

        final String otherProfileImage = message.getRecipient().getUid() == loggedInUser.getUid() ?
                message.getSender().getProfileImageUrl() : message.getRecipient().getProfileImageUrl();

        Picasso.with(getContext())
                .load(otherProfileImage)
                .resize((int) (width* MySimpleTweetsConstants.PROFILE_PHOTO_WIDTH_FACTOR), 0)
                .transform(
                        new RoundedCornersTransformation(MySimpleTweetsConstants.ROUNDED_CORNER_CONST,
                                MySimpleTweetsConstants.ROUNDED_CORNER_CONST))
                .into(viewHolder.ivOtherProfileImage);

        Picasso.with(getContext())
                .load(loggedInUser.getProfileImageUrl())
                .resize((int) (width* MySimpleTweetsConstants.PROFILE_PHOTO_WIDTH_FACTOR), 0)
                .transform(
                        new RoundedCornersTransformation(MySimpleTweetsConstants.ROUNDED_CORNER_CONST,
                                MySimpleTweetsConstants.ROUNDED_CORNER_CONST))
                .into(viewHolder.ivMyProfileImage);

        return convertView;
    }

    public void addMessages(List<Message> msgs) {
        objects.addAll(msgs);
        Collections.sort(objects);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || position >= objects.size()) {
            return 0;
        }

        Message message = getItem(position);
        return message.getRecipient().getUid() == loggedInUser.getUid() ? 0 : 1;
    }

    private View getInflatedLayoutForType(int type, ViewGroup parent) {
        if (type == 0) {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_message_received, parent, false);
        } else {
            return LayoutInflater.from(getContext()).inflate(R.layout.item_message_sent, parent, false);
        }
    }
}
