package com.codepath.apps.mysimpletweets.models;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

    private static final SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yy");

    private static final double SECOND = 1000;
    private static final double MINUTE = 60*SECOND;
    private static final double HOUR = 60*MINUTE;
    private static final double DAY = 24*HOUR;


    public static Tweet fromJSON(JSONObject json) {
        Tweet tweet = new Tweet();
        try {
            tweet.text = json.getString("text");
            tweet.uid = json.getLong("id");
            tweet.user = User.fromJSON(json.getJSONObject("user"));
            Date createDate = inputFormat.parse(json.getString("created_at"));
            long diff = System.currentTimeMillis() - createDate.getTime();

            if (diff < MINUTE) {
                tweet.createAt = String.valueOf(Math.round(diff/SECOND)) + "s";
            } else if (diff < HOUR) {
                tweet.createAt = String.valueOf(Math.round(diff/MINUTE)) + "m";
            } else if (diff < DAY) {
                tweet.createAt = String.valueOf(Math.round(diff/HOUR)) + "h";
            } else {
                tweet.createAt = outputFormat.format(createDate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tweet;
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
