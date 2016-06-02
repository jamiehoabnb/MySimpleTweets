package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Table(name = "tweets")
public class Tweet extends Model {

    @Column(name = "text")
    private String text;

    @Column(name = "uid")
    private long uid;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    @Column(name = "created_at")
    private String createAt;

    //The types of tweets that we are caching.
    public enum Type {
        HOME, MENTIONS, PROFILE;
    }

    @Column(name = "type")
    private Type type;

    private static final SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
    static {
        inputFormat.setLenient(true);
    }

    private static final SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yy");

    private static final double SECOND = 1000;
    private static final double MINUTE = 60*SECOND;
    private static final double HOUR = 60*MINUTE;
    private static final double DAY = 24*HOUR;

    public static final int TABLE_MAX_SIZE = 500;

    public static Tweet fromJSON(JSONObject json, Type type) {
        Tweet tweet = new Tweet();
        try {
            tweet.text = json.getString("text");
            tweet.uid = json.getLong("id");
            tweet.user = User.fromJSON(json.getJSONObject("user"));
            tweet.type = type;
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

    public static List<Tweet> fromJSONArray(JSONArray response, Type type) {
        List<Tweet> list = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                Tweet tweet = Tweet.fromJSON(response.getJSONObject(i), type);
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

    public Type getType() {
        return type;
    }

    public static List<Tweet> recentItems(Type type) {
        return new Select().from(Tweet.class).where("type = ?", type.name()).orderBy("uid DESC").execute();
    }

    public static void deleteOldTweets(Type type, long minUid) {
        new Delete().from(Tweet.class).where("uid < ?", minUid).and("type = ?", type.name()).execute();
    }
}
