package com.sinoangel.sazalarm.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;
import com.lidroid.xutils.exception.DbException;
import com.sinoangel.sazalarm.AlarmUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Z on 2016/12/14.
 */

public class AlarmBean implements Parcelable {

    public static final int ALARM_NZ_XUNHUAN = 1;
    public static final int ALARM_NZ_DANCI = 2;
    public static final int ALARM_JISHIQI = 3;

    public static final int STATUS_ON = 1;
    public static final int STATUS_PUASE = 2;
    public static final int STATUS_OFF = 3;
    public static final int STATUS_SHEP = 4;

    @Id
    @NoAutoIncrement
    private long id;
    private long time;
    private boolean isZD;//震动
    private boolean isXL;//铃声
    private int vol;//音量
    private String loop;//循环
    private int headpic;
    private int musicid;
    private int status = STATUS_ON;
    private int type;//计时器 还是闹钟


    public AlarmBean() {
    }

    protected AlarmBean(Parcel in) {
        id = in.readLong();
        time = in.readLong();
        isZD = in.readByte() != 0;
        isXL = in.readByte() != 0;
        vol = in.readInt();
        loop = in.readString();
        headpic = in.readInt();
        musicid = in.readInt();
        status = in.readInt();
        type = in.readInt();
    }

    public static final Creator<AlarmBean> CREATOR = new Creator<AlarmBean>() {
        @Override
        public AlarmBean createFromParcel(Parcel in) {
            return new AlarmBean(in);
        }

        @Override
        public AlarmBean[] newArray(int size) {
            return new AlarmBean[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void checkTime() {

        Calendar newTime = Calendar.getInstance();

        Calendar oldTime = Calendar.getInstance();
        oldTime.setTimeInMillis(time);

        if (type == ALARM_NZ_XUNHUAN) {

            newTime.set(Calendar.HOUR_OF_DAY, oldTime.get(Calendar.HOUR_OF_DAY));
            newTime.set(Calendar.MINUTE, oldTime.get(Calendar.MINUTE));
            newTime.set(Calendar.SECOND, 0);

            String[] week = loop.split(",");

            while (true) {
                int index = newTime.get(Calendar.DAY_OF_WEEK) - 1;
                boolean isOn = Boolean.parseBoolean(week[index % 7]);
                if (isOn) {
                    if (newTime.getTimeInMillis() > new Date().getTime()) {
                        time = newTime.getTimeInMillis();
                        AlarmUtils.outputLog("check:" + AlarmUtils.formatLong(newTime.getTimeInMillis()));
                        AlarmUtils.getAU().satrtAlarm(this);
                        break;
                    }
                }
                newTime.add(Calendar.DAY_OF_MONTH, 1);
            }
        } else {
            if (oldTime.getTimeInMillis() < new Date().getTime()) {
                status = STATUS_OFF;
            } else {
                AlarmUtils.getAU().satrtAlarm(this);
            }
        }

        try {
            AlarmUtils.getDbUtisl().saveOrUpdate(this);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }


    public boolean isZD() {
        return isZD;
    }

    public void setZD(boolean ZD) {
        isZD = ZD;
    }

    public int getVol() {
        return vol;
    }

    public void setVol(int vol) {
        this.vol = vol;
    }

    public String getLoop() {
        return loop;
    }

    public void setLoop(String loop) {
        this.loop = loop;
    }

    public int getHeadpic() {
        return headpic;
    }

    public void setHeadpic(int headpic) {
        this.headpic = headpic;
    }

    public boolean isXL() {
        return isXL;
    }

    public void setXL(boolean XL) {
        isXL = XL;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMusicid() {
        return musicid;
    }

    public void setMusicid(int musicid) {
        this.musicid = musicid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(time);
        dest.writeByte((byte) (isZD ? 1 : 0));
        dest.writeByte((byte) (isXL ? 1 : 0));
        dest.writeInt(vol);
        dest.writeString(loop);
        dest.writeInt(headpic);
        dest.writeInt(musicid);
        dest.writeInt(status);
        dest.writeInt(type);
    }
}
