package com.codepath.apps.mysimpletweets.storage;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.codepath.apps.mysimpletweets.models.Tweet;

import java.util.List;

public class DBWriteAsyncTask extends AsyncTask<List<Tweet>, Void, Void> {
    private ProgressBar progressBar;

    public static DBWriteAsyncTask newInstance(ProgressBar progressBar) {
        DBWriteAsyncTask task = new DBWriteAsyncTask();
        task.progressBar = progressBar;
        return task;
    }

    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected Void doInBackground(List<Tweet>... tweets) {
        for (List<Tweet> tweetList: tweets) {
            for (Tweet tweet: tweetList) {
                tweet.getUser().save();
                tweet.save();
            }
        }
        return null;
    }

    protected void onProgressUpdate() {
    }

    protected void onPostExecute() {
        progressBar.setVisibility(View.INVISIBLE);
    }
}
