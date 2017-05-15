package com.sinoangel.saz_alarm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sinoangel.saz_alarm.AlarmUtils;
import com.sinoangel.saz_alarm.R;
import com.sinoangel.saz_alarm.base.MyApplication;
import com.sinoangel.saz_alarm.bean.AlarmBean;
import com.sinoangel.saz_alarm.bean.AlarmTimer;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Z on 2016/12/12.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<AlarmBean> alab;
    private final String[] weekName = new String[]{"Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"};
    private int[] headlist = new int[]{R.mipmap.alarm_head6, R.mipmap.alarm_head3, R.mipmap.alarm_head2, R.mipmap.alarm_head1, R.mipmap.alarm_head4, R.mipmap.alarm_head_jishi};


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm_list, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final AlarmBean ab = alab.get(position);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ab.getTime());
        int apm = calendar.get(Calendar.AM_PM);
        if (apm == Calendar.AM) {
            holder.tv_APM.setText("AM");
            holder.iv_type.setImageResource(R.mipmap.alarm_zhengwu);
        } else {
            holder.tv_APM.setText("PM");
            holder.iv_type.setImageResource(R.mipmap.alarm_wanshang);
        }
        switch (ab.getType()) {
            case AlarmBean.ALARM_NZ_DANCI:
                holder.pb_bar.setVisibility(View.GONE);

                if (ab.getStatus() == AlarmBean.STATUS_ON) {
                    long pross = ((ab.getTime() - new Date().getTime()) / 1000);
                    holder.tv_loop.setText(setAlarmVal(pross));
                } else if (ab.getStatus() == AlarmBean.STATUS_PUASE) {
                    holder.tv_loop.setText(MyApplication.getInstance().getString(R.string.guanbi));
                } else if (ab.getStatus() == AlarmBean.STATUS_SHEP) {
                    holder.tv_loop.setText(MyApplication.getInstance().getString(R.string.xiaoshui));
                } else {
                    holder.tv_loop.setText(MyApplication.getInstance().getString(R.string.guoqi));
                }
                break;
            case AlarmBean.ALARM_NZ_XUNHUAN:
                if (ab.getStatus() == AlarmBean.STATUS_ON) {
                    String[] list = ab.getLoop().split(",");
                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < list.length; i++) {
                        if (Boolean.parseBoolean(list[i]))
                            sb.append(weekName[i] + " ");
                    }
                    if (sb.length() > 0)
                        sb.deleteCharAt(sb.length() - 1);
                    holder.tv_loop.setText(sb.toString());
                } else {
                    holder.tv_loop.setText(MyApplication.getInstance().getString(R.string.guanbi));
                }
                holder.pb_bar.setVisibility(View.GONE);

                break;
            case AlarmBean.ALARM_JISHIQI:
                holder.pb_bar.setVisibility(View.VISIBLE);
                final AlarmTimer at = AlarmUtils.getAU().getAtimer(ab);
                holder.pb_bar.setProgress(360 - at.getBaifenbi());
                holder.tv_loop.setText(setTimeVal(at.getPross()));
                break;
        }

        holder.tv_time.setText(AlarmUtils.fMLongToStr_HM(ab.getTime()));
        holder.pb_vol.setProgress(ab.getVol());

        holder.iv_header.setImageResource(headlist[ab.getHeadpic()]);
        holder.iv_delete.setTag(ab);
        holder.iv_header.setTag(ab);

        if (ab.getStatus() == AlarmBean.STATUS_PUASE || ab.getStatus() == AlarmBean.STATUS_OFF)
            holder.rl_smbox.setAlpha(0.5f);
        else
            holder.rl_smbox.setAlpha(1f);

        if (ab.isZD() && ab.isXL()) {
            holder.iv_state.setImageResource(R.mipmap.alarm_zdxl);
        } else if (ab.isZD()) {
            holder.iv_state.setImageResource(R.mipmap.alarm_zhendong);
        } else {
            holder.iv_state.setImageResource(R.mipmap.alarm_xiangling);
        }


    }

    @Override
    public int getItemCount() {
        return alab == null ? 0 : alab.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView iv_type, iv_header, iv_state, iv_delete;
        public ProgressBar pb_vol, pb_bar;
        public TextView tv_APM, tv_time, tv_loop;
        public View rl_smbox;

        public ViewHolder(View itemView) {
            super(itemView);
            rl_smbox = itemView.findViewById(R.id.rl_smbox);
            iv_type = (ImageView) itemView.findViewById(R.id.iv_type);
            iv_header = (ImageView) itemView.findViewById(R.id.iv_header);
            iv_state = (ImageView) itemView.findViewById(R.id.iv_state);
//            iv_vol = (ImageView) itemView.findViewById(R.id.iv_vol);
            iv_delete = (ImageView) itemView.findViewById(R.id.iv_delete);

            pb_vol = (ProgressBar) itemView.findViewById(R.id.pb_vol);
            pb_bar = (ProgressBar) itemView.findViewById(R.id.pb_bar);

            tv_APM = (TextView) itemView.findViewById(R.id.tv_APM);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_loop = (TextView) itemView.findViewById(R.id.tv_loop);

        }
    }

    public void setDate(List<AlarmBean> alab) {
        this.alab = alab;
        notifyDataSetChanged();
    }


    private String setAlarmVal(long pross) {
        long day = pross / 60 / 60 / 24;
        long hour = pross / 60 / 60 % 24;
        long minute = pross / 60 % 60;
        if (day != 0)
            return getFormatString(1, day, hour, minute, 0);
        else if (hour != 0)
            return getFormatString(2, day, hour, minute, 0);
        else if (minute != 0)
            return getFormatString(3, day, hour, minute, 0);
        else
            return MyApplication.getInstance().getString(R.string.xiaoyuyifen);
    }

    private String setTimeVal(long pross) {
        long hour = pross / 60 / 60 % 24;
        long minute = pross / 60 % 60;
        long second = pross % 60;
        if (hour != 0)
            return getFormatString(4, 0, hour, minute, second);
        else if (minute != 0)
            return getFormatString(5, 0, hour, minute, second);
        else
            return getFormatString(6, 0, hour, minute, second);
    }

    private String getFormatString(int flag, long day, long hour, long min, long second) {
        String shengyu = MyApplication.getInstance().getString(R.string.shengyu);
        StringBuilder stringBuilder = new StringBuilder(shengyu);
        switch (flag) {
            case 1:
                stringBuilder.append(getFormatTime(day, 1));
                stringBuilder.append(getFormatTime(hour, 2));
                break;
            case 2:
                stringBuilder.append(getFormatTime(hour, 2));
                stringBuilder.append(getFormatTime(min, 3));
                break;
            case 3:
                stringBuilder.append(getFormatTime(min, 3));
                break;

            case 4:
                stringBuilder.append(getFormatTime(hour, 2));
                stringBuilder.append(getFormatTime(min, 3));
                stringBuilder.append(getFormatTime(second, 4));
                break;
            case 5:
                stringBuilder.append(getFormatTime(min, 3));
                stringBuilder.append(getFormatTime(second, 4));
                break;
            case 6:
                stringBuilder.append(getFormatTime(second, 4));
                break;
        }

        return stringBuilder.toString();
    }

    private String getFormatTime(long time, int flage) {
        String danwei;
        if (time == 0 || time == 1)
            switch (flage) {
                case 1://天
                    danwei = getStrTime(R.string.z_day);
                    break;
                case 2://小时
                    danwei = getStrTime(R.string.z_hour);
                    break;
                case 3://分钟
                    danwei = getStrTime(R.string.z_min);
                    break;
                default:
                    danwei = getStrTime(R.string.z_second);
            }
        else
            switch (flage) {
                case 1://天
                    danwei = getStrTime(R.string.z_days);
                    break;
                case 2://小时
                    danwei = getStrTime(R.string.z_hours);
                    break;
                case 3://小时
                    danwei = getStrTime(R.string.z_mins);
                    break;
                default:
                    danwei = getStrTime(R.string.z_seconds);
            }
        return String.format(" %02d " + danwei, time);
    }

    private String getStrTime(int id) {
        return MyApplication.getInstance().getString(id);
    }
}
