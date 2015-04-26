package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.LocationInfo;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lenovo on 2015/4/18.
 */
public class MeasureActivity extends Activity implements View.OnClickListener{

    private ImageButton scanAPBtn,getJSBtn,measureCancleBtn;
    private TextView locationTv, measureInfoTv,scanAPTv;
    private EditText measureEt;

    private LocationInfo locationInfo;
    private LocationInfo locationInfoFinal;
    //LocationInfo Array for calculate the final LocationInfo
    private LocationInfo[] locationInfos = new LocationInfo[10];

    private ProgressBar measurePB;

    private int recordCount = 0 ;

    private String areaName;

    private boolean compareResult = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measureactivity);

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

                if(!scanAPTv.getText().toString().equals("重新扫描：")) {
                    //Invoke the Handler's handleMessage
                    timerHandler.sendEmptyMessage(0);
                }
                else
                {
                    recordCount = 0;
                    locationTv.setText("");
                    timerHandler.sendEmptyMessage(0);
                }
            }

        } else if (v.getId() == R.id.getJSBtn) {

            if(TextUtils.isEmpty(measureInfoTv.toString()) || !compareResult)
            {
                toast("请定位成功后再上传");
            }
            else if(TextUtils.isEmpty(measureEt.getText().toString()))
            {
                toast("请输入区域名称");
             }
            else
            {
                //Set the location name
                locationInfoFinal.setName(measureEt.getText().toString());
                locationInfoFinal.save(MeasureActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {

                        toast("上传位置信息成功");
                        //Close the activity
                        finish();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toast("上传位置信息失败 : " + s);
                    }
                });


            }
        } else if (v.getId() == R.id.measureCancleBtn)
        {
            finish();
        }
    }

    /**
     * Use of Handler for doing the timecount work(定时任务)
     */
    private Handler timerHandler = new Handler()
    {
        String tempMacSort = "";
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //Only need 10 groups of data
            if(recordCount < 10) {

                locationInfo = MainActivity.getLocationInfo();

                if(locationInfo == null)
                {
                    toast("当前AP数量过少，无法定位");
                    //Clear the message
                    timerHandler.removeMessages(0);
                    recordCount = 0;
                    measurePB.setVisibility(View.GONE);
                    locationTv.setText("当前AP数量过少，无法定位");
                    return;
                }

                scanAPTv.setText("扫描中...");
                measureInfoTv.setText("正在获取位置信息\n请耐心等候...");
                measurePB.setVisibility(View.VISIBLE);
                measurePB.setProgress((int) (10 * (recordCount + 0.5)));

                locationInfos[recordCount] = locationInfo;
                locationTv.append(recordCount + 1 + "\nMac1 : " + locationInfo.getMac1() + ", RSSI:" + locationInfo.getMac1Rssi()
                        + "\nMac2 : " + locationInfo.getMac2() + ", RSSI:" + locationInfo.getMac2Rssi()
                        + "\nMac3 : " + locationInfo.getMac3() + ", RSSI:" + locationInfo.getMac3Rssi()
                        + "\nMacSort : " + locationInfo.getMacSort()
                        + "\n\n");

                if(recordCount == 0)
                {
                    tempMacSort = locationInfo.getMacSort();
                    System.out.println("compare1:\n" + tempMacSort);
                }
                else
                {
                    if(!tempMacSort.equals(locationInfo.getMacSort()))
                    {
                        System.out.println("compare2:\n" + tempMacSort + "\n" + locationInfo.getMacSort());

                        compareResult = false;
                        //Stop invoke this function
                        timerHandler.removeMessages(0);
                        recordCount = 0;
                        measurePB.setVisibility(View.GONE);
                        measureInfoTv.setText("由于扫描AP存在不同，建议重新扫描");
                        scanAPTv.setText("重新扫描：");
                        toast("由于扫描AP存在不同，建议重新扫描");

                        return;
                    }
                }

                recordCount++;
                //Every 2 second invoke this function
                timerHandler.sendEmptyMessageDelayed(0, 2000);
                Log.i("Handler", "Send the message");

            }
            else
            {
                compareResult = true;
                scanAPTv.setText("重新扫描：");
                //Stop invoke this function
                timerHandler.removeMessages(0);
//                Log.i("Handler", "Remove the message");
                //Clear the progressbar
                measurePB.setVisibility(View.GONE);

                locationInfoFinal = getFinalLocation(locationInfos);
                locationInfoFinal.setAreaName(areaName);
                measureInfoTv.setText(locationInfoFinal.toString());


                //Invalid to invoke this fuction because of recordcount
                recordCount++;
            }
        }
    };

    /**
     * Get the stable location infomation and return
     * @return LocationInfo
     */
    private LocationInfo getFinalLocation(LocationInfo[] locationInfoArray) {

        LocationInfo li = new LocationInfo();
        int rssi1Final,rssi2Final,rssi3Final;
        int count = locationInfoArray.length;
        int sum = 0;

        //Calculate the rssi1Final
        for(int j = 0; j < count; j++)
        {
            sum += locationInfoArray[j].getMac1Rssi();
        }
        rssi1Final = sum/count;
        sum = 0;

        //Calculate the rssi2Final
        for(int j = 0; j < count; j++)
        {
            sum += locationInfoArray[j].getMac2Rssi();
        }
        rssi2Final = sum/count;
        sum = 0;

        //Calculate the rssi3Final
        for(int j = 0; j < count; j++)
        {
            sum += locationInfoArray[j].getMac3Rssi();
        }
        rssi3Final = sum/count;

        //Set the Mac information, I select the second data(locationInfoArray[1]) for model
            //Do not ask me why, I just think that's stabler
        li.setMac1(locationInfoArray[1].getMac1());
        li.setMac2(locationInfoArray[1].getMac2());
        li.setMac3(locationInfoArray[1].getMac3());
        li.setMacSort(locationInfoArray[1].getMacSort());
        li.setMac1Rssi(rssi1Final);
        li.setMac2Rssi(rssi2Final);
        li.setMac3Rssi(rssi3Final);

        return li;
    }

}
