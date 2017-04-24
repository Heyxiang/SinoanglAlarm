package com.sinoangel.saz_alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.annotation.Nullable;

/**
 * Created by Z on 2017/4/6.
 */

public class AlarmServer extends Service {
    private AlarmManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new AlarmConctrl();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        AlarmUtils.outputLog("闹钟服务创建");
    }

    public class AlarmConctrl extends IAlarmAidlInterface.Stub {

        @Override
        public void setOnceAlarm(long id, long startTime) throws RemoteException {

            Intent intent = new Intent("SINOALARM_START");
            intent.putExtra("DATA", id);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmServer.this, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.set(AlarmManager.RTC_WAKEUP, startTime, pendingIntent);

        }

        @Override
        public void setRepeatAlarm(long id, long startTime) throws RemoteException {
            Intent intent = new Intent("SINOALARM_START");
            intent.putExtra("DATA", id);
//            intent.setData(Uri.parse("content://alarm/alarmid/" + id));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmServer.this, (int) id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, AlarmManager.INTERVAL_DAY, pendingIntent);
        }

        @Override
        public void cancelAlarm(long id) throws RemoteException {
            Intent intent = new Intent("SINOALARM_START");
//            intent.setData(Uri.parse("content://alarm/alarmid/" + id));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AlarmServer.this, (int) id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            if (pendingIntent != null)
                manager.cancel(pendingIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        AlarmUtils.outputLog("闹钟服务结束");
//        System.exit(0);
    }
}
