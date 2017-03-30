package com.sinoangel.saz_alarm.bean;

import android.content.Intent;

import com.sinoangel.saz_alarm.TimeringActivity;
import com.sinoangel.saz_alarm.base.MyApplication;
import com.sinoangel.saz_alarm.bean.AlarmBean;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Z on 2016/12/26.
 */

public class AlarmTimer {
    private Timer timer = new Timer();
    private long start, end;
    private long pross;
    private int baifenbi;

    public AlarmTimer(final AlarmBean ab) {
        start = ab.getId();
        end = ab.getTime();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int max = (int) ((end - start) / 1000);
                pross = ((end - new Date().getTime()) / 1000);
                baifenbi = (int) ((double) pross / (double) max * 360);

                if (pross <= 0) {
                    Intent intent = new Intent(MyApplication.getInstance(), TimeringActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("DATA", ab);
                    MyApplication.getInstance().startActivity(intent);
                    close();
                }
            }
        }, 0, 1000);
    }

    public void close() {
        if (timer != null)
            timer.cancel();
        timer = null;
    }

    public int getBaifenbi() {
        return baifenbi;
    }

    public long getPross() {
        return pross;
    }

}
