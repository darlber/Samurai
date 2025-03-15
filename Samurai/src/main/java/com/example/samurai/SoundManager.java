package com.example.samurai;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager {
    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundMap;

    public static void init(Context context) {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        soundMap = new HashMap<>();
        soundMap.put(R.raw.attack_sound, soundPool.load(context, R.raw.attack_sound, 1));
        soundMap.put(R.raw.hurt_sound, soundPool.load(context, R.raw.hurt_sound, 1));
        soundMap.put(R.raw.dash_sound, soundPool.load(context, R.raw.dash_sound, 1));
        // Agrega más sonidos según sea necesario
    }

    public static void playSound(int soundId) {
        Integer sound = soundMap.get(soundId);
        if (sound != null) {
            soundPool.play(sound, 1, 1, 1, 0, 1);
        }
    }

    public static void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}