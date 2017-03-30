package com.sinoangel.saz_alarm.bean;

import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NoAutoIncrement;

import java.io.Serializable;

/**
 * Created by Z on 2016/12/14.
 */

public class AlarmBean implements Serializable {

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
}
