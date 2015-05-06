package net.lee.wifilocation.config;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;

/**
 * Created by lenovo on 2015/5/4.
 */
public class MyActionBarConfig {

    private ActionBar actionBar;
    private View customNav;

    public MyActionBarConfig(ActionBar actionBar, View customNav)
    {
        this.actionBar = actionBar;
        this.customNav = customNav;

        //���ú��˼�
        actionBar.setDisplayHomeAsUpEnabled(true);
        //������ɫ
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff356bff")));
        //������ʾ/����ͼ��icon
        actionBar.setDisplayShowHomeEnabled(false);
        //���ñ���
        actionBar.setDisplayShowTitleEnabled(true);

        if(customNav != null)
        {
            //Custom.xml use LinearLayout, this way is perfect
            ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            actionBar.setCustomView(customNav, lp);
            actionBar.setDisplayShowCustomEnabled(true);
        }

    }
}
