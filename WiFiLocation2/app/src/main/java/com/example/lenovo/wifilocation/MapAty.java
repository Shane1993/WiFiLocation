package com.example.lenovo.wifilocation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import net.lee.wifilocation.config.Config;
import net.lee.wifilocation.config.MyActionBarConfig;
import net.lee.wifilocation.utils.FileUtils;

import java.io.File;

/**
 * Created by LEE on 2015/5/21.
 */
public class MapAty extends Activity {

    ImageView mapImageView;
    String path = "Wifilocation";
    String pictureName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapaty);

        new MyActionBarConfig(getActionBar(),null);

        pictureName = getIntent().getStringExtra(Config.KEY_AREA_NAME);

        mapImageView = (ImageView) findViewById(R.id.mapIv);

        FileUtils fileUtils = new FileUtils();
        File file = fileUtils.getFile(path, pictureName + ".jpg");

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;

        Bitmap bmp = BitmapFactory.decodeFile(file.getAbsolutePath(),options);

        System.out.println("MapAty.java============file.getAbsolutePath() : " + file.getAbsolutePath());
        mapImageView.setImageBitmap(bmp);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
