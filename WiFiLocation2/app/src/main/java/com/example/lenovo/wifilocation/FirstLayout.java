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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.LocationInfo;
import net.lee.wifilocation.net.GetAreaName;
import net.lee.wifilocation.net.GetLocation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;

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

    private ImageButton startBtn;
    private TextView myLocationTv,myLocationInformationTv;
    private LocationInfo locationInfo;

    private Spinner spinner;
    private List<String> areaNameList;
    private ArrayAdapter<String> arrayAdapter;

    //Use the handler to change the UI after get the data from server
    private Handler handler;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        startBtn = (ImageButton) findViewById(R.id.startMyBtn);
        myLocationTv = (TextView) findViewById(R.id.myLocationTv);
        myLocationInformationTv = (TextView) findViewById(R.id.myLocationInformationTv);
        spinner = (Spinner) findViewById(R.id.areaSelectSp);

        startBtn.setOnClickListener(this);

        areaNameList = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(getContext(),R.layout.spinner_item,areaNameList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(this);

//        handler = new Handler()
//        {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if(msg.what == Config.VALUE_GET_AREA_NAME)
//                {
//                    refreshSpinner();
//                }
//
//            }
//        };

        //Because it's running in a new thread, so when you want to change you spinner you must wait for it's complete
        //So I decide to use the Handler
//        onRequestCloud(Config.ACTION_GET_AREA_NAME);
        new GetAreaName(getContext(), new GetAreaName.SuccessCallback() {
            @Override
            public void onSuccess(String areaName) {
                Config.valueAllAreaName = areaName;
                refreshSpinner();
            }
        }, new GetAreaName.FailCallback() {
            @Override
            public void onFail(String failResult) {
                Toast.makeText(getContext(),failResult,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshSpinner()
    {
        areaNameList.clear();
        //Set the first item is null
        areaNameList.add("无");
        arrayAdapter.notifyDataSetChanged();

        String str = Config.valueAllAreaName;
        System.out.println("FirstLayout : " + str);
        //Add data to the spinner
        if(str != null)
        {
            Log.i("String Data : AreaName",str);
            String[] areaNameArray = str.split(",");
            for(String s : areaNameArray)
            {
                areaNameList.add(s);
            }
            arrayAdapter.notifyDataSetChanged();

//            //Get the all JSON format data
//            try {
//                JSONArray jsonArray = new JSONArray(str);
//
//                //Get every data
//                for(int i=0;i<jsonArray.length();i++)
//                {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    areaNameList.add(jsonObject.getString(Config.KEY_AREA_NAME));
//                }
//                //You must notify the adapter to set change!!!
//                arrayAdapter.notifyDataSetChanged();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }


        }

    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(visibility == View.VISIBLE)
        {
            if(Config.valueAreaChanged)
            {
                new GetAreaName(getContext(), new GetAreaName.SuccessCallback() {
                    @Override
                    public void onSuccess(String areaName) {
                        Config.valueAllAreaName = areaName;
                        refreshSpinner();
                    }
                }, new GetAreaName.FailCallback() {
                    @Override
                    public void onFail(String failResult) {
                        Toast.makeText(getContext(),failResult,Toast.LENGTH_SHORT).show();
                    }
                });
                Config.valueAreaChanged = false;
            }
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.startMyBtn)
        {
            if(Config.valueSelectedAreaName != null && Config.valueSelectedAreaName != "无") {
                if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {

                    showLocation();

                } else if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                    Toast.makeText(getContext(), "正在打开WiFi定位中", Toast.LENGTH_SHORT).show();
                } else if (MainActivity.wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                    Toast.makeText(getContext(), "已自动开启WiFi", Toast.LENGTH_SHORT).show();
                    MainActivity.wifiManager.setWifiEnabled(true);
                }
            }
            else
            {
                Toast.makeText(getContext(), "请先选择所处区域", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showLocation()
    {
        myLocationTv.setText("");
        locationInfo = MainActivity.getLocationInfo();
        if (locationInfo != null) {
            locationInfo.setAreaName(Config.valueSelectedAreaName);
            myLocationInformationTv.setText("AreaName : " + locationInfo.getAreaName()
                    + "\nMac1 : " + locationInfo.getMac1() + ", RSSI:" + locationInfo.getMac1Rssi()
                    + "\nMac2 : " + locationInfo.getMac2() + ", RSSI:" + locationInfo.getMac2Rssi()
                    + "\nMac3 : " + locationInfo.getMac3() + ", RSSI:" + locationInfo.getMac3Rssi()
                    + "\nMacSort : " + locationInfo.getMacSort());
            //Send the data to server for getting the locaiton from server
//            onRequestCloud(Config.ACTION_GET_LOCATION);
            new GetLocation(getContext(), locationInfo.toJSONString(), new GetLocation.SuccessCallback() {
                @Override
                public void onSuccess(String locationName) {

                    myLocationTv.setText(locationName);
                }
            }, new GetLocation.FailCallback() {
                @Override
                public void onFail(String failResult) {
                    Toast.makeText(getContext(),failResult,Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    /**
     * Invoke the cloud function
     */
    private void onRequestCloud(String name) {

        if(name == Config.ACTION_GET_AREA_NAME) {

            // test对应你刚刚创建的云端代码名称
            String cloudCodeName = Config.KEY_CLOUD_CODE_NAME;
            JSONObject params = new JSONObject();
            try {
                // name是上传到云端的参数名称，值是bmob，云端代码可以通过调用request.body.name获取这个值
                params.put(Config.KEY_REQUEST_BODY_NAME, Config.ACTION_GET_AREA_NAME);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // 创建云端代码对象
            AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
            // 异步调用云端代码
            cloudCode.callEndpoint(getContext(), cloudCodeName, params,
                    new CloudCodeListener() {

                        @Override
                        public void onSuccess(Object result) {
                            // TODO Auto-generated method stub

                            System.out.println("FirstLayout's onSuccess");
                            Config.valueAllAreaName = result.toString();
                            handler.sendEmptyMessage(Config.VALUE_GET_AREA_NAME);

//                            try {
//                                JSONObject resultJSON = new JSONObject(result.toString());
//                                JSONArray resultArray = resultJSON.getJSONArray("results");
//                                if (resultArray != null) {
//                                    Config.valueAllAreaName = resultArray.toString();
//                                    System.out.println("AreaName : " + Config.valueAllAreaName);
//                                    //Invoke the handleMessage
//                                    handler.sendEmptyMessage(Config.VALUE_GET_AREA_NAME);
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                        }


                        @Override
                        public void onFailure(int i, String s) {

                            Toast.makeText(getContext(), "获取数据失败 : " + s, Toast.LENGTH_SHORT).show();
                        }

                    });
        }
        else if(name == Config.ACTION_GET_LOCATION)
        {
            // test对应你刚刚创建的云端代码名称
            String cloudCodeName = Config.KEY_CLOUD_CODE_NAME;
            JSONObject params = new JSONObject();
            try {
                // name是上传到云端的参数名称，值是bmob，云端代码可以通过调用request.body.name获取这个值
                params.put(Config.KEY_REQUEST_BODY_NAME, Config.ACTION_GET_LOCATION);
                params.put(Config.KEY_REQUEST_BODY_INFORMATION,locationInfo.toJSONString() );

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // 创建云端代码对象
            AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
            // 异步调用云端代码
            cloudCode.callEndpoint(getContext(), cloudCodeName, params,
                    new CloudCodeListener() {

                        @Override
                        public void onSuccess(Object result) {
                            // TODO Auto-generated method stub

                            System.out.println("FirstLayout's onSuccess");
                            myLocationTv.setText(result.toString());
                        }

                        @Override
                        public void onFailure(int i, String s) {

                            Toast.makeText(getContext(), "获取数据失败 : " + s, Toast.LENGTH_SHORT).show();
                        }

                    });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.i("Spinner","AreaName:" + areaNameList.get(position));

        Config.valueSelectedAreaName = areaNameList.get(position);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
