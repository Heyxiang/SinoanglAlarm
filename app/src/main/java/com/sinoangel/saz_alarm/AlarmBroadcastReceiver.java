package com.sinoangel.saz_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lidroid.xutils.exception.DbException;
import com.sinoangel.saz_alarm.base.MyApplication;
import com.sinoangel.saz_alarm.bean.AlarmBean;

import java.util.Calendar;

/**
 * Created by Z on 2017/3/22.
 */

public class AlarmBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (MyApplication.isDateChange)
            return;

        long id = intent.getLongExtra("DATA", 0);
        AlarmBean ab;
        try {
            ab = AlarmUtils.getDbUtisl().findById(AlarmBean.class, id);
        } catch (DbException e) {
            return;
        }
        if (ab == null) {
            return;
        }

        AlarmUtils.outputLog("ing 时间:" + AlarmUtils.formatLong(ab.getTime()));
        if (ab.getType() == AlarmBean.ALARM_NZ_XUNHUAN) {
            Calendar cal = Calendar.getInstance();
            int i = cal.get(Calendar.DAY_OF_WEEK);
            String[] list = ab.getLoop().split(",");
            if (!Boolean.parseBoolean(list[i - 1])) {
                return;
            }
//            Calendar calendar = Calendar.getInstance();
//            int hour_now = calendar.get(Calendar.HOUR_OF_DAY);
//            int min_now = calendar.get(Calendar.MINUTE);
//            calendar.setTimeInMillis(ab.getTime());
//            int hour_tag = calendar.get(Calendar.HOUR_OF_DAY);
//            int min_tag = calendar.get(Calendar.MINUTE);
//            if (hour_now != hour_tag || min_now != min_tag) {
//                return;
//            }
        } else {
            try {
                ab.setStatus(AlarmBean.STATUS_OFF);
                AlarmUtils.getDbUtisl().saveOrUpdate(ab);
            } catch (DbException e) {
                return;
            }
        }


        Intent intent1 = new Intent(context, AlarmingActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("DATA", id);
        context.startActivity(intent1);
    }
}
