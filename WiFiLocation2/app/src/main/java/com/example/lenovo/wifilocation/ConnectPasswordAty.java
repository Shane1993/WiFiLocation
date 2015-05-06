package com.example.lenovo.wifilocation;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.config.MyActionBarConfig;
import net.lee.wifilocation.net.GetConnectPassword;
import net.lee.wifilocation.net.ResetConnectPassword;
import net.lee.wifilocation.net.ResetLoginPassword;

/**
 * Created by lenovo on 2015/5/3.
 */
public class ConnectPasswordAty extends Activity implements View.OnClickListener{

    private Button doneBtn;
    private EditText setPasswordEt,confirmPasswordEt;
    private TextView userNameInSettingTv,connectPasswordInSettingTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connectpasswordactivity);

        View customNav = LayoutInflater.from(this).inflate(R.layout.customdone, null); // layout which contains your button.
        new MyActionBarConfig(getActionBar(),customNav);

        doneBtn = (Button) customNav.findViewById(R.id.doneBtn);
        setPasswordEt = (EditText) findViewById(R.id.setConnectPasswordEt);
        confirmPasswordEt = (EditText) findViewById(R.id.confirmConnectPasswordEt);
        userNameInSettingTv = (TextView) findViewById(R.id.userNameInSettingTv);
        connectPasswordInSettingTv = (TextView) findViewById(R.id.connectPasswordTv);

        doneBtn.setOnClickListener(this);
        userNameInSettingTv.setText(Config.getCacheUserName(ConnectPasswordAty.this));
        if(Config.getCacheConnectPassword(ConnectPasswordAty.this) != null)
        {
            connectPasswordInSettingTv.setText(Config.getCacheConnectPassword(ConnectPasswordAty.this));
        }
        else
        {
            new GetConnectPassword(ConnectPasswordAty.this, Config.getCacheUserName(ConnectPasswordAty.this), new GetConnectPassword.SuccessCallback() {
                @Override
                public void onSuccess(String connectPassword) {

                    connectPasswordInSettingTv.setText(connectPassword);

                }
            }, new GetConnectPassword.FailCallback() {
                @Override
                public void onFail(String failResult) {
                    Toast.makeText(ConnectPasswordAty.this,failResult,Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    @Override
    public void onClick(View v) {
        final String setPassword = setPasswordEt.getText().toString();
        String confirmPassword = confirmPasswordEt.getText().toString();
        if(v.getId() == R.id.doneBtn)
        {
            if(!setPassword.equals(confirmPassword))
            {
                Toast.makeText(ConnectPasswordAty.this, "两次输入的密码不一样", Toast.LENGTH_SHORT).show();
            }
            else if(!setPassword.equals(""))
            {
                new ResetConnectPassword(ConnectPasswordAty.this, Config.getCacheUserName(ConnectPasswordAty.this),
                        Config.getCachePassword(ConnectPasswordAty.this), setPassword, new ResetConnectPassword.SuccessCallback() {
                    @Override
                    public void onSuccess(int status) {

                        switch (status)
                        {
                            case Config.RESULT_STATUS_SUCCESS:

                                //Success to reset password, save it
                                Config.cacheConnectPassword(ConnectPasswordAty.this,setPassword);
                                Toast.makeText(ConnectPasswordAty.this,getResources().getString(R.string.success_to_reset_password),Toast.LENGTH_SHORT).show();
                                finish();

                                break;
                            case Config.RESULT_STATUS_FAIL:

                                Toast.makeText(ConnectPasswordAty.this,getResources().getString(R.string.fail_to_reset_password),Toast.LENGTH_SHORT).show();

                                break;
                        }

                    }
                }, new ResetConnectPassword.FailCallback() {
                    @Override
                    public void onFail(String failResult) {
                        Toast.makeText(ConnectPasswordAty.this,failResult,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
