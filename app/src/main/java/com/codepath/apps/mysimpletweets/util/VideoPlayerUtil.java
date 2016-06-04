package com.codepath.apps.mysimpletweets.util;

import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.utils.Logger;

/**
 * Only the most visible video is playing at any time.  That's why there is a single VideoPlayerManager.
 */
public class VideoPlayerUtil {

    private static final VideoPlayerManager<MetaData> videoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {
            Logger.d("DEBUG", "onPlayerItemChanged " + metaData);

        }
    });

    public static VideoPlayerManager<MetaData> getVideoPlayerManager() {
        return videoPlayerManager;
    }
}
