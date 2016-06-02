package com.codepath.apps.mysimpletweets.util;

import android.view.MenuItem;

public class ListProgressBar {

    private static MenuItem actionProgressItem;

    public static void setInstance(MenuItem actionProgressItem) {
        ListProgressBar.actionProgressItem = actionProgressItem;
    }


    public static void showProgressBar() {
        // Show progress item
        if (actionProgressItem != null) {
            actionProgressItem.setVisible(true);
        }
    }

    public static void hideProgressBar() {
        // Hide progress item
        if (actionProgressItem != null) {
            actionProgressItem.setVisible(false);
        }
    }
}
