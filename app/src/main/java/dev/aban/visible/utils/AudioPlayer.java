package dev.aban.visible.utils;

import android.media.MediaPlayer;

public class AudioPlayer {

    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(int rid) {
        stop();

        String canPlay = Helper.loadSetting(Constants._TABLE_USER, Constants._KEY_BUBBLE_SOUND, "true");
        if (canPlay.equals("true")) {
            mMediaPlayer = MediaPlayer.create(ContextHelper.retrieveContext(), rid);
            mMediaPlayer.setVolume(0.1f, 0.1f);
            mMediaPlayer.setOnCompletionListener(mediaPlayer -> stop());
            mMediaPlayer.start();
        }
    }
}
