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
public class IntegerPreferenceTest {

    SharedPreferences mSharedPreferences;
    IntegerPreference mIntegerPreference;

    String mTestKey = "test_key";
    int mDefaultValue = (int) (Math.random() * Integer.MAX_VALUE);

    @Before
    public void setUp() throws Exception {
        // First set up the shared prefs
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment
                .application.getApplicationContext());
        mIntegerPreference = new IntegerPreference(mSharedPreferences, mTestKey, mDefaultValue);
    }

    @Test
    public void testGet() throws Exception {
        assertThat("Should pass back the default value as nothing as been set",
                mIntegerPreference.get(), is(mDefaultValue));

    }

    @Test
    public void testIsSet() throws Exception {
        assertThat("Should return false due to preference no being set",
                mIntegerPreference.isSet(), is(false));
        mIntegerPreference.set(0);
        assertThat("Should return true as preference has just been set",
                mIntegerPreference.isSet(), is(true));
    }

    @Test
    public void testSet() throws Exception {
        int randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
        mIntegerPreference.set(randomNumber);
        assertThat("Should pass back the value just written", mIntegerPreference.get(), is
                (randomNumber));
    }

    @Test
    public void testDelete() throws Exception {
        mIntegerPreference.set(0);
        assertThat("Should return true as preference has just been set",
                mIntegerPreference.isSet(), is(true));
        mIntegerPreference.delete();
        assertThat("Should return false as preference has just been deleted",
                mIntegerPreference.isSet(), is(false));

    }
}