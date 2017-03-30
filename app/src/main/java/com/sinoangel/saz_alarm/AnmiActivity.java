package com.sinoangel.saz_alarm;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sinoangel.saz_alarm.base.MyBaseActivity;

import java.io.IOException;

/**
 * Created by Z on 2016/12/14.
 */

public class AnmiActivity extends MyBaseActivity implements View.OnClickListener {
    private MediaPlayer mPlayer, mPlayer_p4;
    private AssetFileDescriptor afd_p3, afd_p4;
    private SurfaceView sv_media;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_timering);

        sv_media = (SurfaceView) findViewById(R.id.sv_media);

        int id = getIntent().getIntExtra("ZIYUAN", 0);
        int musicId = getIntent().getIntExtra("MUSIC", 0);

        afd_p3 = getResources().openRawResourceFd(musicId);
        afd_p4 = getResources().openRawResourceFd(id);

        //音频
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mPlayer.setLooping(true);
        try {
            mPlayer.setDataSource(afd_p3.getFileDescriptor(), afd_p3.getStartOffset(), afd_p3.getLength());
            afd_p3.close();
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
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

        sv_media.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            mPlayer.stop();
            mPlayer.release();
            mPlayer_p4.stop();
            mPlayer_p4.release();
        } catch (Exception e) {
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sv_media:
                finish();
                break;
        }
    }

}
