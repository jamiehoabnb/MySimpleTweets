package com.codepath.apps.mysimpletweets.models;

import com.codepath.apps.mysimpletweets.util.MySimpleTweetsConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message implements Comparable<Message> {

    long id;

    Date createdAt;

    User recipient;

    User sender;

    String text;

    public static List<Message> fromJSONArray(JSONArray response) {
        List<Message> list = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            try {
                Message msg = Message.fromJSON(response.getJSONObject(i));
                if (msg != null) {
                    list.add(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    public static Message fromJSON(JSONObject json) {
        Message msg = new Message();

        try {
            msg.id = json.getLong("id");
            msg.createdAt = MySimpleTweetsConstants.inputDateFormat.parse(json.getString("created_at"));
            msg.recipient = User.fromJSON(json.optJSONObject("recipient"));
            msg.sender = User.fromJSON(json.optJSONObject("sender"));
            msg.text = json.getString("text");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msg;
    }

    public long getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public User getRecipient() {
        return recipient;
    }

    public User getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    @Override
    public int compareTo(Message another) {
        //We want to list in ascending order, just like any other messaging list.
        return createdAt.compareTo(another.createdAt);
    }
}
