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
public class LongPreferenceTest {

    SharedPreferences mSharedPreferences;
    LongPreference mLongPreference;

    String mTestKey = "test_key";
    long mDefaultValue = (long) (Math.random() * Long.MAX_VALUE);

    @Before
    public void setUp() throws Exception {
        // First set up the shared prefs
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment
                .application.getApplicationContext());
        mLongPreference = new LongPreference(mSharedPreferences, mTestKey, mDefaultValue);
    }

    @Test
    public void testGet() throws Exception {
        assertThat("Should pass back the default value as nothing as been set",
                mLongPreference.get(), is(mDefaultValue));

    }

    @Test
    public void testIsSet() throws Exception {
        assertThat("Should return false due to preference no being set",
                mLongPreference.isSet(), is(false));
        mLongPreference.set(0);
        assertThat("Should return true as preference has just been set",
                mLongPreference.isSet(), is(true));
    }

    @Test
    public void testSet() throws Exception {
        long randomNumber = (long) (Math.random() * Long.MAX_VALUE);
        mLongPreference.set(randomNumber);
        assertThat("Should pass back the value just written", mLongPreference.get(), is
                (randomNumber));
    }

    @Test
    public void testDelete() throws Exception {
        mLongPreference.set(0);
        assertThat("Should return true as preference has just been set",
                mLongPreference.isSet(), is(true));
        mLongPreference.delete();
        assertThat("Should return false as preference has just been deleted",
                mLongPreference.isSet(), is(false));

    }
}