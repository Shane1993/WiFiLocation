package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.config.MyActionBarConfig;
import net.lee.wifilocation.model.LocationInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lenovo on 2015/4/18.
 */
public class MeasureActivity extends Activity implements View.OnClickListener {

    private ImageButton scanAPBtn, getJSBtn, measureCancleBtn;
    private TextView locationTv, measureInfoTv, scanAPTv;
    private EditText measureEt;

    private LocationInfo locationInfo;
    private ArrayList<LocationInfo> locationInfoFinalList;
    //LocationInfo Array for calculate the final LocationInfo
    private LocationInfo[] locationInfos = new LocationInfo[20];

    private ProgressBar measurePB;

    private int recordCount = 0;

    private String areaName;

    private int isSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measureactivity);

        new MyActionBarConfig(getActionBar(), null);

        scanAPBtn = (ImageButton) findViewById(R.id.scanAPBtn);
        locationTv = (TextView) findViewById(R.id.apTv);
        scanAPTv = (TextView) findViewById(R.id.scanAPTv);
        scanAPBtn.setOnClickListener(this);

        getJSBtn = (ImageButton) findViewById(R.id.getJSBtn);
        measureCancleBtn = (ImageButton) findViewById(R.id.measureCancleBtn);
        measureInfoTv = (TextView) findViewById(R.id.measureInfoTv);
        measureEt = (EditText) findViewById(R.id.measureEt);
        measurePB = (ProgressBar) findViewById(R.id.measurePB);

        getJSBtn.setOnClickListener(this);
        measureCancleBtn.setOnClickListener(this);

        //Get the areaName send by the SecondLayout
        areaName = getIntent().getStringExtra(Config.KEY_AREA_NAME);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void toast(String msg) {
        Toast.makeText(MeasureActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scanAPBtn) {
            if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                MainActivity.wifiManager.setWifiEnabled(true);
                toast("自动开启Wifi，请重新扫描");
            } else if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                toast("Wifi正在打开中，请稍后再试");
            } else if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

                if (!scanAPTv.getText().toString().equals("重新扫描：")) {
                    //Invoke the Handler's handleMessage
                    timerHandler.sendEmptyMessage(0);
                } else {
                    recordCount = 0;
                    locationTv.setText("");
                    timerHandler.sendEmptyMessage(0);
                }
            }

        } else if (v.getId() == R.id.getJSBtn) {

            if (locationInfoFinalList == null) {
                toast("请定位成功后再上传");
            } else if (TextUtils.isEmpty(measureEt.getText().toString())) {
                toast("请输入区域名称");
            } else {
                isSuccess = 0;
                for (LocationInfo locationInfoFinal : locationInfoFinalList) {
                    //Set the location name
                    locationInfoFinal.setLocationName(measureEt.getText().toString());
                    locationInfoFinal.save(MeasureActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            isSuccess++;
                            if(isSuccess == locationInfoFinalList.size())
                            {
                                toast("上传位置信息成功");
                                //Close the activity
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toast("上传位置信息失败 : " + s);
                        }
                    });
//                    System.out.println("Before sleep");
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                    System.out.println("after sleep");
                }

            }
        } else if (v.getId() == R.id.measureCancleBtn) {
            finish();
        }
    }

    private Map<String, ArrayList<LocationInfo>> locationMap = new HashMap<String, ArrayList<LocationInfo>>();

    /**
     * Use of Handler for doing the timecount work(定时任务)
     */
    private Handler timerHandler = new Handler() {
        //        String tempMacSort = "";
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //Only need 20 groups of data
            if (recordCount < 20) {

                locationInfo = MainActivity.getLocationInfo();

                if (locationInfo == null) {
//                    toast("当前AP数量过少，无法定位");
//                    //Clear the message
//                    timerHandler.removeMessages(0);
//                    recordCount = 0;
//                    measurePB.setVisibility(View.GONE);
//                    locationTv.setText("当前AP数量过少，无法定位");
//                    return;
                    locationTv.append("当前AP数量过少，无法定位\n\n");
                    recordCount++;
                    //Every 2 second invoke this function
                    timerHandler.sendEmptyMessageDelayed(0, 2000);
                }

                if (recordCount == 0) {
                    scanAPTv.setText("扫描中...");
                    measureInfoTv.setText("正在获取位置信息\n请耐心等候...");
                    measurePB.setVisibility(View.VISIBLE);
                }
                measurePB.setProgress((int) (5 * (recordCount + 0.5)));

                boolean hasSameKay = false;

                if (locationMap.keySet().size() > 0) {
                    for (String macSort : locationMap.keySet()) {
                        if (locationInfo.getMacSort().equals(macSort)) {
                            ArrayList<LocationInfo> list = locationMap.get(macSort);
                            list.add(locationInfo);
                            locationMap.put(macSort, list);
                            hasSameKay = true;
                            break;
                        }
                    }
                    //If there are not the same macSort in map, add mew data
                    if (!hasSameKay) {
                        ArrayList<LocationInfo> list = new ArrayList<LocationInfo>();
                        list.add(locationInfo);
                        locationMap.put(locationInfo.getMacSort(), list);
                    }
                } else {
                    ArrayList<LocationInfo> list = new ArrayList<LocationInfo>();
                    list.add(locationInfo);
                    locationMap.put(locationInfo.getMacSort(), list);
                }

//                locationInfos[recordCount] = locationInfo;
                locationTv.append(recordCount + 1 + "\nMac1 : " + locationInfo.getMac1() + ", RSSI:" + locationInfo.getMac1Rssi()
                        + "\nMac2 : " + locationInfo.getMac2() + ", RSSI:" + locationInfo.getMac2Rssi()
                        + "\nMac3 : " + locationInfo.getMac3() + ", RSSI:" + locationInfo.getMac3Rssi()
                        + "\nMacSort : " + locationInfo.getMacSort()
                        + "\n\n");

//                if(recordCount == 0)
//                {
//                    tempMacSort = locationInfo.getMacSort();
//                    System.out.println("compare1:\n" + tempMacSort);
//                }
//                else
//                {
//                    if(!tempMacSort.equals(locationInfo.getMacSort()))
//                    {
//                        System.out.println("compare2:\n" + tempMacSort + "\n" + locationInfo.getMacSort());
//
//                        //Stop invoke this function
//                        timerHandler.removeMessages(0);
//                        recordCount = 0;
//                        measurePB.setVisibility(View.GONE);
//                        measureInfoTv.setText("由于扫描AP存在不同，建议重新扫描");
//                        scanAPTv.setText("重新扫描：");
//                        toast("由于扫描AP存在不同，建议重新扫描");
//
//                        return;
//                    }
//                }

                recordCount++;
                //Every 2 second invoke this function
                timerHandler.sendEmptyMessageDelayed(0, 2000);

            }
            //Scan over
            else {
                scanAPTv.setText("重新扫描：");
                //Stop invoke this function
                timerHandler.removeMessages(0);
//                Log.i("Handler", "Remove the message");
                //Clear the progressbar
                measurePB.setVisibility(View.GONE);

                //Calculate the average
//                locationInfoFinal = getFinalLocationList(locationInfos);
//                locationInfoFinal.setAreaName(areaName);
//                measureInfoTv.setText(locationInfoFinal.toString());
                locationInfoFinalList = getFinalLocationList(locationMap);
                measureInfoTv.setText(locationInfoFinalList.toString());

                //Invalid to invoke this fuction because of recordcount
                recordCount++;
            }
        }
    };

    /**
     * Get the stable location infomation and return
     *
     * @return LocationInfo
     */
    private ArrayList<LocationInfo> getFinalLocationList(Map<String, ArrayList<LocationInfo>> map) {

        ArrayList<LocationInfo> list = new ArrayList<LocationInfo>();

        for (ArrayList<LocationInfo> arrayList : map.values()) {

            LocationInfo li = new LocationInfo();
            int rssi1Final, rssi2Final, rssi3Final;
            int count = arrayList.size();
            int sum1 = 0, sum2 = 0, sum3 = 0;

            for (LocationInfo locationInfo : arrayList) {
                sum1 += locationInfo.getMac1Rssi();
                sum2 += locationInfo.getMac2Rssi();
                sum3 += locationInfo.getMac3Rssi();
            }
            rssi1Final = sum1 / count;
            rssi2Final = sum2 / count;
            rssi3Final = sum3 / count;

            li.setMac1(arrayList.get(0).getMac1());
            li.setMac2(arrayList.get(0).getMac2());
            li.setMac3(arrayList.get(0).getMac3());
            li.setMacSort(arrayList.get(0).getMacSort());
            li.setMac1Rssi(rssi1Final);
            li.setMac2Rssi(rssi2Final);
            li.setMac3Rssi(rssi3Final);
            li.setAreaName(areaName);

            list.add(li);
        }

        System.out.println(list);
        return list;

    }


}
