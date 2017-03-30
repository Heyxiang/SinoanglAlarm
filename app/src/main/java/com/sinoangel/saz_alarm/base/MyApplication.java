package com.sinoangel.saz_alarm.base;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.sinoangel.saz_alarm.AlarmBroadcastReceiver;
import com.sinoangel.saz_alarm.AlarmUtils;

import java.lang.reflect.Method;


/**
 * 类说明：全局的appliction
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    public void onCreate() {
        super.onCreate();

        instance = this;
        AlarmUtils.setDefaultFont(this, "SANS_SERIF", "ziti_sim.ttf");

        IntentFilter intentFilter = new IntentFilter("SINOALARM_START");
        registerReceiver(new AlarmBroadcastReceiver(), intentFilter);
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
}
