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

    // API
    public static final String BASE_URL = "https://gamexwebapi.azurewebsites.net/";
//    public static final String BASE_URL = "http://172.20.10.3/";

    public static final String API_EXHIBITION = "api/Exhibition";
    public static final String API_ACCOUNT_LOGIN = "api/account/login";
    public static final String API_ACCOUNT_GET_INFO = "api/account/{username}";

    // Validate error messages
    public static final String ERR_REQUIRED = "Required";
    public static final String ERR_LENGTH_6_12 = "Required 6-12 characters";
    public static final String ERR_AZ_CHARACTERS = "Contain alphabetic characters only";
    public static final String ERR_LENGTH_MIN_6 = "At least 6 characters";

    //
    public final static String TAG_MAIN = "[MainActivity] ***";
    public final static String TAG_LOGIN = "[LoginActivity] ***";
    public final static String TAG_REGISTER = "[RegisterActivity] ***";
    public final static String TAG_HOME = "[HomeFragment] ***";
    public final static String TAG_FG_EX_DETAIL = "[ExDetailFragment] ***";
}
