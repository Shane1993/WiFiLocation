package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.lee.wifilocation.config.Config;

import cn.bmob.v3.Bmob;

/**
 * This is the entrance of the APP
 * Created by lenovo on 2015/4/29.
 */
public class EntranceAty extends Activity {

    //Bmob”¶”√ID
    private String Bmob_AppId = "1b31c55348aeab1a15e5263e668fb88a";
//    private String Bmob_AppId = "3233acd4c0aefbdc13bc96792208c71e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This is about Bmob
        Bmob.initialize(this, Bmob_AppId);

        //
        String userName = Config.getCacheUserName(EntranceAty.this);
        String token = Config.getCacheToken(EntranceAty.this);

        if(userName != null && token != null)
        {
            Intent intent = new Intent(EntranceAty.this,MainActivity.class);
            intent.putExtra(Config.KEY_TOKEN,token);
            intent.putExtra(Config.KEY_REQUEST_BODY_USERNAME,userName);
            startActivity(intent);

            finish();
        }
        else
        {
            Intent intent = new Intent(EntranceAty.this,LoginAty.class);
            startActivity(intent);

            finish();
        }

    }


}
