package com.sinoangel.sazalarm;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.sinoangel.sazalarm.base.MyApplication;
import com.sinoangel.sazalarm.base.MyBaseActivity;
import com.sinoangel.sazalarm.dialog.DialogAlarmUtils;

import java.io.File;

public class AboutActivity extends MyBaseActivity implements View.OnClickListener {

    private View tv_yinsi, btn_monster_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tv_yinsi = findViewById(R.id.tv_yinsi);
        btn_monster_class = findViewById(R.id.btn_monster_class);

        tv_yinsi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_yinsi:
                DialogAlarmUtils.showYSDialog(AboutActivity.this);
                break;
            case R.id.btn_monster_class:
                break;
            case R.id.btn_monster_camera:
                break;
            case R.id.btn_monster_alarm:
                downApk(MyApplication.getInstance().getExternalFilesDir("") + "/tool/SinoAlarm.apk");
                break;
        }
    }


    private void downApk(String url) {
        File file = new File(url);
        if (file.exists()) {
            if (!PackageUtils.installNormal(file.getAbsolutePath()))
                AlarmUtils.showToast(getString(R.string.download));
        } else {
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse("http://cn.api.sinoangel.cn/apk/apk/SinoAlarm.apk");
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationUri(Uri.fromFile(file));
            downloadManager.enqueue(request);
            AlarmUtils.showToast(getString(R.string.download));

        }
    }
}
