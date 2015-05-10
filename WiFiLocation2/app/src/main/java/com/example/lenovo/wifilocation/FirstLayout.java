package com.example.lenovo.wifilocation;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.DeviceLocation;
import net.lee.wifilocation.model.LocationInfo;
import net.lee.wifilocation.net.GetAreaName;
import net.lee.wifilocation.net.GetMyLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/4/18.
 */
public class FirstLayout extends LinearLayout implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    public FirstLayout(Context context) {
        super(context);
    }

    public FirstLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Button startBtn;
    private TextView myLocationTv;
    private LocationInfo locationInfo;
    private DeviceLocation deviceLocation;

//    private Spinner spinner;
//    private List<String> areaNameList;
//    private ArrayAdapter<String> arrayAdapter;

    private Handler mHandler;
    private static boolean isTracking = false;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        startBtn = (Button) findViewById(R.id.startMyBtn);
        myLocationTv = (TextView) findViewById(R.id.myLocationTv);
//        spinner = (Spinner) findViewById(R.id.areaSelectSp);

        startBtn.setOnClickListener(this);

//        areaNameList = new ArrayList<String>();
//        arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,areaNameList);
//        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(arrayAdapter);
//        spinner.setOnItemSelectedListener(this);

//        new GetAreaName(getContext(), new GetAreaName.SuccessCallback() {
//            @Override
//            public void onSuccess(String areaName) {
//                Config.valueAllAreaName = areaName;
//                refreshSpinner();
//            }
//        }, new GetAreaName.FailCallback() {
//            @Override
//            public void onFail(String failResult) {
//                Toast.makeText(getContext(),failResult,Toast.LENGTH_SHORT).show();
//            }
//        });

        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                showLocation();

                mHandler.sendEmptyMessageDelayed(0,5000);
            }
        };
    }

//    private void refreshSpinner()
//    {
//        areaNameList.clear();
//        //Set the first item is null
//        areaNameList.add("无");
//        arrayAdapter.notifyDataSetChanged();
//
//        String str = Config.valueAllAreaName;
//        System.out.println("FirstLayout : " + str);
//        //Add data to the spinner
//        if(str != null)
//        {
//            String[] areaNameArray = str.split(",");
//            for(String s : areaNameArray)
//            {
//                areaNameList.add(s);
//            }
//            arrayAdapter.notifyDataSetChanged();
//
//        }
//
//    }

//    @Override
//    protected void onVisibilityChanged(View changedView, int visibility) {
//        super.onVisibilityChanged(changedView, visibility);
//        if(visibility == View.VISIBLE)
//        {
//            if(Config.valueAreaChanged)
//            {
//                new GetAreaName(getContext(), new GetAreaName.SuccessCallback() {
//                    @Override
//                    public void onSuccess(String areaName) {
//                        Config.valueAllAreaName = areaName;
//                        refreshSpinner();
//                    }
//                }, new GetAreaName.FailCallback() {
//                    @Override
//                    public void onFail(String failResult) {
//                        Toast.makeText(getContext(),failResult,Toast.LENGTH_SHORT).show();
//                    }
//                });
//                Config.valueAreaChanged = false;
//            }
//        }
//    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.startMyBtn)
        {
//            if(Config.valueSelectedAreaName != null && !Config.valueSelectedAreaName.equals("无")) {
                if (!isTracking) {
                    isTracking = true;
                    startBtn.setText("停止定位");

                    if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

                        showLocation();

                    } else if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                        Toast.makeText(getContext(), "正在打开WiFi定位中", Toast.LENGTH_SHORT).show();
                    } else if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                        Toast.makeText(getContext(), "已自动开启WiFi", Toast.LENGTH_SHORT).show();
                        MainActivity.wifiManager.setWifiEnabled(true);
                    }

                    mHandler.sendEmptyMessageDelayed(0,5000);
                }
                else
                {
                    isTracking = false;
                    startBtn.setText("开始定位");
                    mHandler.removeMessages(0);
                }
//            }
//            else
//            {
//                Toast.makeText(getContext(), "请先选择所处区域", Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void showLocation()
    {
        myLocationTv.setText("");
        locationInfo = MainActivity.getLocationInfo();
        if (locationInfo != null) {
//            locationInfo.setAreaName(Config.valueSelectedAreaName);
            new GetMyLocation(getContext(), locationInfo.toJSONString(), new GetMyLocation.SuccessCallback() {
                @Override
                public void onSuccess(int locationStatus, String locationName) {
                    myLocationTv.setText(locationName);

                    switch (locationStatus)
                    {
                        case Config.RESULT_STATUS_SUCCESS:

                            deviceLocation = new DeviceLocation();
                            deviceLocation.setUserName(Config.getCacheUserName(getContext()));
                            deviceLocation.setAreaName(locationInfo.getAreaName());
                            deviceLocation.setLocationName(locationName);
                            deviceLocation.save(getContext());
                            break;
                        case Config.RESULT_STATUS_FAIL:

                            break;
                    }
                }
            }, new GetMyLocation.FailCallback() {
                @Override
                public void onFail(String failResult) {
                    Toast.makeText(getContext(),failResult,Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        myLocationTv.setText("");
        isTracking = false;
        startBtn.setText("开始定位");
        mHandler.removeMessages(0);
//        Config.valueSelectedAreaName = areaNameList.get(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
