package net.lee.wifilocation.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2015/4/18.
 */
public class LocationInfo extends BmobObject {

    private String userName = "null";
    private String locationName = "null";
    private String areaName = "null";
    private String mac1;
    private String mac2;
    private String mac3;
    private String macSort;
    private int mac1Rssi;
    private int mac2Rssi;
    private int mac3Rssi;

    public LocationInfo(){}

    public LocationInfo(String userName, String locationName, String areaName, String mac1, String mac2, String mac3, String macSort, int mac1Rssi, int mac2Rssi, int mac3Rssi) {
        this.userName = userName;
        this.locationName = locationName;
        this.areaName = areaName;
        this.mac1 = mac1;
        this.mac2 = mac2;
        this.mac3 = mac3;
        this.macSort = macSort;
        this.mac1Rssi = mac1Rssi;
        this.mac2Rssi = mac2Rssi;
        this.mac3Rssi = mac3Rssi;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getMac1() {
        return mac1;
    }

    public void setMac1(String mac1) {
        this.mac1 = mac1;
    }

    public String getMac2() {
        return mac2;
    }

    public void setMac2(String mac2) {
        this.mac2 = mac2;
    }

    public String getMac3() {
        return mac3;
    }

    public void setMac3(String mac3) {
        this.mac3 = mac3;
    }

    public String getMacSort() {
        return macSort;
    }

    public void setMacSort(String macSort) {
        this.macSort = macSort;
    }

    public int getMac1Rssi() {
        return mac1Rssi;
    }

    public void setMac1Rssi(int mac1Rssi) {
        this.mac1Rssi = mac1Rssi;
    }

    public int getMac2Rssi() {
        return mac2Rssi;
    }

    public void setMac2Rssi(int mac2Rssi) {
        this.mac2Rssi = mac2Rssi;
    }

    public int getMac3Rssi() {
        return mac3Rssi;
    }

    public void setMac3Rssi(int mac3Rssi) {
        this.mac3Rssi = mac3Rssi;
    }

    //将toString重写为返回一个JSON数据类型
    @Override
    public String toString() {
        return "{userName:" + userName + ",locationName:" + locationName + ",areaName:" + areaName
                + ",mac1:" + mac1 + ",mac1Rssi:" + mac1Rssi
                + ",mac2:" + mac2 + ",mac2Rssi:" + mac2Rssi
                + ",mac3:" + mac3 + ",mac3Rssi:" + mac3Rssi
                + ",macSort:" + macSort + "}";
    }

    public String toJSONString() {

        return "{\"userName\":" + userName + ",\"locationName\":\"" + locationName + "\",\"areaName\":\"" + areaName
                + "\",\"mac1\":\"" + mac1 + "\",\"mac1Rssi\":\"" + mac1Rssi
                + "\",\"mac2\":\"" + mac2 + "\",\"mac2Rssi\":\"" + mac2Rssi
                + "\",\"mac3\":\"" + mac3 + "\",\"mac3Rssi\":\"" + mac3Rssi
                + "\",\"macSort\":\"" + macSort + "\"}";
    }
}
