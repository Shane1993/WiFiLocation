package com.example.lenovo.wifilocation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.LocationInfo;
import net.lee.wifilocation.net.Login;
import net.lee.wifilocation.net.VerifyToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;


public class MainActivity extends Activity implements View.OnClickListener{

    private TextView userNameTV;
    private ImageButton setttingImageBtn,searchImgageBtn;

    //create the TabHost
    private TabHost tabHost;

    //create WifiManager
    public static WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set action bar
        setContentView(R.layout.activity_main);


        Intent intent = getIntent();
        final String token = intent.getStringExtra(Config.KEY_TOKEN);
        String userName = intent.getStringExtra(Config.KEY_REQUEST_BODY_USERNAME);
        userNameTV = (TextView) findViewById(R.id.userNameTV);
        setttingImageBtn = (ImageButton) findViewById(R.id.settingImageBtn);
        searchImgageBtn = (ImageButton) findViewById(R.id.searchImageBtn);

        userNameTV.setText(userName);
        setttingImageBtn.setOnClickListener(this);
        searchImgageBtn.setOnClickListener(this);


        //Verify whether the token is out to date
        new VerifyToken(MainActivity.this, token, new VerifyToken.SuccessCallback() {
            @Override
            public void onSuccess() {

            }
        }, new VerifyToken.FailCallback() {
            @Override
            public void onFail(String failResult) {

                Toast.makeText(MainActivity.this, failResult, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, LoginAty.class);
                startActivity(intent);

                finish();
            }
        });

        //Bind the service
        wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);

        setTabHost();


    }



    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.searchImageBtn)
        {
            Intent searchIntent = new Intent(MainActivity.this,SearchAty.class);
            startActivity(searchIntent);
        }
        else if(v.getId() == R.id.settingImageBtn)
        {
            PopupMenu popupMenu = new PopupMenu(MainActivity.this,searchImgageBtn);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());

            //set clickListener
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Intent menuIntent;

                    switch (item.getItemId()) {
                        case R.id.myDevicePopupMenu:
                            menuIntent = new Intent(MainActivity.this,MyDevicesAty.class);
                            startActivity(menuIntent);
                            break;
                        case R.id.settingPopupMenu:
                            menuIntent = new Intent(MainActivity.this, SettingAty.class);
                            startActivity(menuIntent);
                            break;
                        case R.id.exitPopupMenu:
                            //Clear the data when exit
                            Config.cacheToken(MainActivity.this, null);
                            Config.cacheConnectPassword(MainActivity.this, null);
                            Config.cacheAllDeivecsName(MainActivity.this,null);
                            Config.valueMyDevicesChange = true;

                            menuIntent = new Intent(MainActivity.this, LoginAty.class);
                            startActivity(menuIntent);
                            finish();
                            break;
                    }
                    return true;
                }
            });
            //Do not remember to show
            popupMenu.show();
        }
    }

    /**
     * This function is used to set the tabhost
     */
    private void setTabHost() {
        //Set the TabHost
        tabHost = (TabHost) findViewById(R.id.tabHost);
        //Don't remember to set up
        tabHost.setup();

        TabHost.TabSpec firstTabSpec = tabHost.newTabSpec("tid1");
        TabHost.TabSpec secondTabSpec = tabHost.newTabSpec("tid2");
        TabHost.TabSpec thirdTabSpec = tabHost.newTabSpec("tid3");

        firstTabSpec.setIndicator("定位");
        secondTabSpec.setIndicator("管理");
        thirdTabSpec.setIndicator("搜寻设备");

        firstTabSpec.setContent(R.id.firstLayoutId);
        secondTabSpec.setContent(R.id.secondLayoutId);
        thirdTabSpec.setContent(R.id.thirdLayoutId);

        tabHost.addTab(firstTabSpec);
        tabHost.addTab(secondTabSpec);
        tabHost.addTab(thirdTabSpec);

        /**
         * The method of setting the font size and color in tab
         */
        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
            tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setTextSize(20);
        }

        tabHost.getTabWidget().setCurrentTab(0);
        TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
        tv.setTextColor(Color.parseColor("#ff97c6ff"));

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title); //Unselected Tabs
                    tv.setTextColor(Color.parseColor("#ffffff"));
                    tv.setTextSize(20);
                }

                TextView tv = (TextView) tabHost.getCurrentTabView().findViewById(android.R.id.title); //for Selected Tab
                tv.setTextColor(Color.parseColor("#ff97c6ff"));
            }
        });
    }

    /**
     * Get the location information through the wifimanager
     *
     * @return
     */
    public static LocationInfo getLocationInfo() {
        //Scan agian
        wifiManager.startScan();

        ScanResult[] scanResults = getAPSelectList(wifiManager.getScanResults());

        if (scanResults != null) {
            LocationInfo locationInfo = new LocationInfo();

            locationInfo.setMac1(scanResults[0].BSSID);
            locationInfo.setMac2(scanResults[1].BSSID);
            locationInfo.setMac3(scanResults[2].BSSID);
            locationInfo.setMac1Rssi(WifiManager.calculateSignalLevel(scanResults[0].level, 50));
            locationInfo.setMac2Rssi(WifiManager.calculateSignalLevel(scanResults[1].level, 50));
            locationInfo.setMac3Rssi(WifiManager.calculateSignalLevel(scanResults[2].level, 50));
            locationInfo.setMacSort(scanResults[0].BSSID + scanResults[1].BSSID + scanResults[2].BSSID);

            return locationInfo;
        } else {
            return null;
        }
    }

    /**
     * Return the Access Point Array after sorting by RSSI
     * Attention:This return Array will only include 3 APs with the most RSSI
     *
     * @param list
     * @return Scanresult[] after sorting by RSSI and Mac
     */
    public static ScanResult[] getAPSelectList(List<ScanResult> list) {

        //The number of ap must be 3 or more
        if (list.size() >= 3) {
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
            for (int i = 0; i < 2; i++) {
                for (int j = i + 1; j < 3; j++) {
                    //如果对比结果大于0说明前面字符串较大
                    if (scanResults[i].BSSID.compareTo(scanResults[j].BSSID) > 0) {
                        temp = scanResults[j];
                        scanResults[j] = scanResults[i];
                        scanResults[i] = temp;
                    }
                }
            }

            for (int i = 0; i < 3; i++) {
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
        } else {
            return null;
        }
    }
}
