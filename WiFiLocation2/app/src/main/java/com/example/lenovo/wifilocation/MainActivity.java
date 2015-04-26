package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.app.Service;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.LocationInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.CloudCodeListener;


public class MainActivity extends Activity  {

    //Bmob应用ID
    private String Bmob_AppId = "1b31c55348aeab1a15e5263e668fb88a";

    //create the TabHost
    private TabHost tabHost;

    //create WifiManager
    public static WifiManager wifiManager;
    private String allAreaName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //This is about Bmob
        Bmob.initialize(this, Bmob_AppId);
        //Bind the service
        wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);

        //Set the TabHost
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("start").setIndicator("定位").setContent(R.id.firstLayoutId));
        tabHost.addTab(tabHost.newTabSpec("area").setIndicator("管理").setContent(R.id.secondLayoutId));
        tabHost.addTab(tabHost.newTabSpec("person").setIndicator("预留").setContent(R.id.thirdLayoutId));



    }

    public static LocationInfo getLocationInfo()
    {
        //Scan agian
        wifiManager.startScan();

        ScanResult[] scanResults = getAPSelectList(wifiManager.getScanResults());

        if(scanResults != null) {
            LocationInfo locationInfo = new LocationInfo();

            locationInfo.setMac1(scanResults[0].BSSID);
            locationInfo.setMac2(scanResults[1].BSSID);
            locationInfo.setMac3(scanResults[2].BSSID);
            locationInfo.setMac1Rssi(WifiManager.calculateSignalLevel(scanResults[0].level, 50));
            locationInfo.setMac2Rssi(WifiManager.calculateSignalLevel(scanResults[1].level, 50));
            locationInfo.setMac3Rssi(WifiManager.calculateSignalLevel(scanResults[2].level, 50));
            locationInfo.setMacSort(scanResults[0].BSSID + scanResults[1].BSSID + scanResults[2].BSSID);

            return locationInfo;
        }
        else
        {
            return null;
        }
    }

    /**
     *  Return the Access Point Array after sorting by RSSI
     *  Attention:This return Array will only include 3 APs with the most RSSI
     * @param list
     * @return Scanresult[] after sorting by RSSI and Mac
     */
    public static ScanResult[] getAPSelectList(List<ScanResult> list) {

        //The number of ap must be 3 or more
        if(list.size() >= 3) {
            ScanResult[] scanResults = new ScanResult[list.size()];
            ScanResult[] selectResults = new ScanResult[3];
            list.toArray(scanResults);
            ScanResult temp;
            //Sort the array by RSSI
            for (int i = 0; i < scanResults.length - 1; i++) {
                for (int j = i + 1; j < scanResults.length; j++) {
                    if (scanResults[i].level < scanResults[j].level) {
                        temp = scanResults[j];
                        scanResults[j] = scanResults[i];
                        scanResults[i] = temp;
                    }
                }
            }

            //Sort the array by Mac
            for(int i = 0; i < 2; i++)
            {
                for(int j = i+1;j < 3; j++)
                {
                    //如果对比结果大于0说明前面字符串较大
                    if(scanResults[i].BSSID.compareTo(scanResults[j].BSSID) > 0)
                    {
                        temp = scanResults[j];
                        scanResults[j] = scanResults[i];
                        scanResults[i] = temp;
                    }
                }
            }

            for(int i = 0;i<3;i++)
            {
                selectResults[i] = scanResults[i];
            }

//        //Select the three APs
//        for (int i = 0; i < 3; i++) {
//            sortList.add(scanResults[i]);
//        }
//        //Select the three APs
//        List<ScanResult> selectList = new ArrayList<ScanResult>();
//        Iterator<ScanResult> iterator = list.iterator();
//        for (int i = 0; i < 3; i++) {
//            selectList.add(iterator.next());
//        }

            return selectResults;
        }
        else
        {
            return null;
        }
    }

}
