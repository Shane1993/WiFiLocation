package com.example.lenovo.wifilocation;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;

/**
 * Created by lenovo on 2015/5/1.
 */
public class SettingAty extends Activity implements AdapterView.OnItemClickListener{

    private ListView listView;

    private static final int LOGIN_PASSWORD = 0;
    private static final int CONNECT_PASSWORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingaty);

        ActionBar actionBar = getActionBar();
        //设置后退键
        actionBar.setDisplayHomeAsUpEnabled(true);
        //设置颜色
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff356bff")));
        //设置显示/隐藏图标icon
        actionBar.setDisplayShowHomeEnabled(false);
        //设置标题
        actionBar.setDisplayShowTitleEnabled(true);

        listView = (ListView) findViewById(R.id.settingLv);
        listView.setOnItemClickListener(this);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (position)
        {
            case LOGIN_PASSWORD:

                final EditText alertEt = new EditText(SettingAty.this);
                //设置输入类型为密码
                alertEt.setTransformationMethod(PasswordTransformationMethod.getInstance());

                final AlertDialog ad = new AlertDialog.Builder(SettingAty.this).create();
                ad.setTitle("请先填写原密码");
                ad.setView(alertEt);
                ad.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String password = alertEt.getText().toString();
                        if(password.equals(Config.getCachePassword(SettingAty.this)))
                        {
                            Intent intent = new Intent(SettingAty.this,LoginPasswordAty.class);
                            startActivity(intent);
                        }
                        else
                        {
                            final AlertDialog errAd = new AlertDialog.Builder(SettingAty.this).create();
                            errAd.setTitle("提示");
                            errAd.setMessage("\n密码错误，请重新输入\n");
                            errAd.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    errAd.dismiss();
                                }
                            });
                            errAd.show();
                        }

                        ad.dismiss();
                    }
                });
                ad.setButton(DialogInterface.BUTTON_NEGATIVE,"取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ad.dismiss();
                    }
                });
                ad.show();

                break;
            case CONNECT_PASSWORD:

                Intent intent = new Intent(SettingAty.this,ConnectPasswordAty.class);
                startActivity(intent);
                break;
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
