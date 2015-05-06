package com.example.lenovo.wifilocation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

    private ImageButton searchDeviceIb;
    private boolean firstClick = true;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        myDevicesSpinner = (Spinner) findViewById(R.id.devicesSelectSp);
        deviceLocationLV = (ListView) findViewById(R.id.deviceLocationInThirdLV);
        searchDeviceIb = (ImageButton) findViewById(R.id.searchDeviceLocationIb);

        myDeviceList = new ArrayList<String>();
        myDeviceSpinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, myDeviceList);
        myDeviceSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        myDevicesSpinner.setAdapter(myDeviceSpinnerAdapter);
        myDevicesSpinner.setOnItemSelectedListener(this);

        deviceLocationList = new ArrayList<DeviceLocation>();
        deviceLocationAdapter = new DeviceLocationAdapter(getContext(), deviceLocationList);
        deviceLocationLV.setAdapter(deviceLocationAdapter);

        searchDeviceIb.setOnClickListener(this);

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
                refreshSpinner();
            }
        }
    }

    private void refreshSpinner() {

        myDeviceList.clear();
        myDeviceList.add("无");
        myDeviceSpinnerAdapter.notifyDataSetChanged();

        if (Config.getCacheAllDeivecsName(getContext()) != null) {

            String myDevices = Config.getCacheAllDeivecsName(getContext());

            myDeviceList.clear();
            if (!myDevices.equals("")) {
                for (String device : myDevices.split(",")) {
                    myDeviceList.add(device);
                }
            }
        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.searchDeviceLocationIb) {
            System.out.println(myDevicesSpinner.getSelectedItem().toString());
            if (firstClick) {
                new GetDeviceLocation(getContext(), myDevicesSpinner.getSelectedItem().toString(), 10,
                        new GetDeviceLocation.SuccessCallback() {
                            @Override
                            public void onSuccess(boolean haveData, List<DeviceLocation> list) {
                                if (haveData) {
                                    for (DeviceLocation dl : list)
                                    {
                                        deviceLocationList.add(dl);
                                        System.out.println(dl.getAreaName() + " "+ dl.getTime());
                                    }
                                    deviceLocationAdapter.notifyDataSetChanged();
                                } else {
                                    DeviceLocation dl = new DeviceLocation();
                                    dl.setAreaName("");
                                    dl.setLocationName("该设备暂时没有记录");
                                    dl.setTime("");
                                    deviceLocationList.add(dl);
                                    deviceLocationAdapter.notifyDataSetChanged();

                                }
                            }
                        }, new GetDeviceLocation.FailCallback() {
                    @Override
                    public void onFail(String failResult) {
                        Toast.makeText(getContext(), failResult, Toast.LENGTH_SHORT).show();
                    }
                });

                firstClick = false;
            } else {
                new GetDeviceLocation(getContext(), myDevicesSpinner.getSelectedItem().toString(), 1,
                        new GetDeviceLocation.SuccessCallback() {
                            @Override
                            public void onSuccess(boolean haveData, List<DeviceLocation> list) {
                                if (haveData) {

                                    deviceLocationList.add(0,list.get(0));
                                    deviceLocationAdapter.notifyDataSetChanged();
                                }
                            }
                        }, new GetDeviceLocation.FailCallback() {
                    @Override
                    public void onFail(String failResult) {
                        Toast.makeText(getContext(), failResult, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        firstClick = true;
        deviceLocationList.clear();
        deviceLocationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
