package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.app.Service;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.listener.CloudCodeListener;


public class MainActivity extends Activity implements View.OnClickListener {

    private Button scanAPBtn;
    private TextView apScanTv,apSelectTv;
    private WifiManager wifiManager;
    private List<ScanResult> apList,apSelectList;

    private Button getJSBtn;
    private TextView resultTv;

    //BmobӦ��ID
    private String Bmob_AppId = "1b31c55348aeab1a15e5263e668fb88a";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scanAPBtn = (Button) findViewById(R.id.scanAPBtn);
        apScanTv = (TextView) findViewById(R.id.apTv);
        apSelectTv = (TextView) findViewById(R.id.apSelectTv);
        wifiManager = (WifiManager) getSystemService(Service.WIFI_SERVICE);
        apList = new ArrayList<ScanResult>();
        apSelectList = new ArrayList<ScanResult>();

        scanAPBtn.setOnClickListener(this);


        Bmob.initialize(this, Bmob_AppId);

        getJSBtn = (Button) findViewById(R.id.getJSBtn);
        resultTv = (TextView) findViewById(R.id.msgTv);

        getJSBtn.setOnClickListener(this);

    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scanAPBtn) {
            if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_DISABLED) {
                wifiManager.setWifiEnabled(true);
                toast("�Զ�����Wifi��������ɨ��");
            } else if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
                toast("Wifi���ڴ��У����Ժ�����");
            } else if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
                //�����¼
                apScanTv.setText("");
                apSelectTv.setText("");

                //�����յ���AP��ʾ����
                apList = getSortList(wifiManager.getScanResults());
                for (ScanResult scanResult : apList) {
                    apScanTv.append("SSID : " + scanResult.SSID + "\nMAC : " + scanResult.BSSID + "\nRSSI : "
                            + scanResult.level + "\n\n");
                }

                //���б����ź���ǿ������AP��ȡ����
                apSelectList = getApSelectList(apList);
                for (ScanResult scanResult : apSelectList) {
                    apSelectTv.append("SSID : " + scanResult.SSID + "\nMAC : " + scanResult.BSSID + "\nRSSI : "
                            + scanResult.level + "\n\n");
                }
            }

        } else if (v.getId() == R.id.getJSBtn) {
            resultTv.setText("");
            onRequestYun();
        }
    }
    private List<ScanResult> getSortList(List<ScanResult> list)
    {
        List<ScanResult> sortList = new ArrayList<ScanResult>();
        ScanResult[] scanResults = new ScanResult[list.size()];
        list.toArray(scanResults);
        ScanResult temp;
        for(int i=0;i<scanResults.length-1;i++)
        {
            for(int j=i+1;j<scanResults.length;j++)
            {
                if(scanResults[i].level < scanResults[j].level)
                {
                    temp = scanResults[j];
                    scanResults[j] = scanResults[i];
                    scanResults[i] = temp;
                }
            }
        }
        for(int i=0;i<scanResults.length;i++)
        {
            sortList.add(scanResults[i]);
        }
        return sortList;
    }
    private List<ScanResult> getApSelectList(List<ScanResult> list)
    {
        List<ScanResult> selectList = new ArrayList<ScanResult>();
        Iterator<ScanResult> iterator = list.iterator();

        for(int i=0;i<3;i++)
        {
            selectList.add(iterator.next());
        }
        return selectList;
    }



    private void onRequestYun() {
        // test��Ӧ��ոմ������ƶ˴�������
        String cloudCodeName = "test";
        JSONObject params = new JSONObject();
        try {
            // name���ϴ����ƶ˵Ĳ������ƣ�ֵ��bmob���ƶ˴������ͨ������request.body.name��ȡ���ֵ
            params.put("name", "bmob");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        // �����ƶ˴������
        AsyncCustomEndpoints cloudCode = new AsyncCustomEndpoints();
        // �첽�����ƶ˴���
        cloudCode.callEndpoint(MainActivity.this, cloudCodeName, params,
                new CloudCodeListener() {

                    @Override
                    public void onSuccess(Object arg0) {
                        // TODO Auto-generated method stub
                        resultTv.setText("�ƶ˴���ִ�гɹ���" + arg0.toString());

                    }


                    @Override
                    public void onFailure(int i, String s) {
                        Log.i("life", "" + s);
                        resultTv.setText("�ƶ˴���ִ��ʧ�ܣ�" + s);
                    }

                });

    }
}
