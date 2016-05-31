package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tweet {

    private String text;
    private long uid;
    private User user;
    private String createAt;

    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
    static {
        inputFormat.setLenient(true);
    }


    public static Tweet fromJSON(JSONObject json) {
        Tweet tweet = new Tweet();
        try {
            tweet.text = json.getString("text");
            tweet.uid = json.getLong("id");
            tweet.user = User.fromJSON(json.getJSONObject("user"));
            tweet.createAt = getRelativeTimeAgo(json.getString("created_at"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tweet;
    }

    private static String getRelativeTimeAgo(String rawJsonDate) {
        String relativeDate = "";
        try {
            long dateMillis = inputFormat.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static List<Tweet> fromJSONArray(JSONArray response) {
        List<Tweet> list = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i));
                if (tweet != null) {
                    list.add(tweet);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public String getText() {
        return text;
    }

    public long getUid() {
        return uid;
    }

    public User getUser() {
        return user;
    }

    public String getCreateAt() {
        return createAt;
    }
}
