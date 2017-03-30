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

import com.lidroid.xutils.exception.DbException;
import com.sinoangel.saz_alarm.base.MyBaseActivity;
import com.sinoangel.saz_alarm.bean.AlarmBean;

import java.io.IOException;

/**
 * Created by Z on 2016/12/14.
 */

public class TimeringActivity extends MyBaseActivity {
    private MediaPlayer mPlayer, mPlayer_p4;
    private AssetFileDescriptor afd_p3, afd_p4;
    private SurfaceView sv_media;
    private Vibrator vibrator;
    private AlarmBean ab;
    private ImageView iv_toff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) this
                .getSystemService(Context.TELEPHONY_SERVICE);
        //创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);


        ab = (AlarmBean) getIntent().getExtras().get("DATA");
        AlarmUtils.outputLog("ing 时间:" + AlarmUtils.formatLong(ab.getTime()));
        try {
            AlarmUtils.getDbUtisl().delete(ab);
        } catch (DbException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_timering);

        sv_media = (SurfaceView) findViewById(R.id.sv_media);
        iv_toff = (ImageView) findViewById(R.id.iv_toff);
        iv_toff.setVisibility(View.VISIBLE);

        afd_p3 = getResources().openRawResourceFd(R.raw.alarm_jishiqi);
        afd_p4 = getResources().openRawResourceFd(R.raw.alarm_jishiqi_ve);

        //音频
        if (ab.isXL()) {
            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mPlayer.setLooping(true);
            int vol = ab.getVol() / 10;
            mPlayer.setVolume(vol, vol);
            try {
                mPlayer.setDataSource(afd_p3.getFileDescriptor(), afd_p3.getStartOffset(), afd_p3.getLength());
                afd_p3.close();
                mPlayer.prepare();
                mPlayer.start();
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

        sv_media.getHolder().addCallback(new SurfaceHolder.Callback() {
            //surfaceview销毁的时候
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

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
        });

        iv_toff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (ab.isZD())
            vibrator = AlarmUtils.vibrate(TimeringActivity.this, new long[]{200, 1000}, true);

        sendBroadcast(new Intent("SIONANGEL_STOPAUDIO"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
        }
        if (mPlayer_p4 != null) {
            mPlayer_p4.stop();
            mPlayer_p4.release();
        }

        if (vibrator != null)
            vibrator.cancel();
        sendBroadcast(new Intent("SIONANGEL_STOPAUDIO").putExtra("data", true));
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (mPlayer != null)
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: //空闲
                        if (!mPlayer.isPlaying())
                            mPlayer.start();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING: //来电
                        mPlayer.pause();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）
                        break;
                }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        ab = (AlarmBean) intent.getExtras().get("DATA");
        AlarmUtils.outputLog("ing 时间:" + AlarmUtils.formatLong(ab.getTime()));
        try {
            AlarmUtils.getDbUtisl().delete(ab);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }
}
