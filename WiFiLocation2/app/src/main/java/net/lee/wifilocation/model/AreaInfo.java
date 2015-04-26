package net.lee.wifilocation.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by lenovo on 2015/4/20.
 * This class is used for save the area's name you create
 */
public class AreaInfo extends BmobObject{

    private String areaName;

    public AreaInfo(){}

    public AreaInfo(String areaName)
    {
        this.areaName = areaName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}
