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
import net.lee.wifilocation.net.ResetLoginPassword;

/**
 * Created by lenovo on 2015/5/3.
 */
public class LoginPasswordAty extends Activity implements View.OnClickListener{

    private Button doneBtn;
    private EditText setPasswordEt,confirmPasswordEt;
    private TextView userNameInSettingTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpasswordactivity);

        View customNav = LayoutInflater.from(this).inflate(R.layout.customdone, null); // layout which contains your button.
        new MyActionBarConfig(getActionBar(),customNav);

        doneBtn = (Button) customNav.findViewById(R.id.doneBtn);
        setPasswordEt = (EditText) findViewById(R.id.setLoginPasswordEt);
        confirmPasswordEt = (EditText) findViewById(R.id.confirmLoginPasswordEt);
        userNameInSettingTv = (TextView) findViewById(R.id.userNameInSettingTv);

        doneBtn.setOnClickListener(this);
        userNameInSettingTv.setText(Config.getCacheUserName(LoginPasswordAty.this));


    }

    @Override
    public void onClick(View v) {
        final String setPassword = setPasswordEt.getText().toString();
        String confirmPassword = confirmPasswordEt.getText().toString();
        if(v.getId() == R.id.doneBtn)
        {
            if(!setPassword.equals(confirmPassword))
            {
                Toast.makeText(LoginPasswordAty.this,"两次输入的密码不一样",Toast.LENGTH_SHORT).show();
            }
            else if(!setPassword.equals(""))
            {
                new ResetLoginPassword(LoginPasswordAty.this, Config.getCacheUserName(LoginPasswordAty.this),
                        Config.getCachePassword(LoginPasswordAty.this), setPassword, new ResetLoginPassword.SuccessCallback() {
                    @Override
                    public void onSuccess(int status) {

                        switch (status)
                        {
                            case Config.RESULT_STATUS_SUCCESS:

                                //Success to reset password, save it
                                Config.cachePassword(LoginPasswordAty.this,setPassword);
                                Toast.makeText(LoginPasswordAty.this,getResources().getString(R.string.success_to_reset_password),Toast.LENGTH_SHORT).show();
                                finish();

                                break;
                            case Config.RESULT_STATUS_FAIL:

                                Toast.makeText(LoginPasswordAty.this,getResources().getString(R.string.fail_to_reset_password),Toast.LENGTH_SHORT).show();

                                break;
                        }

                    }
                }, new ResetLoginPassword.FailCallback() {
                    @Override
                    public void onFail(String failResult) {
                        Toast.makeText(LoginPasswordAty.this,failResult,Toast.LENGTH_SHORT).show();
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
