package com.sinoangel.saz_alarm;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.exception.DbException;
import com.sinoangel.saz_alarm.adapter.AlarmAdapter;
import com.sinoangel.saz_alarm.base.MyBaseActivity;
import com.sinoangel.saz_alarm.bean.AlarmBean;
import com.sinoangel.saz_alarm.dialog.DialogAlarmUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Z on 2016/12/12.
 */

public class AlarmActivity extends MyBaseActivity implements View.OnClickListener {

    private ZRecyclerView rv_list;
    private AlarmAdapter aa;
    private ImageView iv_add_timer, iv_add_alarm;
    private View iv_timer, iv_alarm;
    private List<AlarmBean> alab;
    private ImageView iv_bulr;
    private View ll_noDate;
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    aa.notifyDataSetChanged();
                }
            });
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        rv_list = (ZRecyclerView) findViewById(R.id.rv_list);
        iv_timer = findViewById(R.id.iv_timer);
        iv_add_timer = (ImageView) findViewById(R.id.iv_add_timer);
        iv_add_alarm = (ImageView) findViewById(R.id.iv_add_alarm);
        iv_alarm = findViewById(R.id.iv_alarm);
        ll_noDate = findViewById(R.id.ll_noDate);
        iv_bulr = (ImageView) findViewById(R.id.iv_bulr);

        LinearLayoutManager rlm = new LinearLayoutManager(this);
        rlm.setOrientation(LinearLayoutManager.VERTICAL);
        rv_list.setLayoutManager(rlm);

        iv_add_timer.setOnClickListener(this);
        iv_add_alarm.setOnClickListener(this);
        iv_alarm.setOnClickListener(this);
        iv_timer.setOnClickListener(this);

        aa = new AlarmAdapter();
        rv_list.setAdapter(aa);

        rv_list.setOnItemClick(new ZRecyclerView.OnItenOnClickIF() {

            @Override
            public void onHead(int postion) {
                final AlarmBean ab = alab.get(postion);
                if (ab.getType() == AlarmBean.ALARM_JISHIQI) {
                    DialogAlarmUtils.showYNDialog(AlarmActivity.this, R.string.delete_my_tag_message, new DialogAlarmUtils.OnClickDialogLintener() {
                        @Override
                        public void onOk() {
                            try {
                                AlarmUtils.getDbUtisl().delete(ab);
                                AlarmUtils.getAU().canelAlarm(ab);
                                alab.remove(ab);
                                aa.notifyDataSetChanged();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onClose() {
                        }
                    });
                    aa.notifyDataSetChanged();
                } else {
                    try {
                        if (ab.getStatus() == AlarmBean.STATUS_ON || ab.getStatus() == AlarmBean.STATUS_SHEP) {
                            ab.setStatus(AlarmBean.STATUS_PUASE);
                            AlarmUtils.getAU().canelAlarm(ab);
                        } else {
                            ab.setStatus(AlarmBean.STATUS_ON);
                            AlarmUtils.getAU().satrtAlarm(ab, false);
                        }
                        AlarmUtils.getDbUtisl().saveOrUpdate(ab);
                        aa.notifyDataSetChanged();
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onDelete(final int postion) {
                DialogAlarmUtils.showYNDialog(AlarmActivity.this, R.string.delete_my_tag_message, new DialogAlarmUtils.OnClickDialogLintener() {
                    @Override
                    public void onOk() {
                        AlarmBean ab = alab.get(postion);
                        try {
                            AlarmUtils.getDbUtisl().delete(ab);
                            AlarmUtils.getAU().canelAlarm(ab);
                            alab.remove(ab);
                            aa.notifyDataSetChanged();
                            rv_list.close();
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        if (alab.size() == 0) {
                            sHView(true);
                        } else {
                            sHView(false);
                        }
                    }

                    @Override
                    public void onClose() {
                    }
                });
            }

            @Override
            public void onClickItem(int postion) {

                final AlarmBean ab = alab.get(postion);
                if (ab.getType() != AlarmBean.ALARM_JISHIQI) {
                    Intent intent = new Intent(AlarmActivity.this, AlarmSetActivity.class);
                    intent.putExtra("DATA", ab);
                    startActivity(intent);
                } else {
                    DialogAlarmUtils.showYNDialog(AlarmActivity.this, R.string.delete_my_tag_message, new DialogAlarmUtils.OnClickDialogLintener() {
                        @Override
                        public void onOk() {
                            try {
                                AlarmUtils.getDbUtisl().delete(ab);
                                AlarmUtils.getAU().canelAlarm(ab);
                                alab.remove(ab);
                                aa.notifyDataSetChanged();
                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onClose() {
                        }
                    });
                }
            }
        });

        timer.schedule(timerTask, 0, 1000);
        try {
            alab = AlarmUtils.getDbUtisl().findAll(AlarmBean.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        try {
            alab = AlarmUtils.getDbUtisl().findAll(AlarmBean.class);
            if (alab != null)
                for (AlarmBean ab : alab) {
                    if (ab.getStatus() == AlarmBean.STATUS_ON && ab.getType() != AlarmBean.ALARM_JISHIQI)
                        AlarmUtils.getAU().satrtAlarm(ab);
                }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_add_alarm:
            case R.id.iv_alarm:
                startActivity(new Intent(AlarmActivity.this, AlarmSetActivity.class));
                break;
            case R.id.iv_add_timer:
            case R.id.iv_timer:
                startActivityForResult(new Intent(AlarmActivity.this, TimerActivity.class), 0);
                break;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            iv_bulr.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refDate();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 200) {
            refDate();
        }
    }

    private void refDate() {
        try {
            alab = AlarmUtils.getDbUtisl().findAll(AlarmBean.class);
            if (alab != null && alab.size() > 0) {
                aa.setDate(alab);
                sHView(false);
            } else {
                aa.setDate(null);
                sHView(true);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void sHView(boolean flage) {
        if (flage) {
            ll_noDate.setVisibility(View.VISIBLE);
            iv_timer.setVisibility(View.GONE);
            iv_alarm.setVisibility(View.GONE);
        } else {
            ll_noDate.setVisibility(View.GONE);
            iv_timer.setVisibility(View.VISIBLE);
            iv_alarm.setVisibility(View.VISIBLE);
        }
    }
}
