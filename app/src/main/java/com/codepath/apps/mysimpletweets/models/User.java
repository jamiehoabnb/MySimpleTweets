package com.codepath.apps.mysimpletweets.models;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String name;
    private long uid;
    private String screenName;
    private String profileImageUrl;
    private String tagline;
    private String location;
    private int followersCount;
    private int friendsCount;

    public static User fromJSON(JSONObject json) {
        User user = new User();

        try {
            user.name = json.getString("name");
            user.uid = json.getLong("id");
            user.screenName = json.getString("screen_name");
            user.location = json.getString("location");
            user.profileImageUrl = json.getString("profile_image_url");
            user.tagline = json.getString("description");
            user.followersCount = json.getInt("followers_count");
            user.friendsCount = json.getInt("friends_count");
        } catch (JSONException e) {

        }

        return user;
    }

    public String getName() {
        return name;
    }

    public long getUid() {
        return uid;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getTagline() {
        return tagline;
    }

    public String getLocation() {
        return location;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }
}
