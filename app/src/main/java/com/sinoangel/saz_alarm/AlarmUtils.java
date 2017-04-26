package com.sinoangel.saz_alarm;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Type;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.sinoangel.saz_alarm.base.MyApplication;
import com.sinoangel.saz_alarm.bean.AlarmBean;
import com.sinoangel.saz_alarm.bean.AlarmTimer;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Z on 2016/12/15.
 */

public class AlarmUtils {
    private Context mContext;
    private static AlarmUtils au;
    private static DbUtils dbUtisl;
    private ServiceConnection connS;
    private IAlarmAidlInterface ssc;

    public static DbUtils getDbUtisl() {
        synchronized (AlarmUtils.class) {
            if (dbUtisl == null)
                dbUtisl = DbUtils.create(MyApplication.getInstance(), "SINOANGEL_ALARM");
        }
        return dbUtisl;
    }

    private AlarmUtils() {
        mContext = MyApplication.getInstance();
    }

    public static AlarmUtils getAU() {
        if (au == null)
            au = new AlarmUtils();
        return au;
    }

    public void nOFSoundService(boolean flage) {
        Intent AlarmService = new Intent(mContext, AlarmServer.class);
        if (connS == null)
            connS = new ServiceConnection() {
                public void onServiceDisconnected(ComponentName name) {
                    ssc = null;
                    AlarmUtils.outputLog("闹钟链接断开");
                }

                public void onServiceConnected(ComponentName name, IBinder service) {
                    ssc = IAlarmAidlInterface.Stub.asInterface(service);
                    AlarmUtils.outputLog("闹钟链接成功");
                    try {
                        List<AlarmBean> alab = AlarmUtils.getDbUtisl().findAll(AlarmBean.class);
                        if (alab != null)
                            for (AlarmBean ab : alab) {
                                if (ab.getStatus() == AlarmBean.STATUS_ON && ab.getType() != AlarmBean.ALARM_JISHIQI) {
                                    satrtAlarm(ab);
                                }
                            }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }

                }
            };

        if (flage) {
            boolean isBind = mContext.bindService(AlarmService, connS, mContext.BIND_AUTO_CREATE);
        } else {
            mContext.unbindService(connS);
        }
    }

    public void satrtAlarm(AlarmBean ab, boolean isSave) {
        boolean isloop = ab.getLoop().indexOf("true") < 0;
        if (isloop) {
            ab.setType(AlarmBean.ALARM_NZ_DANCI);
        } else {
            ab.setType(AlarmBean.ALARM_NZ_XUNHUAN);
        }

        Calendar now = Calendar.getInstance();
        if (ab.getTime() < now.getTimeInMillis()) {
            Calendar old = Calendar.getInstance();
            old.setTimeInMillis(ab.getTime());
            now.set(Calendar.HOUR_OF_DAY, old.get(Calendar.HOUR_OF_DAY));
            now.set(Calendar.MINUTE, old.get(Calendar.MINUTE));
            now.set(Calendar.SECOND, 0);
            if (now.getTimeInMillis() < new Date().getTime())
                now.add(Calendar.DAY_OF_MONTH, 1);
            ab.setTime(now.getTimeInMillis());
        }

        try {
            if (isloop) {
                ssc.setOnceAlarm(ab.getId(), ab.getTime());
                outputLog("单次 时间:" + formatLong(ab.getTime()));
            } else {
                ssc.setOnceAlarm(ab.getId(), ab.getTime());
                outputLog("循环 时间:" + formatLong(ab.getTime()));
            }
        } catch (Exception e) {
        }
        if (isSave)
            try {
                dbUtisl.saveOrUpdate(ab);
            } catch (DbException e) {
                e.printStackTrace();
            }
    }


