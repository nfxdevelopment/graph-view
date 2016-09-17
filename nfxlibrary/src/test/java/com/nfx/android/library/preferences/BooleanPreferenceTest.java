package com.nfx.android.library.preferences;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import com.nfx.android.library.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * NFX Development
 * Created by nick on 15/09/16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.LOLLIPOP, constants = BuildConfig.class)
public class BooleanPreferenceTest {

    SharedPreferences mSharedPreferences;
    BooleanPreference mBooleanPreference;

    String mTestKey = "test_key";
    Boolean mDefaultValue = false;

    @Before
    public void setUp() throws Exception {
        // First set up the shared prefs
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment
                .application.getApplicationContext());
        mBooleanPreference = new BooleanPreference(mSharedPreferences, mTestKey, mDefaultValue);
    }

    @Test
    public void testGet() throws Exception {
        assertThat("Should pass back the default value as nothing as been set",
                mBooleanPreference.get(), is(mDefaultValue));

    }

    @Test
    public void testIsSet() throws Exception {
        assertThat("Should return false due to preference no being set",
                mBooleanPreference.isSet(), is(false));
        mBooleanPreference.set(true);
        assertThat("Should return true as preference has just been set",
                mBooleanPreference.isSet(), is(true));
    }

    @Test
    public void testSet() throws Exception {
        mBooleanPreference.set(true);
        assertThat("Should pass back the value just written", mBooleanPreference.get(), is(true));
        mBooleanPreference.set(false);
        assertThat("Should pass back the value just written", mBooleanPreference.get(), is(false));
    }

    @Test
    public void testDelete() throws Exception {
        mBooleanPreference.set(true);
        assertThat("Should return true as preference has just been set",
                mBooleanPreference.isSet(), is(true));
        mBooleanPreference.delete();
        assertThat("Should return false as preference has just been deleted",
                mBooleanPreference.isSet(), is(false));

    }
}