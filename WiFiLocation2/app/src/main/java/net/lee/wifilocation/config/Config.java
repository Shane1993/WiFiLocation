package net.lee.wifilocation.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lenovo on 2015/4/19.
 */
public class Config {

    public static final String APP_ID = "net.lee.wifilocation";
    public static final String CHARSET = "utf-8";

    //The values with there keys are send to the server
    public static final String KEY_CLOUD_CODE_NAME = "test";
    public static final String KEY_REQUEST_BODY_NAME = "name";
    public static final String KEY_REQUEST_BODY_TOKEN = "token";
    public static final String KEY_REQUEST_BODY_INFORMATION = "information";
    public static final String KEY_REQUEST_BODY_USERNAME = "userName";
    public static final String KEY_REQUEST_BODY_PASSWORD = "password";
    public static final String KEY_REQUEST_BODY_CONNECTPASSWORD = "connectPassword";
    public static final String KEY_REQUEST_BODY_NEWLOGINPASSWORD = "newLoginPassword";
    public static final String KEY_REQUEST_BODY_NEWCONNECTPASSWORD = "newConnectPassword";
    public static final String KEY_REQUEST_BODY_MYDEVICES = "myDevices";
    public static final String KEY_REQUEST_BODY_INFONUMBER = "infoNumber";
    public static final String KEY_REQUEST_BODU_AREA_NAME = "areaName";

    //These actions are the action commands send to the server
    public static final String ACTION_GET_AREA_NAME = "getAreaName";
    public static final String ACTION_GET_MY_LOCATION = "getMyLocation";
    public static final String ACTION_LOGIN = "login";
    public static final String ACTION_VERIFY_TOKEN = "verifyToken";
    public static final String ACTION_GET_DEVICE_NAME = "getDeviceName";
    public static final String ACTION_VERIFY_CONNECTPASSWORD = "verifyConnectPassword";
    public static final String ACTION_RESET_LOGIN_PASSWORD = "resetLoginPassword";
    public static final String ACTION_RESET_Connect_PASSWORD = "resetConnectPassword";
    public static final String ACTION_GET_CONNECT_PASSWORD = "getConnectPassword";
    public static final String ACTION_GET_MY_DEVICES = "getMyDevices";
    public static final String ACTION_REFRESH_MY_DEVEICES = "refreshMyDevices";
    public static final String ACTION_GET_DEVICE_LOCATION = "getDeviceLocation";
    public static final String ACTION_GET_OBJECT_ID = "getObjectId";

    //These keys are used to get the values send by server
    public static final String KEY_AREA_NAME = "areaName";
    public static final String KEY_TOKEN = "token";
    public static final String KEY_STATUS = "status";
//    public static final String KEY_GET_LOCATION = "locationName";
//    public static final String KEY_ACCESS_FOR_DEVICE = "getAccessForDevice";
//    public static final String KEY_GET_DEVICES_NAME = "getAllDevicesName";
    public static final String KEY_DEVICE_NAME = "deviceName";
    public static final String KEY_LOCATION_STATUS = "locationStatus";
    public static final String KEY_ALL_DEVICES_NAME = "allDevicesName";
    public static final String KEY_MY_DEVICES = "myDevices";
    public static final String KEY_DEVICE_LOCATION = "deviceLocation";
    public static final String KEY_LOCATION_NAME = "locationName";
    public static final String KEY_TIME = "time";
    public static final String KEY_OBJECT_ID = "objectId";

    public static final String KEY_MANAGE_PASSWORD = "managePassword";
    public static final String VALUE_MANAGE_PASSWORD = "234567";


    public static final int RESULT_STATUS_FAIL = 0;
    public static final int RESULT_STATUS_SUCCESS = 1;
    public static final int RESULT_INVALID_TOKEN = 2;
//    public static final int VALUE_GET_AREA_NAME = 0;
//    public static final int VALUE_GET_DEVICES_NAME = 0;

    public static String valueManageSelectedAreaName = null;
    public static String valueSelectedAreaName = null;
    public static String valueAllAreaName = null;
    public static boolean valueAreaChanged = false;
    public static boolean valueMyDevicesChange = true;
//    public static String valueAllDevicesName  = null;

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

    /**
     * Save the conenct password into the database
     * Column name : KEY_REQUEST_BODY_CONNECTPASSWORD
     * @param context
     * @param password
     */
    public static void cacheConnectPassword(Context context,String password)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_REQUEST_BODY_CONNECTPASSWORD,password);
        editor.apply();
    }

    /**
     * Get the password from the database
     * @param context
     * @return
     */
    public static String getCacheConnectPassword(Context context)
    {
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_REQUEST_BODY_CONNECTPASSWORD,null);
    }

    /**
     * Save the devices name into the database
     * Column name : KEY_ALL_DEVICES_NAME
     * @param context
     * @param allDevicesName
     */
    public static void cacheAllDeivecsName(Context context,String allDevicesName)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_ALL_DEVICES_NAME,allDevicesName);
        editor.apply();
    }

    /**
     * Get the devices name from the database
     * @param context
     * @return
     */
    public static String getCacheAllDeivecsName(Context context)
    {
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_ALL_DEVICES_NAME,null);
    }

    /**
     * Save the manage password into the database
     * Column name : KEY_MANAGE_PASSWORD
     * @param context
     * @param managePassword
     */
    public static void cacheManagePassword(Context context,String managePassword)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).edit();
        editor.putString(KEY_MANAGE_PASSWORD,managePassword);
        editor.apply();
    }

    /**
     * Get the manage password from the database
     * @param context
     * @return
     */
    public static String getCacheManagePassword(Context context)
    {
        return context.getSharedPreferences(APP_ID,Context.MODE_PRIVATE).getString(KEY_MANAGE_PASSWORD,null);
    }


}
