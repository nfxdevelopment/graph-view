package com.nfx.android.library.preferences;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * NFX Development
 * Created by nick on 19/09/16.
 * <p/>
 * A wrapper around a integer preference for ease of access
 */
public class IntegerPreference {
    /**
     * The shared preference instance to store the integer in
     */
    private final SharedPreferences preferences;
    /**
     * The key which the preference is stored under
     */
    private final String key;
    /**
     * The default value for the preference
     */
    private final int defaultValue;

    /**
     * @param preferences  where to save the preference
     * @param key          key to the preference
     * @param defaultValue default value for the preference
     */
    public IntegerPreference(@NonNull SharedPreferences preferences,
                             @NonNull String key,
                             int defaultValue) {
        this.preferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    /**
     * Get the preference
     *
     * @return current value stored in preferences or default value if no instance is stored
     */
    public int get() {
        return preferences.getInt(key, defaultValue);
    }

    /**
     * Has the preference been set previously
     * @return true if the preference is stored, false if there is no instance
     */
    public boolean isSet() {
        return preferences.contains(key);
    }

    /**
     * Set the value of the preference
     * @param value value to set
     */
    public void set(int value) {
        preferences.edit().putInt(key, value).apply();
    }

    /**
     * Remove instance of preference from preferences
     */
    public void delete() {
        preferences.edit().remove(key).apply();
    }
}