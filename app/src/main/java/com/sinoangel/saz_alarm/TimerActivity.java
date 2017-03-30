package com.sinoangel.saz_alarm;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.sinoangel.saz_alarm.base.MyBaseActivity;
import com.sinoangel.saz_alarm.bean.AlarmBean;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Z on 2016/12/16.
 */

public class TimerActivity extends MyBaseActivity implements View.OnClickListener {
    private ZCircularSeekBar zCircularSeekBar;
    private TextView tv_time_val;
    private CheckBox rb_zdls;
    private View iv_addtime1, iv_addtime2, iv_addtime3, iv_addtime4, iv_addtime5, iv_reset;
    private TextView iv_cancel, iv_okay;
    private int time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_timer);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(lp);

        iv_addtime1 = findViewById(R.id.iv_addtime1);
        iv_addtime2 = findViewById(R.id.iv_addtime2);
        iv_addtime3 = findViewById(R.id.iv_addtime3);
        iv_addtime4 = findViewById(R.id.iv_addtime4);
        iv_addtime5 = findViewById(R.id.iv_addtime5);
        iv_reset = findViewById(R.id.iv_reset);
        iv_cancel = (TextView) findViewById(R.id.iv_cancel);
        iv_okay = (TextView) findViewById(R.id.iv_okay);

        iv_addtime1.setOnClickListener(this);
        iv_addtime2.setOnClickListener(this);
        iv_addtime3.setOnClickListener(this);
        iv_addtime4.setOnClickListener(this);
        iv_addtime5.setOnClickListener(this);

        iv_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zCircularSeekBar.setPross(0);
                time = 0;
                tv_time_val.setText(formatTime(time));
            }
        });
        iv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (time == 0) {
                    AlarmUtils.showToast(getString(R.string.notime));
                    return;
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(calendar.getTimeInMillis() + time * 60 * 1000);
                AlarmBean ab = new AlarmBean();
                ab.setId(new Date().getTime());
                ab.setHeadpic(5);
                ab.setTime(calendar.getTimeInMillis());
                ab.setVol(50);
                ab.setXL(!rb_zdls.isChecked());
                ab.setZD(rb_zdls.isChecked());
                ab.setLoop("flase");
                ab.setMusicid(0);
                ab.setType(AlarmBean.ALARM_JISHIQI);
                ab.setStatus(AlarmBean.STATUS_ON);
                AlarmUtils.getAU().satrtAlarmForTimer(ab);
                setResult(200);
                AlarmUtils.showToast(getString(R.string.addsucce));
                finish();
            }
        });


        rb_zdls = (CheckBox) findViewById(R.id.rb_zdls);
        tv_time_val = (TextView) findViewById(R.id.tv_time_val);
        zCircularSeekBar = (ZCircularSeekBar) findViewById(R.id.zCircularSeekBar);

        zCircularSeekBar.setOnSeekBarChangeListener(new ZCircularSeekBar.OnCircleSeekBarChangeListener() {
            @Override
            public void onProgressChanged(ZCircularSeekBar seekBar, double progress, boolean fromUser) {
                time = (int) progress * 5;
                tv_time_val.setText(formatTime(time));
            }
        });

        rb_zdls.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundResource(R.drawable.alarm_aimo_lszd);
                } else {
                    buttonView.setBackgroundResource(R.drawable.alarm_aimo_zdls);
                }
                AnimationDrawable AniDraw = (AnimationDrawable) buttonView.getBackground();
                AniDraw.start();
            }
        });

    }

    public String formatTime(int time) {
        long hour = time / 60;
        long minute = time % 60;
        return String.format("%02d:%02d", hour, minute);
    }

    @Override
    public void onClick(View v) {
        double i = zCircularSeekBar.getPross();
        double val, timevar;
        switch (v.getId()) {
            case R.id.iv_addtime1:
                val = i;
                timevar = 1;
                if ((time + 1) % 5 == 0)
                    val = i + 1;
                break;
            case R.id.iv_addtime2:
                val = i + 1;
                timevar = 5;
                break;
            case R.id.iv_addtime3:
                val = i + 2;
                timevar = 10;
                break;
            case R.id.iv_addtime4:
                val = i + 6;
                timevar = 30;
                break;
            default:
                val = i + 12;
                timevar = 60;
                break;
        }

        if (val > 144) {
            val = 144;
            time = 720;
        } else if (time < 720)
            time += timevar;
        tv_time_val.setText(formatTime(time));
        zCircularSeekBar.setPross(val);
    }
}
