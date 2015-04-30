package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.LocationInfo;
import net.lee.wifilocation.net.VerifyToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;


public class MainActivity extends Activity {

    //create the TabHost
    private TabHost tabHost;

    //create WifiManager
    public static WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        String token = intent.getStringExtra(Config.KEY_TOKEN);
        String userName = intent.getStringExtra(Config.KEY_USER_NAME);
        System.out.println("========>token : " + token);
        System.out.println("========>userName : " + userName);

//        onRequestCloud(token);
        new VerifyToken(MainActivity.this, token, new VerifyToken.SuccessCallback() {
            @Override
            public void onSuccess() {

            }
        }, new VerifyToken.FailCallback() {
            @Override
            public void onFail(String failResult) {
                Toast.makeText(MainActivity.this,failResult,Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, LoginAty.class);
                startActivity(intent);

                finish();
            }
        });

        //Bind the service
        wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);

        //Set the TabHost
        tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        tabHost.addTab(tabHost.newTabSpec("start").setIndicator("定位").setContent(R.id.firstLayoutId));
        tabHost.addTab(tabHost.newTabSpec("area").setIndicator("管理").setContent(R.id.secondLayoutId));
        tabHost.addTab(tabHost.newTabSpec("person").setIndicator("搜寻设备").setContent(R.id.thirdLayoutId));

    }

    /**
     * Verify whether the token is valid
     */
    private void onRequestCloud(String token) {
        // test对应你刚刚创建的云端代码名称
        String cloudCodeName = Config.KEY_CLOUD_CODE_NAME;
        JSONObject params = new JSONObject();
        try {
            // name是上传到云端的参数名称，值是bmob，云端代码可以通过调用request.body.name获取这个值
            params.put(Config.KEY_REQUEST_BODY_NAME, Config.KEY_VERIFY_TOKEN);
            params.put(Config.KEY_REQUEST_BODY_TOKEN, token);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // 创建云端代码对象
        AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
        // 异步调用云端代码
        cloudCode.callEndpoint(MainActivity.this, cloudCodeName, params,
                new CloudCodeListener() {

                    @Override
                    public void onSuccess(Object result) {
                        // TODO Auto-generated method stub

                        System.out.println("This is verifyToken's onSuccess : " + result.toString());

                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());

                            int statusResult = jsonObject.getInt("status");
                            if (statusResult == Config.RESULT_STATUS_SUCCESS) {

                            } else if (statusResult == Config.RESULT_INVALID_TOKEN) {
                                Toast.makeText(MainActivity.this, "验证过期，请重新登录", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, LoginAty.class);
                                startActivity(intent);

                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int i, String s) {

                        Toast.makeText(MainActivity.this, "验证token失败 : " + s, Toast.LENGTH_SHORT).show();
                    }

                });

    }

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
