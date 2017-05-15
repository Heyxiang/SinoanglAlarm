package com.sinoangel.saz_alarm;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.sinoangel.saz_alarm.adapter.AlarmMusicAdapter;
import com.sinoangel.saz_alarm.base.MyBaseActivity;
import com.sinoangel.saz_alarm.bean.AlarmBean;

import java.io.IOException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Z on 2016/12/14.
 */

public class AlarmingActivity extends MyBaseActivity implements View.OnClickListener {
    private MediaPlayer mPlayer, mPlayer_p4;
    private AssetFileDescriptor afd_p3, afd_p4;
    private SurfaceView sv_media;
    private Vibrator vibrator;
    private TextView tv_wait1, tv_wait5, tv_wait10, tv_time, tv_apm;
    private ImageView iv_toff;
    private AlarmBean ab;
    private Timer timer;
    private SurfaceHolder.Callback shc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long id = getIntent().getLongExtra("DATA", 0);
        try {
            ab = AlarmUtils.getDbUtisl().findById(AlarmBean.class, id);
        } catch (DbException e) {
            return;
        }

        Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

//        获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        //创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

        setContentView(R.layout.activity_alarming);

        sv_media = (SurfaceView) findViewById(R.id.sv_media);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_apm = (TextView) findViewById(R.id.tv_apm);
        tv_wait1 = (TextView) findViewById(R.id.tv_wait1);
        tv_wait5 = (TextView) findViewById(R.id.tv_wait5);
        tv_wait10 = (TextView) findViewById(R.id.tv_wait10);
        iv_toff = (ImageView) findViewById(R.id.iv_toff);

        afd_p3 = getResources().openRawResourceFd(AlarmMusicAdapter.lsraw[ab.getMusicid()]);
        switch (ab.getHeadpic()) {
            case 0:
                afd_p4 = getResources().openRawResourceFd(R.raw.alarm_yizhi_ve);
                break;
            case 1:
                afd_p4 = getResources().openRawResourceFd(R.raw.alarm_kexue_ve);
                break;
            case 2:
                afd_p4 = getResources().openRawResourceFd(R.raw.alarm_renzhi_ve);
                break;
            case 3:
                afd_p4 = getResources().openRawResourceFd(R.raw.alarm_yishu_ve);
                break;
            case 4:
                afd_p4 = getResources().openRawResourceFd(R.raw.alarm_shuxue_ve);
                break;
            default:
                afd_p4 = getResources().openRawResourceFd(R.raw.alarm_kexue_ve);
                break;
        }

        //音频
        if (ab.isXL()) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mPlayer.setLooping(true);
            float vol = ab.getVol() / 100f;
            try {
                mPlayer.setDataSource(afd_p3.getFileDescriptor(), afd_p3.getStartOffset(), afd_p3.getLength());
                afd_p3.close();
                mPlayer.prepare();
                mPlayer.setVolume(vol, vol);
                mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //视频
        mPlayer_p4 = new MediaPlayer();
        mPlayer_p4.setLooping(true);
        try {
            mPlayer_p4.setDataSource(afd_p4.getFileDescriptor(), afd_p4.getStartOffset(), afd_p4.getLength());
            afd_p4.close();
            mPlayer_p4.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        shc = new SurfaceHolder.Callback() {
            //surfaceview销毁的时候
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mPlayer_p4.setDisplay(null);
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mPlayer_p4.setDisplay(holder);
                mPlayer_p4.start();
            }

            //surfaceview改变的时候
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                       int height) {

            }
        };

        sv_media.getHolder().addCallback(shc);

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                int apm = calendar.get(Calendar.AM_PM);
                final String apmstr;
                if (apm == Calendar.AM) {
                    apmstr = "AM";
                } else {
                    apmstr = "PM";
                }
                final String time = AlarmUtils.fMLongToStr_HM(calendar.getTimeInMillis());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_apm.setText(apmstr);
                        tv_time.setText(time);
                    }
                });

            }
        }, 0, 1000);

        tv_wait1.setOnClickListener(this);
        tv_wait5.setOnClickListener(this);
        tv_wait10.setOnClickListener(this);

        iv_toff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vibrator != null)
                    vibrator.cancel();

                if (mPlayer_p4 != null && mPlayer_p4.isPlaying()) {
                    mPlayer_p4.stop();
                }

                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
                finish();
            }
        });

        if (ab.isZD())
            vibrator = AlarmUtils.vibrate(this, new long[]{200, 1000}, true);

        sendBroadcast(new Intent("SIONANGEL_ALARMING"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (vibrator != null)
            vibrator.cancel();

        if (mPlayer_p4 != null && mPlayer_p4.isPlaying()) {
            mPlayer_p4.stop();
        }

        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        if (vibrator != null)
            vibrator.cancel();

        if (mPlayer_p4 != null) {
            mPlayer_p4.release();
        }

        if (mPlayer != null) {
            mPlayer.release();
        }

        sv_media.getHolder().removeCallback(shc);

    }

    @Override
    public void onClick(View v) {

        try {
            ab.setStatus(AlarmBean.STATUS_SHEP);
            AlarmUtils.getDbUtisl().saveOrUpdate(ab);
        } catch (DbException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.tv_wait1:
                calendar.add(Calendar.MINUTE, 1);
                break;
            case R.id.tv_wait5:
                calendar.add(Calendar.MINUTE, 5);
                break;
            case R.id.tv_wait10:
                calendar.add(Calendar.MINUTE, 10);
                break;
        }
        ab.setTime(calendar.getTimeInMillis());
        AlarmUtils.getAU().satrtAlarm(ab, false);

        finish();
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (mPlayer != null)
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: //空闲
                        if (mPlayer != null && !mPlayer.isPlaying())
                            mPlayer.start();
                        if (vibrator != null)
                            vibrator.cancel();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING: //来电
                        if (mPlayer != null && mPlayer.isPlaying())
                            mPlayer.pause();
                        if (vibrator != null)
                            vibrator.cancel();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）
                        break;
                }
        }

    }

}
