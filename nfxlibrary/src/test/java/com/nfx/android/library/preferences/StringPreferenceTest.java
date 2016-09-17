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
public class StringPreferenceTest {

    SharedPreferences mSharedPreferences;
    StringPreference mStringPreference;

    String mTestKey = "test_key";
    String mDefaultValue = "DEFAULT_VALUE";

    @Before
    public void setUp() throws Exception {
        // First set up the shared prefs
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment
                .application.getApplicationContext());
        mStringPreference = new StringPreference(mSharedPreferences, mTestKey, mDefaultValue);
    }

    @Test
    public void testGet() throws Exception {
        assertThat("Should pass back the default value as nothing as been set",
                mStringPreference.get(), is(mDefaultValue));

    }

    @Test
    public void testIsSet() throws Exception {
        assertThat("Should return false due to preference no being set",
                mStringPreference.isSet(), is(false));
        mStringPreference.set("TestIsSet");
        assertThat("Should return true as preference has just been set",
                mStringPreference.isSet(), is(true));
    }

    @Test
    public void testSet() throws Exception {
        String testString = "TestSet";
        mStringPreference.set(testString);
        assertThat("Should pass back the value just written", mStringPreference.get(), is
                (testString));
    }

    @Test
    public void testDelete() throws Exception {
        mStringPreference.set("TestDelete");
        assertThat("Should return true as preference has just been set",
                mStringPreference.isSet(), is(true));
        mStringPreference.delete();
        assertThat("Should return false as preference has just been deleted",
                mStringPreference.isSet(), is(false));

    }
}