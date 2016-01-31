package com.nfx.android.library.activityhelper;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * NFX Development
 * Created by nick on 11/11/15.
 * <p/>
 * This class will take permissions and ask the user at startup if they accept to usage. When the
 * user has accepted or denied they will not be asked again until requested to do so.
 */
public abstract class NfxActivity extends AppCompatActivity {

    /**
     * Shared Preferences placeholder
     */
    private static final String PREFS_NAME = "NfxActivityPermissions";

    /**
     * unique code for permission request
     */
    private static final int sPreferenceRequestCode = 200;
    /**
     * A Map describing whether the permission is allowed by the user
     */
    private final Map<String, Boolean> mPermissionsGranted = new HashMap<>();
    /**
     * A Map of all permissions the developer wants
     */
    private final Map<String, Permission> mPermissionsRequested = new HashMap<>();
    /**
     * Shared preferences where information of which permissions have been asked for
     */
    private SharedPreferences mSharedPreferences;

    /**
     * The Constructor that takes in the requested permissions and stores them for later use in
     * onCreate
     *
     * @param permissionsRequested static integer found in {@code Permission}
     */
    protected NfxActivity(int[] permissionsRequested) {
        super();

        for (int permissionRequested : permissionsRequested) {
            if (permissionRequested > Permission.MINIMUM_PERMISSION &&
                    permissionRequested < Permission.MAXIMUM_PERMISSION) {
                Permission permission = new Permission(permissionRequested);
                mPermissionsRequested.put(permission.toString(), permission);
            } else {
                throw new RuntimeException("Permission " + permissionRequested +
                        " is unknown, see com.nfx.android.library.activityhelper.Permission");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSharedPreferences = getSharedPreferences(PREFS_NAME, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String> permissionsToAskFor = new ArrayList<>();

        // If we already have permission or we've already asked. Remove it from ask list
        for (Permission permission : mPermissionsRequested.values()) {
            mPermissionsGranted.put(permission.toString(), hasPermission(permission));
            if (!hasPermission(permission) && shouldWeAsk(permission)) {
                permissionsToAskFor.add(permission.toString());
            }
        }

        if (permissionsToAskFor.size() > 0) {
            askForPermission(permissionsToAskFor.toArray(new String[permissionsToAskFor.size()]));
        } else {
            permissionRequestComplete(mPermissionsGranted);
        }
    }

    /**
     * Ask for all the permissions in the array
     *
     * @param permissionsToAskFor an array with all permissions to ask for
     */
    private void askForPermission(String[] permissionsToAskFor) {
        if (isMarshmallowOrAbove()) {
            requestPermissions(permissionsToAskFor, sPreferenceRequestCode);
        }
    }

    /**
     * This will clear the shared preferences flags so all permissions will be attempted again if
     * denied previously.
     */
    @SuppressWarnings("unused")
    public void clearUserAlreadyAskedFlags() {
        for (Permission permission : mPermissionsRequested.values()) {
            mSharedPreferences.edit().putBoolean(permission.toString(), true).apply();
        }
    }

    /**
     * Check shared preferences to see whether to ask for the permission
     *
     * @param permission permission to check
     * @return true if it has not been asked before, false if user has already been asked
     */
    private boolean shouldWeAsk(Permission permission) {
        return (mSharedPreferences.getBoolean(permission.toString(), true));
    }

    /**
     * Set a flag in shared preferences indicating the user has responded to permission request
     *
     * @param permission permission to check
     */
    private void markAsAsked(String permission) {
        mSharedPreferences.edit().putBoolean(permission, false).apply();
    }

    /**
     * Is the permission allowed already
     *
     * @param permission permission to check
     * @return true if allow, false if denied
     */
    private boolean hasPermission(Permission permission) {
        return !isMarshmallowOrAbove() ||
                (checkSelfPermission(permission.toString()) == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (permsRequestCode) {
            case 200:
                for (int i = 0; i < permissions.length; ++i) {
                    markAsAsked(permissions[i]);
                    mPermissionsGranted.put(permissions[i],
                            (grantResults[i] == PackageManager.PERMISSION_GRANTED));
                }

                permissionRequestComplete(mPermissionsGranted);
                break;
        }
    }

    /**
     * Check whether we are greater that 6.0 android release
     *
     * @return true if 6.0 release or greater
     */
    private boolean isMarshmallowOrAbove() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /**
     * A callback to inform the child that we have finished with the permission requests
     *
     * @param permissions a map of the requested permissions and whether they are allowed or denied
     */
    protected abstract void permissionRequestComplete(Map<String, Boolean> permissions);
}
