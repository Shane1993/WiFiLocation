package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.adapter.DeviceAdapter;
import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.config.MyActionBarConfig;
import net.lee.wifilocation.net.RefreshMyDevices;
import net.lee.wifilocation.net.GetDeviceName;
import net.lee.wifilocation.net.VerifyConnectPassword;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;

/**
 * Created by lenovo on 2015/5/3.
 */
public class SearchAty extends Activity implements SearchView.OnQueryTextListener, AdapterView.OnItemClickListener{

    private TextView hintTv;

    private ListView searchResultLv;
    private ArrayList<String> searchResultList;
    private DeviceAdapter adapter;

    private int searchStatus;
    private String userName;
    private boolean nameChangeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchaty);

        new MyActionBarConfig(getActionBar(),null);


        hintTv = (TextView) findViewById(R.id.searchHintTv);
        searchResultLv = (ListView) findViewById(R.id.searchResultLv);
        searchResultList = new ArrayList<String>();
        adapter = new DeviceAdapter(SearchAty.this,searchResultList);

        searchResultLv.setAdapter(adapter);
        searchResultLv.setOnItemClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("This is pause");
        if(nameChangeFlag)
        {
            final String myDevices = Config.getCacheAllDeivecsName(SearchAty.this);
            new RefreshMyDevices(SearchAty.this, Config.getCacheUserName(SearchAty.this), Config.getCachePassword(SearchAty.this),
                    myDevices, new RefreshMyDevices.SuccessCallback() {
                @Override
                public void onSuccess() {
                    System.out.println(myDevices + "success");
                }
            }, new RefreshMyDevices.FailCallback() {
                @Override
                public void onFail(String failResult) {
                    Toast.makeText(SearchAty.this, failResult, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        MenuItemCompat.expandActionView(searchViewItem);

        searchView = (SearchView) searchViewItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
//        System.out.println(query);

        //Hide the hint
        hintTv.setVisibility(View.GONE);
        searchResultList.clear();

        //Submit search to server
        new GetDeviceName(SearchAty.this, query, new GetDeviceName.SuccessCallback() {
            @Override
            public void onSuccess(int status, String deviceName) {

                searchStatus = status;
                searchResultList.add(deviceName);
                adapter.notifyDataSetChanged();
                userName = deviceName;

            }
        }, new GetDeviceName.FailCallback() {
            @Override
            public void onFail(String failResult) {
                Toast.makeText(SearchAty.this,failResult,Toast.LENGTH_SHORT).show();
            }
        });

        //Hide the keyboard
        searchView.clearFocus();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText))
        {
            hintTv.setVisibility(View.VISIBLE);
            searchResultList.clear();
            adapter.notifyDataSetChanged();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(searchStatus == Config.RESULT_STATUS_SUCCESS) {
            //Can not link to oneself repeatedly
            boolean repeat = false;
            if(Config.getCacheAllDeivecsName(SearchAty.this) != null)
            {
                for(String name : Config.getCacheAllDeivecsName(SearchAty.this).split(","))
                {
                    if(name.equals(userName))
                    {
                        repeat = true;
                        Toast.makeText(SearchAty.this,"已经添加该设备",Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
            //Can not link to youself
            if (!userName.equals(Config.getCacheUserName(SearchAty.this)) && !repeat) {

                final EditText alertEt = new EditText(SearchAty.this);
                //Change the input type of EditText
                alertEt.setInputType(InputType.TYPE_CLASS_NUMBER );
                alertEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

                int maxLength = 4;
                alertEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});

                alertEt.setHint("设备连接密码");
                //Create a AlertDialog
                final AlertDialog ad = new AlertDialog.Builder(SearchAty.this).create();
//            ad.setCanceledOnTouchOutside(false);
                ad.setTitle("添加新设备");
                ad.setView(alertEt);
                ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (TextUtils.isEmpty(alertEt.getText().toString())) {
                            Toast.makeText(SearchAty.this, "请输入连接密码", Toast.LENGTH_SHORT).show();
                        } else {
                            final String connectPassword = alertEt.getText().toString();

                            //Verify the connectPassword
                            new VerifyConnectPassword(SearchAty.this, userName, connectPassword, new VerifyConnectPassword.SuccessCallback() {
                                @Override
                                public void onSuccess(int status) {

                                    switch (status) {
                                        case Config.RESULT_STATUS_SUCCESS:

                                            if (Config.getCacheAllDeivecsName(SearchAty.this) == null || Config.getCacheAllDeivecsName(SearchAty.this).equals("")) {
                                                Config.cacheAllDeivecsName(SearchAty.this,userName);
                                            } else {
                                                String allDevicesName = Config.getCacheAllDeivecsName(SearchAty.this) + "," + userName;
                                                Config.cacheAllDeivecsName(SearchAty.this, allDevicesName);
                                            }

                                            nameChangeFlag = true;

                                            Toast.makeText(SearchAty.this, "添加设备成功", Toast.LENGTH_SHORT).show();
                                            break;
                                        case Config.RESULT_STATUS_FAIL:
                                            Toast.makeText(SearchAty.this, "密码错误", Toast.LENGTH_SHORT).show();
                                            break;
                                    }

                                }
                            }, new VerifyConnectPassword.FailCallback() {
                                @Override
                                public void onFail(String failResult) {
                                    Toast.makeText(SearchAty.this, failResult, Toast.LENGTH_SHORT).show();
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
    }

    //Disable the listview scroll
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        if(ev.getAction()==MotionEvent.ACTION_MOVE)
            return true;
        return super.dispatchTouchEvent(ev);
    }
}
