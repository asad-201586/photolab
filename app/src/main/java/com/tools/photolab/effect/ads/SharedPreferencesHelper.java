package com.tools.photolab.effect.ads;

/* this class is for save or retrieving data in/from shared preference */

import android.content.Context;
import android.content.SharedPreferences;

import com.tools.photolab.effect.MainApplication;

public class SharedPreferencesHelper {

    private static SharedPreferencesHelper instance;

    private SharedPreferences settings;

    private SharedPreferences.Editor editor;

    private SharedPreferencesHelper() {

        settings = MainApplication.getContext().getSharedPreferences("PhotoLab", 0);
        editor = settings.edit();
    }
    public static SharedPreferencesHelper getInstance() {

        if (instance == null)
            instance = new SharedPreferencesHelper();
        return instance;
    }

    public static SharedPreferencesHelper getInstance(Context context) {
        if (instance == null)
            instance = new SharedPreferencesHelper();
        return instance;
    }

    public String getString(String key, String defValue) {

        return settings.getString(key, defValue);
    }
    public SharedPreferencesHelper setString(String key, String value) {

        editor.putString(key, value);
        editor.commit();

        return this;
    }

    public SharedPreferencesHelper setStatus(String key, boolean value) {

        editor.putBoolean(key, value);
        editor.commit();

        return this;
    }

    public int getInt(String key, int defValue) {

        return settings.getInt(key, defValue);
    }

    public SharedPreferencesHelper setInt(String key, int value) {

        editor.putInt(key, value);
        editor.commit();

        return this;
    }

    public boolean getBoolean(String key, boolean defValue) {

        return settings.getBoolean(key, defValue);
    }

    public SharedPreferencesHelper setBoolean(String key, boolean value) {

        editor.putBoolean(key, value);
        editor.commit();

        return this;
    }

    public SharedPreferencesHelper setLong(String key, long value) {

        editor.putLong(key, value);
        editor.commit();

        return this;
    }

    public long getLong(String key, long defValue) {

        return settings.getLong(key, defValue);
    }

    public void clearData() {

        editor.clear();
        editor.commit();
    }
}
