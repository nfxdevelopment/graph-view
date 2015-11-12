package com.nfx.android.library.activityhelper;

/**
 * NFX Development
 * Created by nick on 11/11/15.
 * <p/>
 * A helper class to simplify permissions so strings are not miss typed etc.
 */
public class Permission {

    private static final int PERMISSION_OFFSET = 1000;
    public static final int MINIMUM_PERMISSION = PERMISSION_OFFSET - 1;
    public static final int READ_CALENDAR = PERMISSION_OFFSET;
    public static final int WRITE_CALENDAR = PERMISSION_OFFSET + 1;
    public static final int CAMERA = PERMISSION_OFFSET + 2;
    public static final int READ_CONTACTS = PERMISSION_OFFSET + 3;
    public static final int WRITE_CONTACTS = PERMISSION_OFFSET + 4;
    public static final int READ_PROFILE = PERMISSION_OFFSET + 5;
    public static final int WRITE_PROFILE = PERMISSION_OFFSET + 6;
    public static final int ACCESS_FINE_LOCATION = PERMISSION_OFFSET + 7;
    public static final int ACCESS_COARSE_LOCATION = PERMISSION_OFFSET + 8;
    public static final int RECORD_AUDIO = PERMISSION_OFFSET + 9;
    public static final int READ_PHONE_STATE = PERMISSION_OFFSET + 10;
    public static final int CALL_PHONE = PERMISSION_OFFSET + 11;
    public static final int READ_CALL_LOG = PERMISSION_OFFSET + 12;
    public static final int WRITE_CALL_LOG = PERMISSION_OFFSET + 13;
    public static final int ADD_VOICEMAIL = PERMISSION_OFFSET + 14;
    public static final int USE_SIP = PERMISSION_OFFSET + 15;
    public static final int PROCESS_OUTGOING_CALLS = PERMISSION_OFFSET + 16;
    public static final int BODY_SENSORS = PERMISSION_OFFSET + 17;
    public static final int USE_FINGERPRINT = PERMISSION_OFFSET + 18;
    public static final int SEND_SMS = PERMISSION_OFFSET + 19;
    public static final int RECEIVE_SMS = PERMISSION_OFFSET + 20;
    public static final int READ_SMS = PERMISSION_OFFSET + 21;
    public static final int RECEIVE_WAP_PUSH = PERMISSION_OFFSET + 22;
    public static final int RECEIVE_MMS = PERMISSION_OFFSET + 23;
    public static final int READ_CELL_BROADCASTS = PERMISSION_OFFSET + 24;
    public static final int READ_EXTERNAL_STORAGE = PERMISSION_OFFSET + 25;
    public static final int WRITE_EXTERNAL_STORAGE = PERMISSION_OFFSET + 26;
    public static final int MAXIMUM_PERMISSION = PERMISSION_OFFSET + 27;
    private static final String[] sPermissionsString = {
            "android.permission.READ_CALENDAR",
            "android.permission.WRITE_CALENDAR",
            "android.permission.CAMERA",
            "android.permission.READ_CONTACTS",
            "android.permission.WRITE_CONTACTS",
            "android.permission.READ_PROFILE",
            "android.permission.WRITE_PROFILE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.RECORD_AUDIO",
            "android.permission.READ_PHONE_STATE",
            "android.permission.CALL_PHONE",
            "android.permission.READ_CALL_LOG",
            "android.permission.WRITE_CALL_LOG",
            "com.android.voicemail.permission.ADD_VOICEMAIL",
            "android.permission.USE_SIP",
            "android.permission.PROCESS_OUTGOING_CALLS",
            "android.permission.BODY_SENSORS",
            "android.permission.USE_FINGERPRINT",
            "android.permission.SEND_SMS",
            "android.permission.RECEIVE_SMS",
            "android.permission.READ_SMS",
            "android.permission.RECEIVE_WAP_PUSH",
            "android.permission.RECEIVE_MMS",
            "android.permission.READ_CELL_BROADCASTS",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };
    /**
     * The set permission
     */
    private int mPermission;

    /**
     * Constructor
     *
     * @param permission permission in concern, use static ints within this class
     */
    public Permission(int permission) {
        mPermission = permission;
    }

    /**
     * Constructor
     *
     * @param permission permission in concern
     */
    public Permission(String permission) {
        int i;
        for (i = 0; i < sPermissionsString.length; ++i) {
            if (permission.equals(sPermissionsString[i])) {
                mPermission = i;
                break;
            }
        }
        if (i == sPermissionsString.length) {
            throw new RuntimeException(
                    "Permission string not found, see com.nfx.android.actvityhelper.Permission");
        }
    }

    /**
     * Get permission set within the object
     *
     * @return int of the permission
     */
    public int getPermission() {
        return mPermission;
    }

    /**
     * Get permission set within the object
     *
     * @return string of the permission
     */
    public String toString() {
        return sPermissionsString[mPermission - PERMISSION_OFFSET];
    }
}
