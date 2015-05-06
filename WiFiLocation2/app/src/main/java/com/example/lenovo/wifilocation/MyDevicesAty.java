package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import net.lee.wifilocation.adapter.DeviceAdapter;
import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.config.MyActionBarConfig;
import net.lee.wifilocation.net.GetMyDiveces;
import net.lee.wifilocation.net.RefreshMyDevices;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2015/5/4.
 */
public class MyDevicesAty extends Activity implements View.OnCreateContextMenuListener {

    private ListView listView;
    private ArrayList<String> devicesList;
    private DeviceAdapter adapter;

    private boolean nameChangeFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mydevicesactivity);

        new MyActionBarConfig(getActionBar(), null);

        listView = (ListView) findViewById(R.id.myDevicesLv);
        devicesList = new ArrayList<String>();
        adapter = new DeviceAdapter(MyDevicesAty.this, devicesList);

        listView.setAdapter(adapter);
        listView.setOnCreateContextMenuListener(this);


        if (Config.valueMyDevicesChange) {
            //Get deveices name from server
            new GetMyDiveces(MyDevicesAty.this, Config.getCacheUserName(MyDevicesAty.this), new GetMyDiveces.SuccessCallback() {
                @Override
                public void onSuccess(String myDevices) {

                    System.out.println("devices" + myDevices);
                    if (!myDevices.equals("null")) {
                        Config.cacheAllDeivecsName(MyDevicesAty.this, myDevices);
                        refreshListView();
                    }
                }
            }, new GetMyDiveces.FailCallback() {
                @Override
                public void onFail(String failResult) {
                    Toast.makeText(MyDevicesAty.this, failResult, Toast.LENGTH_SHORT).show();
                }
            });

            Config.valueMyDevicesChange = false;
        }
        else
        {
            refreshListView();
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("this is pause");
        if (nameChangeFlag) {
            String myDevices = Config.getCacheAllDeivecsName(MyDevicesAty.this);
            new RefreshMyDevices(MyDevicesAty.this, Config.getCacheUserName(MyDevicesAty.this), Config.getCachePassword(MyDevicesAty.this),
                    myDevices, null, new RefreshMyDevices.FailCallback() {
                @Override
                public void onFail(String failResult) {
                    Toast.makeText(MyDevicesAty.this, failResult, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void refreshListView() {

        if (Config.getCacheAllDeivecsName(MyDevicesAty.this) != null && !Config.getCacheAllDeivecsName(MyDevicesAty.this).equals("")) {

            String[] devicesName = Config.getCacheAllDeivecsName(MyDevicesAty.this).split(",");
            for (String name : devicesName) {
                devicesList.add(name);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (nameChangeFlag) {
                new RefreshMyDevices(MyDevicesAty.this, Config.getCacheUserName(MyDevicesAty.this), Config.getCachePassword(MyDevicesAty.this),
                        Config.getCacheAllDeivecsName(MyDevicesAty.this), null, new RefreshMyDevices.FailCallback() {
                    @Override
                    public void onFail(String failResult) {
                        Toast.makeText(MyDevicesAty.this, failResult, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = (int) info.id;
        switch (item.getItemId()) {
            case R.id.deleteContextMenu:
                System.out.println(devicesList);
                devicesList.remove(position);
                adapter.notifyDataSetChanged();

                nameChangeFlag = true;
                String allDevices = "";
                for (String name : devicesList) {
                    allDevices += name + ",";
                }
                Config.cacheAllDeivecsName(MyDevicesAty.this, allDevices);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

}
