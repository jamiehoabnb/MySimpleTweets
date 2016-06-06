package com.codepath.apps.mysimpletweets.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class MySimpleTweetsConstants {

    public static final double PROFILE_PHOTO_WIDTH_FACTOR = 0.15;
    public static final double PROFILE_LARGE_PHOTO_WIDTH_FACTOR = 0.20;
    public static final String BANNER_SIZE = "600x200";
    public static final int ROUNDED_CORNER_CONST = 10;

    public static final SimpleDateFormat inputDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss ZZZZZ yyyy", Locale.ENGLISH);
    static {
        inputDateFormat.setLenient(true);
    }

    public static final SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd MMM yy");

    public static final SimpleDateFormat longDateOutputFormat = new SimpleDateFormat("hh:mm aa dd MMM yy");
}
