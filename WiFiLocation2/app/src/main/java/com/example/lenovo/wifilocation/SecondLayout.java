package com.example.lenovo.wifilocation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.lee.wifilocation.adapter.MyAdapter;
import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.AreaInfo;
import net.lee.wifilocation.net.GetAreaName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by lenovo on 2015/4/18.
 */
public class SecondLayout extends LinearLayout implements View.OnClickListener, AdapterView.OnItemClickListener{

    public SecondLayout(Context context) {
        super(context);
    }

    public SecondLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Button measureBtn;
    private Button createBtn;
    private ListView locationListView;
    private List<String> locationList;
    private MyAdapter adapter;
    private Handler handler;

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        measureBtn = (Button) findViewById(R.id.measureBtn);
        createBtn = (Button) findViewById(R.id.createLocationBtn);
        locationListView = (ListView) findViewById(R.id.locationLV);

        locationList = new ArrayList<String>();
        adapter = new MyAdapter(getContext(),locationList);
        locationListView.setAdapter(adapter);

        measureBtn.setOnClickListener(this);
        createBtn.setOnClickListener(this);
        locationListView.setOnItemClickListener(this);

//        handler = new Handler()
//        {
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                if (msg.what == Config.VALUE_GET_AREA_NAME) {
//                    refreshListView();
//                }
//            }
//
//        };
//        onRequestCloud();

        new GetAreaName(getContext(), new GetAreaName.SuccessCallback() {
            @Override
            public void onSuccess(String areaName) {

                Config.valueAllAreaName = areaName;
                refreshListView();
            }
        }, new GetAreaName.FailCallback() {
            @Override
            public void onFail(String failResult) {
                Toast.makeText(getContext(),failResult,Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void refreshListView()
    {
        String str = Config.valueAllAreaName;
        System.out.println("SecondLayout : " + str);
        if(str != null)
        {
            Log.i("StringData",str);

            String[] areaNameArray = str.split(",");
            for(String s : areaNameArray)
            {
                locationList.add(s);
            }
            adapter.notifyDataSetChanged();


//            //Get the all JSON format data
//            try {
//                JSONArray jsonArray = new JSONArray(str);
//
//                //Get every data
//                for(int i=0;i<jsonArray.length();i++)
//                {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                    AreaInfo areaInfo = new AreaInfo(jsonObject.getString(Config.KEY_AREA_NAME));
//                    locationList.add(areaInfo);
//                }
//                adapter.notifyDataSetChanged();
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.measureBtn)
        {
            if(Config.valueManageSelectedAreaName == null)
            {
                Toast.makeText(getContext(),"请先选择当前区域",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Intent intent = new Intent(getContext(),MeasureActivity.class);
                intent.putExtra(Config.KEY_AREA_NAME,Config.valueManageSelectedAreaName);
                getContext().startActivity(intent);
            }
        }else if(v.getId() == R.id.createLocationBtn)
        {
            final View view = LayoutInflater.from(getContext()).inflate(R.layout.alerdialog, null);
            final EditText alertEt = (EditText) view.findViewById(R.id.alertEt);
            //Create a AlertDialog
            final AlertDialog ad = new AlertDialog.Builder(getContext()).create();
            ad.setCanceledOnTouchOutside(false);
            ad.setTitle("添加新区域");
            ad.setView(view);
            ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (TextUtils.isEmpty(alertEt.getText().toString())) {
                        Toast.makeText(getContext(), "请输入区域名称", Toast.LENGTH_SHORT).show();
                    } else {
                        final String name = alertEt.getText().toString();

                        //Send the new area name to the server
                        AreaInfo areaInfo = new AreaInfo(name);
                        areaInfo.save(getContext(), new SaveListener() {
                            @Override
                            public void onSuccess() {

                                Log.i("Bmob", "This is onSuccess");
                                locationList.add(name);
                                //Tell the FirstLayout's spinner that area have change
                                Config.valueAreaChanged = true;
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Log.i("Bmob", "This is onFailure : " + s);

                                Toast.makeText(getContext(), "创建区域失败：" + s, Toast.LENGTH_SHORT).show();
                            }
                        });

                        ad.dismiss();
                    }
                }
            });
            ad.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ad.dismiss();
                }
            });
            ad.show();

        }
    }

    /**
     * Invoke the cloud function
     */
    private void onRequestCloud() {
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

                        System.out.println("This is MainActivity's onSuccess");
                        Config.valueAllAreaName = result.toString();
                        handler.sendEmptyMessage(Config.VALUE_GET_AREA_NAME);

//                        try {
//                            JSONObject resultJSON = new JSONObject(result.toString());
//
//                            JSONArray resultArray = resultJSON.getJSONArray("results");
//                            if(resultArray != null)
//                            {
//
//                                Config.valueAllAreaName = resultArray.toString();
//
//                                System.out.println("AreaName : " + Config.valueAllAreaName);
//
//                                handler.sendEmptyMessage(Config.VALUE_GET_AREA_NAME);
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }

                    }


                    @Override
                    public void onFailure(int i, String s) {

                        Toast.makeText(getContext(),"获取数据失败 : " + s,Toast.LENGTH_SHORT).show();
                    }

                });

    }

    /**
     * This is short click on items
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Config.valueManageSelectedAreaName = locationList.get(position);

        Log.i("onItemClick","Config.valueManageSelectedAreaName:" + Config.valueManageSelectedAreaName );
        //Tell the ListView I have changed! Refresh the list quickly.
        adapter.notifyDataSetChanged();
    }

}
