package com.example.musicplayercontrol;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtility {
    private static PreferencesUtility sInstance;
    private final SharedPreferences mPreferences;

    private PreferencesUtility(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferencesUtility getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    public boolean isFirstTime() {
        return mPreferences.getBoolean("first_time", true);
    }

    public void setFirstTime(boolean firstTime) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("first_time", firstTime);
        editor.apply();
    }

    // 播放器状态
    public void setPlayerState(boolean isExpanded) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean("player_expanded", isExpanded);
        editor.apply();
    }

    public boolean getPlayerState() {
        return mPreferences.getBoolean("player_expanded", false);
    }

    // 播放模式
    public void setPlayMode(int mode) {
        final SharedPreferences.Editor editor = mPreferences.edit();
        editor.putInt("play_mode", mode);
        editor.apply();
    }

    public int getPlayMode() {
        return mPreferences.getInt("play_mode", 0);
    }
}