    //开始复苏闹钟
    public void satrtAlarm(AlarmBean ab) {
//        ab.checkTime();
        try {
            ssc.setOnceAlarm(ab.getId(), ab.getTime());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private Map<Long, AlarmTimer> mlt = new HashMap<>();

    public void satrtAlarmForTimer(AlarmBean ab) {

        mlt.put(ab.getId(), new AlarmTimer(ab));

        try {
            dbUtisl.saveOrUpdate(ab);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public AlarmTimer getAtimer(AlarmBean ab) {
        if (mlt.get(ab.getId()) == null)
            mlt.put(ab.getId(), new AlarmTimer(ab));
        return mlt.get(ab.getId());
    }

    public void canelAlarm(AlarmBean ab) {
        if (ab.getType() == AlarmBean.ALARM_JISHIQI) {
            AlarmTimer at = mlt.get(ab.getId());
            if (at != null) {
                at.close();
                mlt.remove(ab.getId());
            }
        } else {
            try {
                ssc.cancelAlarm((int) ab.getId());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static String formatLong(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(time));
    }

    public static void outputLog(String str) {
        if (str != null)
            Log.e("dd", str);
    }

    public static Vibrator vibrate(Context activity, long[] pattern, boolean isRepeat) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(pattern, isRepeat ? 0 : -1);
        return vib;
    }

    public static String fMLongToStr_HM(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        long minute = calendar.get(Calendar.MINUTE);
        long hour = calendar.get(Calendar.HOUR);
        if (hour == 0)
            hour = 12;
        return String.format(" %02d:%02d ", hour, minute);
    }

    public static void showToast(String word) {
        Toast toast = Toast.makeText(MyApplication.getInstance(), word, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showToast(int id) {
        Toast toast = Toast.makeText(MyApplication.getInstance(), id, Toast.LENGTH_SHORT);
        toast.show();
    }

    //获取屏幕截图
    private static Bitmap myShot(Window window) {
        // 获取windows中最顶层的view
        int hei = MyApplication.getInstance().getHei() / 10;
        int wei = MyApplication.getInstance().getWei() / 10;
//        int hei = AppUtils.getDpi(true) / 4;
//        int wei = AppUtils.getDpi(false) / 4;
        Bitmap bmp = Bitmap.createBitmap(hei,
                wei, Bitmap.Config.ARGB_4444);
        View view = window.getDecorView();

        view.buildDrawingCache();

        // 允许当前窗口保存缓存信息
        view.setDrawingCacheEnabled(true);

        Canvas canvas = new Canvas(bmp);
        if (view.getDrawingCache() != null && !view.getDrawingCache().isRecycled())
            canvas.drawBitmap(view.getDrawingCache(), new Rect(0, 0, hei * 10, wei * 10), new Rect(0, 0, hei, wei), null);
        // 销毁缓存信息
        view.destroyDrawingCache();
        return bmp;
    }

    public static Bitmap blurBitmap(Bitmap bitmap, float radius) {
        //Create renderscript
        RenderScript rs = RenderScript.create(MyApplication.getInstance());

        //Create allocation from Bitmap
        Allocation allocation = Allocation.createFromBitmap(rs, bitmap);

        Type t = allocation.getType();

        //Create allocation with the same type
        Allocation blurredAllocation = Allocation.createTyped(rs, t);

        //Create script
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        //Set blur radius (maximum 25.0)
        blurScript.setRadius(radius);
        //Set input for script
        blurScript.setInput(allocation);
        //Call script for output allocation
        blurScript.forEach(blurredAllocation);

        //Copy script result into bitmap
        blurredAllocation.copyTo(bitmap);

        //Destroy everything to free memory
        try {

            blurredAllocation.destroy();
            allocation.destroy();
            t.destroy();
            rs.destroy();
            blurScript.destroy();
        } catch (Exception e) {
        }

        return bitmap;
    }

    public static void getBulrBit(final Window window, ImageView imageView) {
        imageView.setImageBitmap(blurBitmap(myShot(window), 3));
        imageView.setVisibility(View.VISIBLE);
    }

    public static void setDefaultFont(Context context,
                                      String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName,
                                      final Typeface newTypeface) {
        try {
            final Field staticField = Typeface.class
                    .getDeclaredField(staticTypefaceFieldName);
            staticField.setAccessible(true);
            staticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
