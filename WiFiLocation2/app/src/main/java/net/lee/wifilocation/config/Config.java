package net.lee.wifilocation.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by lenovo on 2015/4/19.
 */
public class Config {

//    public static final String APP_ID = "net.lee.wifilocation";
//    public static final String CHARSET = "utf-8";

    public static final String KEY_AREA_NAME = "areaName";
    public static final String KEY_GET_AREA_NAME = "getAreaName";
    public static final String KEY_GET_LOCATION = "getMyLocation";
    public static final int VALUE_GET_AREA_NAME = 0;


    public static String valueManageSelectedAreaName = null;
    public static String valueSelectedAreaName = null;
    public static String valueAllAreaName = null;
    public static boolean valueAreaChanged = false;


}
