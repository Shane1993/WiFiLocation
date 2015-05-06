package net.lee.wifilocation.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2015/4/30.
 * This class is used to document the location of device
 */
public class DeviceLocation extends BmobObject {

    private String userName;
    private String areaName;
    private String locationName;
    private String time;

    public DeviceLocation(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
