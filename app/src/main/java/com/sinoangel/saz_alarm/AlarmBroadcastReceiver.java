package com.sinoangel.saz_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.sinoangel.saz_alarm.base.MyApplication;
import com.sinoangel.saz_alarm.bean.AlarmBean;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Z on 2017/3/22.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        long id = intent.getLongExtra("DATA", 0);
//        long startTime = intent.getLongExtra("TIME", 0);
        AlarmBean ab;
        try {
            ab = AlarmUtils.getDbUtisl().findById(AlarmBean.class, id);
        } catch (DbException e) {
            return;
        }
        if (ab == null) {
            return;
        }

//        AlarmUtils.outputLog("开启闹钟时间:" + AlarmUtils.formatLong(ab.getTime()));

        long now = new Date().getTime();
        AlarmUtils.outputLog("现在闹钟时间:" + AlarmUtils.formatLong(now));
        AlarmUtils.outputLog("预计闹钟时间:" + AlarmUtils.formatLong(ab.getTime()));
        if (ab.getStatus() != AlarmBean.STATUS_SHEP)
            if (now - ab.getTime() < 0 || (now - ab.getTime()) / 1000 > 59) {
                AlarmUtils.outputLog("作废" + (now - ab.getTime()) / 1000);
                if (ab.getType() == AlarmBean.ALARM_NZ_DANCI) {
                    ab.setStatus(AlarmBean.STATUS_OFF);
                    try {
                        AlarmUtils.getDbUtisl().saveOrUpdate(ab);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    return;
                } else if (ab.getType() == AlarmBean.ALARM_NZ_XUNHUAN) {
                    ab.checkTime();
                    return;
                }

            }
        ab.checkTime();

        Intent intent1 = new Intent(MyApplication.getInstance(), AlarmingActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("DATA", id);
        context.startActivity(intent1);
    }
}
