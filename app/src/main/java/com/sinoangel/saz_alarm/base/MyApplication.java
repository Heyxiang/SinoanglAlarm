package com.sinoangel.saz_alarm.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sinoangel.saz_alarm.AlarmBroadcastReceiver;
import com.sinoangel.saz_alarm.AlarmUtils;
import com.sinoangel.saz_alarm.DateTimeChangeReceiver;
import com.sinoangel.saz_alarm.R;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Method;


/**
 * 类说明：全局的appliction
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    //谷歌统计
    private Tracker mTracker;
    public void onCreate() {
        super.onCreate();

        instance = this;

        AlarmUtils.setDefaultFont(this, "SANS_SERIF", "ziti_sim.ttf");

        IntentFilter intentFilter = new IntentFilter("SINOALARM_START");
        registerReceiver(new AlarmBroadcastReceiver(), intentFilter);

        IntentFilter dateChangeFileter = new IntentFilter();
        dateChangeFileter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(new DateTimeChangeReceiver(), dateChangeFileter);

        AlarmUtils.getAU().nOFSoundService(true);

        MobclickAgent.setScenarioType(MyApplication.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        getDefaultTracker();
    }

    /**
     * 获取唯一的MarsApplication实例
     **/
    public static MyApplication getInstance() {
        return instance;
    }

    private int hei, wei;

    public int getHei() {
        if (hei == 0 || wei == 0) {
            hei = getDpi(false);
            wei = getDpi(true);
        }

        return hei > wei ? hei : wei;
    }

    public int getWei() {
        if (hei == 0 || wei == 0) {
            hei = getDpi(false);
            wei = getDpi(true);
        }
        return wei < hei ? wei : hei;
    }

    //获取屏幕原始尺寸高度，包括虚拟功能键高度
    private int getDpi(boolean wOrH) {
        int dpi = 0;
        WindowManager windowManager = (WindowManager) MyApplication.getInstance().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, displayMetrics);
            if (wOrH)
                dpi = displayMetrics.widthPixels;
            else
                dpi = displayMetrics.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }

    public void sendAnalyticsActivity(String name) {
        getDefaultTracker().setScreenName(name);
        getDefaultTracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    public void sendAnalyticsEvent(String type, String name) {
        getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory(type)
                .setAction(name)
                .build());
    }
}
