package com.sinoangel.saz_alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sinoangel.saz_alarm.base.MyApplication;

/**
 * Created by Z on 2017/2/15.
 */

public class DateTimeChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        MyApplication.isDateChange = true;
        AlarmUtils.getAU().nOFSoundService(false);
        AlarmUtils.getAU().nOFSoundService(true);
    }
}
