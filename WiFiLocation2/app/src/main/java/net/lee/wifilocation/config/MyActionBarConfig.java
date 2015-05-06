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

        //设置后退键
        actionBar.setDisplayHomeAsUpEnabled(true);
        //设置颜色
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff356bff")));
        //设置显示/隐藏图标icon
        actionBar.setDisplayShowHomeEnabled(false);
        //设置标题
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
