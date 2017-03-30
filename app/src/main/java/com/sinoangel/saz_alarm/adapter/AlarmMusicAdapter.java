package com.sinoangel.saz_alarm.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinoangel.saz_alarm.AlarmingActivity;
import com.sinoangel.saz_alarm.R;
import com.sinoangel.saz_alarm.base.MyApplication;


/**
 * Created by Z on 2016/12/12.
 */

public class AlarmMusicAdapter extends RecyclerView.Adapter<AlarmMusicAdapter.ViewHolder> implements View.OnClickListener {

    public static final int[] lsraw = new int[]{R.raw.alarm_01, R.raw.alarm_02, R.raw.alarm_03, R.raw.alarm_04, R.raw.alarm_05, R.raw.alarm_06,
            R.raw.alarm_07, R.raw.alarm_08, R.raw.alarm_09, R.raw.alarm_10, R.raw.alarm_11, R.raw.alarm_14};
    public static final int[] lsname = new int[]{R.string.alarm_ls1, R.string.alarm_ls2, R.string.alarm_ls3, R.string.alarm_ls4, R.string.alarm_ls5,
            R.string.alarm_ls6, R.string.alarm_ls7, R.string.alarm_ls8, R.string.alarm_ls9, R.string.alarm_ls10, R.string.alarm_ls11,
            R.string.alarm_ls12, R.string.alarm_ls13, R.string.alarm_ls14,};
    private ViewHolder lastView;
    private int selectIndex;
    private MediaPlayer mp;

    public AlarmMusicAdapter(int id) {
        //获取电话通讯服务
        TelephonyManager tpm = (TelephonyManager) MyApplication.getInstance()
                .getSystemService(Context.TELEPHONY_SERVICE);
        //创建一个监听对象，监听电话状态改变事件
        tpm.listen(new MyPhoneStateListener(),
                PhoneStateListener.LISTEN_CALL_STATE);
        selectIndex = id;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm_list_music, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_name.setText(lsname[position]);
        holder.pos = position;
        holder.box.setTag(holder);

        if (position == selectIndex) {
            holder.box.setSelected(true);
            holder.iv_static.setVisibility(View.VISIBLE);
            mp = MediaPlayer.create(MyApplication.getInstance(), lsraw[8]);
            lastView = holder;
        }
    }

    @Override
    public int getItemCount() {
        return lsraw.length;
    }

    @Override
    public void onClick(View v) {
        ViewHolder vh = (ViewHolder) v.getTag();
        int musicId = lsraw[vh.pos];

        if (lastView == vh) {
            if (mp.isPlaying()) {
                mp.pause();
                vh.iv_static.setImageResource(R.mipmap.alarm_music_start);
            } else {
                mp.start();
                vh.iv_static.setImageResource(R.mipmap.alarm_music_stop);
            }
        } else {
            if (mp != null) {
                mp.stop();
            }
            mp = MediaPlayer.create(MyApplication.getInstance(), musicId);
            vh.iv_static.setImageResource(R.mipmap.alarm_music_stop);
            mp.start();
        }


        if (lastView != null) {
            lastView.box.setSelected(false);
            lastView.iv_static.setVisibility(View.INVISIBLE);
        }

        v.setSelected(true);
        vh.iv_static.setVisibility(View.VISIBLE);
        lastView = vh;


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public View box;
        private ImageView iv_static;
        public int pos;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView;
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_static = (ImageView) itemView.findViewById(R.id.iv_static);
            box.setOnClickListener(AlarmMusicAdapter.this);
        }
    }


    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public void stopMusic() {
        if (mp != null)
            mp.pause();
        if (lastView != null)
            lastView.iv_static.setImageResource(R.mipmap.alarm_music_start);
    }

    public int getVal() {
        if (lastView != null)
            return lastView.pos;
        return 0;
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: //空闲
//                    if (mp != null && !mp.isPlaying())
//                        mp.start();
                    break;
                case TelephonyManager.CALL_STATE_RINGING: //来电
                    mp.pause();
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: //摘机（正在通话中）
                    break;
            }
        }

    }

}
