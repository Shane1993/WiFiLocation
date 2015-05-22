package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.config.MyActionBarConfig;
import net.lee.wifilocation.model.AreaInfo;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by LEE on 2015/5/20.
 */
public class CreateAreaAty extends Activity implements View.OnClickListener{

    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView imageView;
    EditText editText;
    Button selectPicBtn,okBtn,cancelBtn;

    String picturePath;
    boolean selectMap = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createareaaty);

        new MyActionBarConfig(getActionBar(),null);

        imageView = (ImageView) findViewById(R.id.selectMapImageView);
        editText = (EditText) findViewById(R.id.createAreaEt);
        selectPicBtn = (Button) findViewById(R.id.selectMapBtn);
        okBtn = (Button) findViewById(R.id.createAreaOkBtn);
        cancelBtn = (Button) findViewById(R.id.createAreaCancelBtn);

        selectPicBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.selectMapBtn)
        {
            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,RESULT_LOAD_IMAGE);

        }else if(v.getId() == R.id.createAreaOkBtn)
        {
            if(editText.getText().toString().isEmpty())
            {
                Toast.makeText(CreateAreaAty.this,"«Î ‰»Î√˚≥∆",Toast.LENGTH_SHORT).show();

            } else if(!selectMap)
            {
                Toast.makeText(CreateAreaAty.this,"«Î—°‘ÒÕº∆¨",Toast.LENGTH_SHORT).show();
            }
            else
            {
                final String areaName = editText.getText().toString();
                //Send the picture to server
                final BmobFile file = new BmobFile(new File(picturePath));

                file.upload(CreateAreaAty.this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {

                        AreaInfo areaInfo = new AreaInfo();
                        areaInfo.setAreaName(areaName);
                        areaInfo.setMap(file);
                        areaInfo.save(CreateAreaAty.this, new SaveListener() {
                            @Override
                            public void onSuccess() {

                                Config.valueAllAreaName = Config.valueAllAreaName + "," + areaName;
                                Config.valueAreaChanged = true;
                                System.out.println("CreateAreaAty========Success");
                                finish();

                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Toast.makeText(CreateAreaAty.this, "¥¥Ω®«¯”Ú ß∞‹ : " + s, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onFailure(int i, String s) {
                        Toast.makeText(CreateAreaAty.this, "…œ¥´Õº∆¨ ß∞‹ : " + s, Toast.LENGTH_SHORT).show();
                    }
                });


            }

        }else if(v.getId() == R.id.createAreaCancelBtn)
        {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null)
        {
            Uri selectedImage = data.getData();

            Cursor cursor = getContentResolver().query(selectedImage,
                    null, null, null, null);
            if(cursor.moveToFirst()) {
                picturePath = cursor.getString(cursor.getColumnIndex("_data"));
                cursor.close();

                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                selectMap = true;
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
