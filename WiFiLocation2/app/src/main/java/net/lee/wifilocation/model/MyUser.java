package net.lee.wifilocation.model;

import cn.bmob.v3.BmobUser;

/**
 * Created by lenovo on 2015/4/29.
 */
public class MyUser extends BmobUser {

    private String newLoginPassword;
    private int connectPassword;
    private String myDevices;

    public String getNewLoginPassword() {
        return newLoginPassword;
    }

    public void setNewLoginPassword(String newLoginPassword) {
        this.newLoginPassword = newLoginPassword;
    }

    public int getConnectPassword() {
        return connectPassword;
    }

    public void setConnectPassword(int connectPassword) {
        this.connectPassword = connectPassword;
    }

    public String getMyDevices() {
        return myDevices;
    }

    public void setMyDevices(String myDevices) {
        this.myDevices = myDevices;
    }
}
