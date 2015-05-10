package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.model.MyUser;
import net.lee.wifilocation.net.Login;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by lenovo on 2015/4/29.
 */
public class LoginAty extends Activity implements View.OnClickListener {


    private EditText userNameEt, passwordEt;
    private Button loginBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);

        userNameEt = (EditText) findViewById(R.id.userNameEt);
        passwordEt = (EditText) findViewById(R.id.passwordEt);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);

        loginBtn.setOnClickListener(this);
        signUpBtn.setOnClickListener(this);

        if(Config.getCacheUserName(LoginAty.this) != null && Config.getCachePassword(LoginAty.this) != null)
        {
            userNameEt.setText(Config.getCacheUserName(LoginAty.this));
            passwordEt.setText(Config.getCachePassword(LoginAty.this));
        }

        userNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                passwordEt.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {

        final String userName = userNameEt.getText().toString();
        final String password = passwordEt.getText().toString();

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(LoginAty.this, "«Î ‰»Î”√ªß√˚", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(LoginAty.this, "«Î ‰»Î√‹¬Î", Toast.LENGTH_SHORT).show();
            return;
        }

        if (v.getId() == R.id.loginBtn) {

            new Login(LoginAty.this, userName, password, new Login.SuccessCallback() {
                @Override
                public void onSuccess(String token) {
                    //Save token and username
                    Config.cacheToken(LoginAty.this, token);
                    Config.cacheUserName(LoginAty.this, userName);
                    Config.cachePassword(LoginAty.this,password);

                    Intent intent = new Intent(LoginAty.this,MainActivity.class);
                    intent.putExtra(Config.KEY_TOKEN,token);
                    intent.putExtra(Config.KEY_REQUEST_BODY_USERNAME,userName);
                    startActivity(intent);
                    finish();
                }
            }, new Login.FailCallback() {
                @Override
                public void onFail(String failResult) {
                    Toast.makeText(LoginAty.this,failResult,Toast.LENGTH_SHORT).show();
                }
            });

        } else if (v.getId() == R.id.signUpBtn) {
            MyUser myUser = new MyUser();
            myUser.setUsername(userName);
            myUser.setPassword(password);
            final ProgressDialog progressDialog = new ProgressDialog(LoginAty.this);
            progressDialog.show();
            myUser.signUp(LoginAty.this, new SaveListener() {
                @Override
                public void onSuccess() {
                    progressDialog.dismiss();
                    Toast.makeText(LoginAty.this, "◊¢≤·≥…π¶", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(int i, String s) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginAty.this, "◊¢≤· ß∞‹ : " + s, Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

}
