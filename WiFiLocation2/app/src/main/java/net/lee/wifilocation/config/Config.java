package net.lee.wifilocation.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lenovo on 2015/4/19.
 */
public class Config {

    public static final String APP_ID = "net.lee.wifilocation";
    public static final String CHARSET = "utf-8";

    public static final String KEY_CLOUD_CODE_NAME = "test";
    public static final String KEY_REQUEST_BODY_NAME = "name";
    public static final String KEY_REQUEST_BODY_TOKEN = "token";
    public static final String KEY_REQUEST_BODY_INFORMATION = "information";
    public static final String KEY_REQUEST_BODY_USERNAME = "userName";
    public static final String KEY_REQUEST_BODY_PASSWORD = "password";
    public static final String KEY_REQUEST_BODY_CONNECTPASSWORD = "connectPassword";


    public static final String KEY_AREA_NAME = "areaName";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_STATUS = "status";
    public static final String KEY_GET_LOCATION = "locationName";
    public static final String KEY_ACCESS_FOR_DEVICE = "getAccessForDevice";
    public static final String KEY_GET_DEVICES_NAME = "getAllDevicesName";
    public static final String KEY_DEVICE_NAME = "deviceName";
    public static final String KEY_LOCATION_STATUS = "locationStatus"
            ;
    public static final String ACTION_GET_AREA_NAME = "getAreaName";
    public static final String ACTION_GET_LOCATION = "getMyLocation";
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_VERIFY_TOKEN = "verifyToken";
    public static final String ACTION_GET_DEVICE_NAME = "getDeviceName";
    public static final String ACTION_VERIFY_CONNECTPASSWORD = "verifyConnectPassword";
    public static final String ACTION_GET_DEVICE_LOCATION = "getDeviceLocation";

    public static final int RESULT_STATUS_FAIL = 0;
    public static final int RESULT_STATUS_SUCCESS = 1;
    public static final int RESULT_INVALID_TOKEN = 2;
    public static final int VALUE_GET_AREA_NAME = 0;
    public static final int VALUE_GET_DEVICES_NAME = 0;

    public static String valueManageSelectedAreaName = null;
    public static String valueSelectedAreaName = null;
    public static String valueAllAreaName = null;
    public static boolean valueAreaChanged = false;
    public static String valueAllDevicesName  = null;

    /**
     * Use the token way to login the activity
     * This function is used to save the token from server into the database
     * File name : APP_ID
     * Column name : KEY_TOKEN
     * @param context
     * @param token
     */
    public static void cacheToken(Context context,String token)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(Config.KEY_TOKEN, token);
        editor.apply();
    }

    /**
     * Get the token in the database
     * File name : APP_ID
     * Column name : KEY_TOKEN
     * @param context
     * @return
     */
    public static String getCacheToken(Context context)
    {
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_TOKEN,null);
    }

    /**
     * Save the username into database
     * Column name : KEY_REQUEST_BODY_USERNAME
     * @param context
     * @param userName
     */
    public static void cacheUserName(Context context,String userName)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_REQUEST_BODY_USERNAME,userName);
        editor.apply();
    }

    /**
     * Get the username from database
     * @param context
     * @return
     */
    public static String getCacheUserName(Context context)
    {
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_REQUEST_BODY_USERNAME,null);
    }

    /**
     * Save the password into the database
     * Column name : KEY_REQUEST_BODY_PASSWORD
     * @param context
     * @param password
     */
    public static void cachePassword(Context context,String password)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_REQUEST_BODY_PASSWORD,password);
        editor.apply();
    }

    /**
     * Get the password from the database
     * @param context
     * @return
     */
    public static String getCachePassword(Context context)
    {
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_REQUEST_BODY_PASSWORD,null);
    }




}
