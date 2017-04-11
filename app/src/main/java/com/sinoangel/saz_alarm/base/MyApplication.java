package com.sinoangel.saz_alarm.base;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.lidroid.xutils.exception.DbException;
import com.sinoangel.saz_alarm.AlarmBroadcastReceiver;
import com.sinoangel.saz_alarm.AlarmUtils;
import com.sinoangel.saz_alarm.BuildConfig;
import com.sinoangel.saz_alarm.DateTimeChangeReceiver;
import com.sinoangel.saz_alarm.bean.AlarmBean;

import java.lang.reflect.Method;
import java.util.List;


/**
 * 类说明：全局的appliction
 */
public class MyApplication extends Application {
    private static MyApplication instance;
    public static boolean isDateChange;

    public void onCreate() {
        super.onCreate();

        String processName = getProcessName(this,
                android.os.Process.myPid());
        if (!TextUtils.equals(BuildConfig.APPLICATION_ID, processName))
            return;

        instance = this;

        AlarmUtils.setDefaultFont(this, "SANS_SERIF", "ziti_sim.ttf");

        IntentFilter intentFilter = new IntentFilter("SINOALARM_START");
        registerReceiver(new AlarmBroadcastReceiver(), intentFilter);

        IntentFilter dateChangeFileter = new IntentFilter();
        dateChangeFileter.addAction(Intent.ACTION_DATE_CHANGED);
        dateChangeFileter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(new DateTimeChangeReceiver(), dateChangeFileter);

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

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
