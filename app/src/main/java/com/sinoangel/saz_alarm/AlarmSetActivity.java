package com.sinoangel.saz_alarm;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sinoangel.saz_alarm.adapter.AlarmMusicAdapter;
import com.sinoangel.saz_alarm.base.MyApplication;
import com.sinoangel.saz_alarm.base.MyBaseActivity;
import com.sinoangel.saz_alarm.bean.AlarmBean;
import com.sinoangel.saz_alarm.bean.CustomDate;

import java.util.Calendar;
import java.util.Date;

import static android.app.AlarmManager.INTERVAL_DAY;

/**
 * Created by Z on 2016/12/12.
 */

public class AlarmSetActivity extends MyBaseActivity implements View.OnClickListener, CalendarCardAlarm.OnCellClickListener {
    private ImageView iv_back, iv_header, iv_selectMusic;
    private TimePicker timePicker;
    private TextView tv_spec_date, tv_muName, tv_spectime, iv_save;
    private int hour, minute;
    private SeekBar volumeBar;
    private CheckBox cb_zhendong, cb_vol;
    private boolean[] cBArr = new boolean[7];
    private CheckBox cb_mo, cb_tu, cb_we, cb_th, cb_fr, cb_sa, cb_su;
    private AlarmBean ab;
    private int[] headlist = new int[]{R.mipmap.alarm_head_set1, R.mipmap.alarm_head_set3,
            R.mipmap.alarm_head_set4, R.mipmap.alarm_head_set6, R.mipmap.alarm_head_set2};
    private int currID = 2;
    private ImageView iv_bulr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        initView();
        addLiniter();

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minu) {
                hour = hourOfDay;
                minute = minu;
            }
        });

        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);


        if (getIntent().getExtras() != null) {
            ab = (AlarmBean) getIntent().getExtras().get("DATA");
            setData();
        }

    }

    private void setData() {

        Calendar calendr = Calendar.getInstance();
        calendr.setTimeInMillis(ab.getTime());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.setMinute(calendr.get(Calendar.MINUTE));
            timePicker.setHour(calendr.get(Calendar.HOUR_OF_DAY));
        }


        setCbox(ab.getLoop());

        cb_zhendong.setChecked(ab.isZD());
        cb_vol.setChecked(ab.isXL());

        volumeBar.setProgress(ab.getVol());

        iv_header.setImageResource(headlist[ab.getHeadpic()]);

        tv_muName.setText(AlarmMusicAdapter.lsname[ab.getMusicid()]);

        musicId = ab.getMusicid();


    }

    private void addLiniter() {
        CompoundButton.OnCheckedChangeListener ccl = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    cBArr[Integer.parseInt((String) buttonView.getTag())] = true;
                } else {
                    cBArr[Integer.parseInt((String) buttonView.getTag())] = false;
                }
            }
        };

        cb_mo.setOnCheckedChangeListener(ccl);
        cb_tu.setOnCheckedChangeListener(ccl);
        cb_we.setOnCheckedChangeListener(ccl);
        cb_th.setOnCheckedChangeListener(ccl);
        cb_fr.setOnCheckedChangeListener(ccl);
        cb_sa.setOnCheckedChangeListener(ccl);
        cb_su.setOnCheckedChangeListener(ccl);

        cb_vol.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    volumeBar.setEnabled(true);
                } else {
                    volumeBar.setEnabled(false);
                    if (!cb_zhendong.isChecked())
                        cb_zhendong.setChecked(true);
                }
            }
        });

        cb_zhendong.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked)
                    if (!cb_vol.isChecked())
                        cb_vol.setChecked(true);
            }
        });

        tv_spec_date.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_save.setOnClickListener(this);
        iv_header.setOnClickListener(this);
        iv_selectMusic.setOnClickListener(this);

    }

    private void initView() {
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        tv_spec_date = (TextView) findViewById(R.id.tv_spec_date);
        tv_spectime = (TextView) findViewById(R.id.tv_spectime);
        iv_save = (TextView) findViewById(R.id.iv_save);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_header = (ImageView) findViewById(R.id.iv_header);
        volumeBar = (SeekBar) findViewById(R.id.volumeBar);
        cb_zhendong = (CheckBox) findViewById(R.id.cb_zhendong);
        cb_vol = (CheckBox) findViewById(R.id.cb_vol);
        iv_selectMusic = (ImageView) findViewById(R.id.iv_selectMusic);
        tv_muName = (TextView) findViewById(R.id.tv_muName);

        iv_bulr = (ImageView) findViewById(R.id.iv_bulr);

        cb_mo = (CheckBox) findViewById(R.id.cb_mo);
        cb_tu = (CheckBox) findViewById(R.id.cb_tu);
        cb_we = (CheckBox) findViewById(R.id.cb_we);
        cb_th = (CheckBox) findViewById(R.id.cb_th);
        cb_fr = (CheckBox) findViewById(R.id.cb_fr);
        cb_sa = (CheckBox) findViewById(R.id.cb_sa);
        cb_su = (CheckBox) findViewById(R.id.cb_su);

        Calendar calendar = Calendar.getInstance();
        dyear = calendar.get(Calendar.YEAR);
        dmonth = calendar.get(Calendar.MONTH) + 1;
        dday = calendar.get(Calendar.DAY_OF_MONTH)+1;

        tv_muName.setText(AlarmMusicAdapter.lsname[musicId]);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_save:
                Calendar cal = Calendar.getInstance();

                if (tv_spectime.getVisibility() == View.VISIBLE) {
                    cal.set(Calendar.YEAR, dyear);
                    cal.set(Calendar.MONTH, dmonth - 1);
                    cal.set(Calendar.DATE, dday);
                }

                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);

                saveBean(cal.getTimeInMillis());

                AlarmUtils.showToast(getString(R.string.addsucce));
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_header:
                Intent intent = new Intent(AlarmSetActivity.this, AlarmSelectAnmiActivity.class);
                intent.putExtra("picId", currID);
                startActivityForResult(intent, 0);
                break;
            case R.id.tv_spec_date:
                if (tv_spectime.getVisibility() == View.GONE) {
                    showdialog();
                } else {
                    tv_spectime.setVisibility(View.GONE);
                    tv_spec_date.setText("Not use specific date alarm");
                    tv_spec_date.setTextColor(ContextCompat.getColor(MyApplication.getInstance(), R.color.font_white));
                }
                break;
            case R.id.iv_ok:
                Calendar calendar = Calendar.getInstance();
                long now = calendar.getTimeInMillis();
                calendar.set(Calendar.YEAR, dyear);
                calendar.set(Calendar.MONTH, dmonth - 1);
                calendar.set(Calendar.DATE, dday);
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);
                long setime = calendar.getTimeInMillis();
                if (setime + INTERVAL_DAY > now) {
                    tv_spec_date.setText("Use specific date alarm");
                    tv_spec_date.setTextColor(ContextCompat.getColor(MyApplication.getInstance(), R.color.green_6e));
                    tv_spectime.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    String str = String.format("%d-%02d-%02d ", dyear, dmonth, dday);
                    tv_spectime.setText(str);
                } else {
                    AlarmUtils.showToast(getString(R.string.wuxiaoriqi));
                }
                break;
            case R.id.iv_close:
                dialog.dismiss();
                break;
            case R.id.iv_lift:
                cc.leftSlide();
                break;
            case R.id.iv_right:
                cc.rightSlide();
                break;
            case R.id.iv_selectMusic:
                showMusicDialog();
                break;
        }
    }

    private void saveBean(long alarmtime) {
        if (ab == null) {
            ab = new AlarmBean();
            ab.setId(new Date().getTime());
        }
        ab.setTime(alarmtime);
        ab.setHeadpic(currID);
        ab.setLoop(getLoopStr());
        ab.setVol(volumeBar.getProgress());
        ab.setXL(cb_vol.isChecked());
        ab.setZD(cb_zhendong.isChecked());
        ab.setMusicid(musicId);
        ab.setStatus(AlarmBean.STATUS_ON);
        AlarmUtils.getAU().satrtAlarm(ab, true);
    }

    private String getLoopStr() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < cBArr.length; i++) {
            sb.append(cBArr[i] + ",");
        }
        if (sb.length() > 0)
            sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private void setCbox(String str) {
        String[] list = str.split(",");
        if (list.length > 1) {
            cb_su.setChecked(Boolean.parseBoolean(list[0]));
            cb_mo.setChecked(Boolean.parseBoolean(list[1]));
            cb_tu.setChecked(Boolean.parseBoolean(list[2]));
            cb_we.setChecked(Boolean.parseBoolean(list[3]));
            cb_th.setChecked(Boolean.parseBoolean(list[4]));
            cb_fr.setChecked(Boolean.parseBoolean(list[5]));
            cb_sa.setChecked(Boolean.parseBoolean(list[6]));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            currID = data.getIntExtra("pic", 0);
            iv_header.setImageResource(headlist[currID]);

            switch (currID) {
                case 0:
                    //摇摆哥
                    musicId = 6;
                    break;
                case 1:
                    musicId = 4;
                    break;
                case 2:
                    musicId = 8;
                    break;
                case 3:
                    musicId = 10;
                    break;
                case 4:
                    musicId = 5;
                    break;
            }
            tv_muName.setText(AlarmMusicAdapter.lsname[musicId]);
        }
    }

    private TextView tv_riqi;
    private Dialog dialog;
    private CalendarCardAlarm cc;

    private void showdialog() {
        dialog = new Dialog(this, R.style.customDialog);
        dialog.setCancelable(false);
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_alarm_rili, null);
        dialog.setContentView(contentView);

        ImageView iv_ok = (ImageView) contentView.findViewById(R.id.iv_ok);
        ImageView iv_close = (ImageView) contentView.findViewById(R.id.iv_close);
        ImageView iv_lift = (ImageView) contentView.findViewById(R.id.iv_lift);
        ImageView iv_right = (ImageView) contentView.findViewById(R.id.iv_right);
        tv_riqi = (TextView) contentView.findViewById(R.id.tv_riqi);

        iv_lift.setOnClickListener(this);
        iv_right.setOnClickListener(this);
        iv_ok.setOnClickListener(this);
        iv_close.setOnClickListener(this);

        AlarmUtils.getBulrBit(getWindow(), iv_bulr);
        RelativeLayout rl = (RelativeLayout) contentView.findViewById(R.id.vp_calendar);
        CustomDate customDate = new CustomDate(dyear, dmonth, dday);
        cc = new CalendarCardAlarm(this, customDate, this);
        rl.removeAllViews();
        rl.addView(cc);
        dialog.show();
    }

    private int dyear, dmonth, dday;

    @Override
    public void clickDate(CustomDate date) {
        dyear = date.year;
        dmonth = date.month;
        dday = date.day;
    }

    @Override
    public void changeDate(CustomDate date) {
        StringBuilder str = new StringBuilder();
        str.append(date.year).append("-")
                .append((date.month < 10) ? "0" + date.month : date.month);
        tv_riqi.setText(str);
    }

