package com.example.samurai;

import android.content.Context;
import android.media.MediaPlayer;

public class MusicManager {
    private static MediaPlayer mediaPlayer;

    public static void play(Context context, int resourceId) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, resourceId);
            mediaPlayer.setLooping(true);
        }
        mediaPlayer.start();
    }

    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void resume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public static void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}