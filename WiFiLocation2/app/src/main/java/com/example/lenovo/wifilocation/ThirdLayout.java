package com.example.lenovo.wifilocation;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import net.lee.wifilocation.adapter.DeviceLocationAdapter;
import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.DeviceLocation;
import net.lee.wifilocation.net.GetDeviceLocation;
import net.lee.wifilocation.net.GetMyDiveces;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/4/29.
 */
public class ThirdLayout extends LinearLayout implements View.OnClickListener, OnItemSelectedListener {

    public ThirdLayout(Context context) {
        super(context);
    }

    public ThirdLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Spinner myDevicesSpinner;
    private List<String> myDeviceList;
    private ArrayAdapter<String> myDeviceSpinnerAdapter;

    private ListView deviceLocationLV;
    private ArrayList<DeviceLocation> deviceLocationList;
    private DeviceLocationAdapter deviceLocationAdapter;

    private Button searchDeviceBtn;
    private boolean isFirstClick = true;
    private static boolean hasSelect = false;
    private static boolean isTracking = false;

    //Use handler to get new data timing
    private Handler mHandler;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        myDevicesSpinner = (Spinner) findViewById(R.id.devicesSelectSp);
        deviceLocationLV = (ListView) findViewById(R.id.deviceLocationInThirdLV);
        searchDeviceBtn = (Button) findViewById(R.id.searchDeviceLocationBtn);

        myDeviceList = new ArrayList<String>();
        myDeviceSpinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, myDeviceList);
        myDeviceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myDevicesSpinner.setAdapter(myDeviceSpinnerAdapter);
        myDevicesSpinner.setOnItemSelectedListener(this);

        deviceLocationList = new ArrayList<DeviceLocation>();
        deviceLocationAdapter = new DeviceLocationAdapter(getContext(), deviceLocationList);
        deviceLocationLV.setAdapter(deviceLocationAdapter);

        searchDeviceBtn.setOnClickListener(this);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                new GetDeviceLocation(getContext(), myDevicesSpinner.getSelectedItem().toString(), 1,
                        new GetDeviceLocation.SuccessCallback() {
                            @Override
                            public void onSuccess(boolean haveData, List<DeviceLocation> list) {
                                if (haveData) {
                                    deviceLocationList.add(0, list.get(0));
                                    deviceLocationAdapter.notifyDataSetChanged();
                                }else
                                {
                                    isTracking = false;
                                    searchDeviceBtn.setText("开始追踪");
                                    mHandler.removeMessages(0);
                                }
                            }
                        }, new GetDeviceLocation.FailCallback() {
                    @Override
                    public void onFail(String failResult) {
                        Toast.makeText(getContext(), failResult, Toast.LENGTH_SHORT).show();


                    }
                });

                //Every 5 second get a new data
                mHandler.sendEmptyMessageDelayed(0, 5000);
            }
        };

    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            if (Config.valueMyDevicesChange) {
                //Get deveices name from server
                new GetMyDiveces(getContext(), Config.getCacheUserName(getContext()), new GetMyDiveces.SuccessCallback() {
                    @Override
                    public void onSuccess(String myDevices) {

                        System.out.println("devices" + myDevices);
                        if (!myDevices.equals("null")) {
                            Config.cacheAllDeivecsName(getContext(), myDevices);
                            refreshSpinner();
                        }
                    }
                }, new GetMyDiveces.FailCallback() {
                    @Override
                    public void onFail(String failResult) {
                        Toast.makeText(getContext(), failResult, Toast.LENGTH_SHORT).show();
                    }
                });
                Config.valueMyDevicesChange = false;

            } else {
                if (!hasSelect) {
                    refreshSpinner();
                }
            }
        }
    }

    private void refreshSpinner() {

        myDeviceList.clear();
        myDeviceList.add("无");
        myDeviceSpinnerAdapter.notifyDataSetChanged();

        if (Config.getCacheAllDeivecsName(getContext()) != null) {

            String myDevices = Config.getCacheAllDeivecsName(getContext());

            if (!myDevices.equals("")) {
                for (String device : myDevices.split(",")) {
                    myDeviceList.add(device);
                }
            }
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.searchDeviceLocationBtn) {
            if (!myDevicesSpinner.getSelectedItem().toString().equals("无")) {
                if (!isTracking) {
                    isTracking = true;
                    searchDeviceBtn.setText("停止追踪");
                    if (isFirstClick) {
                        isFirstClick = false;
                        new GetDeviceLocation(getContext(), myDevicesSpinner.getSelectedItem().toString(), 10,
                                new GetDeviceLocation.SuccessCallback() {
                                    @Override
                                    public void onSuccess(boolean haveData, List<DeviceLocation> list) {
                                        if (haveData) {
                                            for (DeviceLocation dl : list) {
                                                deviceLocationList.add(dl);
                                                System.out.println(dl.getAreaName() + " " + dl.getTime());
                                            }
                                            deviceLocationAdapter.notifyDataSetChanged();

                                            mHandler.sendEmptyMessageDelayed(0, 5000);

                                        } else {
                                            DeviceLocation dl = new DeviceLocation();
                                            dl.setAreaName("");
                                            dl.setLocationName("该设备暂时没有记录");
                                            dl.setTime("");
                                            deviceLocationList.add(dl);
                                            deviceLocationAdapter.notifyDataSetChanged();

                                            isFirstClick = true;
                                            isTracking = false;
                                            searchDeviceBtn.setText("开始追踪");
                                            mHandler.removeMessages(0);
                                        }
                                    }
                                }, new GetDeviceLocation.FailCallback() {
                            @Override
                            public void onFail(String failResult) {
                                Toast.makeText(getContext(), failResult, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else
                    {
                        mHandler.sendEmptyMessageDelayed(0, 5000);
                    }

                } else {
                    mHandler.removeMessages(0);
                    isTracking = false;
                    searchDeviceBtn.setText("开始追踪");
                }
            }
            else
            {
                Toast.makeText(getContext(),"请先选择设备",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        isFirstClick = true;
        isTracking = false;
        hasSelect = true;
        searchDeviceBtn.setText("开始追踪");
        mHandler.removeMessages(0);
        deviceLocationList.clear();
        deviceLocationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
