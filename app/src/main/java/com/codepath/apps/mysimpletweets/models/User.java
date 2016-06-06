package com.codepath.apps.mysimpletweets.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Table(name = "users")
@Parcel(analyze={User.class})
public class User extends Model {

    @Column(name = "name")
    String name;

    @Column(name = "uid")
    long uid;

    @Column(name = "screen_name")
    String screenName;

    @Column(name = "profile_image_url")
    String profileImageUrl;

    @Column(name = "tagline")
    String tagline;

    @Column(name = "location")
    String location;

    @Column(name = "followers_count")
    int followersCount;

    @Column(name = "friends_count")
    int friendsCount;

    public static List<User> fromJSONArray(JSONArray response) {
        List<User> list = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                User user = User.fromJSON(response.getJSONObject(i));
                if (user != null) {
                    list.add(user);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

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
            e.printStackTrace();
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

    public String getBiggerProfileImageUrl() {
        return profileImageUrl.replace("normal", "bigger");
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
