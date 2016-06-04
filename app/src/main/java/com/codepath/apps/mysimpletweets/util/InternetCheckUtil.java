package com.codepath.apps.mysimpletweets.util;

import java.io.IOException;

public class InternetCheckUtil {

    //Source: http://guides.codepath.com/android/Sending-and-Managing-Network-Requests#checking-for-network-connectivity
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }
}