//    public enum SildeDirection {
//        RIGHT, LEFT, NO_SILDE;
//    }

//    private SildeDirection mDirection;

//    private void measureDirection(int arg0) {
//
//        if (arg0 > mCurrentIndex) {
//            mDirection = SildeDirection.RIGHT;
//
//        } else if (arg0 < mCurrentIndex) {
//            mDirection = SildeDirection.LEFT;
//        }
//        mCurrentIndex = arg0;
//    }
//
//    // 更新日历视图
//    private void updateCalendarView(int arg0) {
//        mShowViews = adapter.getAllItems();
//        if (mDirection == SildeDirection.RIGHT) {
//            mShowViews[arg0 % mShowViews.length].rightSlide();
//        } else if (mDirection == SildeDirection.LEFT) {
//            mShowViews[arg0 % mShowViews.length].leftSlide();
//        }
//        mDirection = SildeDirection.NO_SILDE;
//    }

    private Dialog musicDialog;
    private int musicId = 8;

    private void showMusicDialog() {
        musicDialog = new Dialog(this, R.style.customDialog);
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.dialog_alarm_music, null);
        musicDialog.setContentView(contentView);

        RecyclerView rv_list = (RecyclerView) contentView.findViewById(R.id.rv_list);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(llm);

        final AlarmMusicAdapter ama = new AlarmMusicAdapter(musicId);
        rv_list.setAdapter(ama);

        musicDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                ama.stopMusic();
            }
        });

        ImageView iv_ok = (ImageView) contentView.findViewById(R.id.iv_ok);
        ImageView iv_close = (ImageView) contentView.findViewById(R.id.iv_close);
        iv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicId = ama.getVal();
                tv_muName.setText(AlarmMusicAdapter.lsname[musicId]);
                musicDialog.dismiss();
            }
        });
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicDialog.dismiss();
            }
        });

        AlarmUtils.getBulrBit(getWindow(), iv_bulr);
        musicDialog.show();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            iv_bulr.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.getInstance().sendAnalyticsActivity("闹钟设置页");
    }
}
