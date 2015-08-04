package com.inscripts.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferenceHelper {
	private static SharedPreferences preferences;
	private static SharedPreferences.Editor editor;

	public static void initialize(Context con) {
		if (null == preferences) {
			preferences = PreferenceManager.getDefaultSharedPreferences(con);
		}
		if (null == editor) {
			editor = preferences.edit();
		}
	}

	public static void save(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public static void save(String key, Integer value) {
		save(key, String.valueOf(value));
	}

	public static void save(String key, Long value) {
		save(key, String.valueOf(value));
	}

	public static String get(String key) {
		return preferences.getString(key, null);
	}

	public static Boolean contains(String key) {
		return preferences.contains(key);
	}

	public static void removeKey(String key) {
		editor.remove(key);
		editor.commit();
	}
}
