package com.sinoangel.saz_alarm.base;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 父类说明：所有activity继承的父类
 */
public class MyBaseActivity extends Activity {

    public static final int flags = View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            getWindow().getDecorView().setSystemUiVisibility(flags);
    }

    {
        Resources resources = MyApplication.getInstance().getResources();// 获得res资源对象
        Configuration config = resources.getConfiguration();// 获得设置对象
        config.fontScale = 1.0f;
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        resources.updateConfiguration(config, dm);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
            if (hasFocus)
                getWindow().getDecorView().setSystemUiVisibility(flags);
    }

}

