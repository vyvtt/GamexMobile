package com.gamex.utils;

public final class Constant {
    // drawer menu identify
    public static final int ITEM_HOME = 1;
    public static final int ITEM_EVENT = 2;
    public static final int ITEM_BOOKMARK = 3;
    public static final int ITEM_REWARD = 4;
    public static final int ITEM_HISTORY = 5;
    public static final int ITEM_EDIT_PROFILE = 6;
    public static final int ITEM_CHANGE_PASSWORD = 7;
    public static final int ITEM_LOGOUT = 8;

    // Shared Preference key
    public static final String PREF_HAS_LOGGED_IN = "HAS_LOGGED_IN";
    public static final String PREF_ACCESS_TOKEN = "ACCESS_TOKEN";
    public static final String PREF_FULLNAME = "FULLNAME";

    // API
    public static final String BASE_URL = "https://gamexwebapi.azurewebsites.net/";
//    public static final String BASE_URL = "http://192.168.100.59:44319/";
    public static final String API_TYPE_ONGOING = "ongoing";
    public static final String API_TYPE_UPCOMING = "upcoming";
    public static final String API_TYPE_NEAR = "near-you";
//    public static final String BASE_URL = "http://172.20.10.3/";

    // Validate error messages
    public static final String ERR_REQUIRED = "Required";
    public static final String ERR_LENGTH_6_12 = "Required 6-12 characters";
    public static final String ERR_AZ_CHARACTERS = "Contain alphabetic characters only";
    public static final String ERR_LENGTH_MIN_6 = "At least 6 characters";

    // Intent extra
    public static final String EXTRA_EX_NAME = "EXTRA_EX_NAME";
    public static final String EXTRA_EX_ID = "EXTRA_EX_ID";
    public static final String EXTRA_EX_IMG = "EXTRA_EX_IMG";
    public static final String EXTRA_SCAN_QR_EX_ID = "EXTRA_SCAN_QR_EX_ID";
}
