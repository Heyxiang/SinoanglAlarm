package com.sinoangel.sazalarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.sinoangel.sazalarm.base.MyBaseActivity;
import com.sinoangel.sazalarm.dialog.DialogAlarmUtils;

public class AboutActivity extends MyBaseActivity implements View.OnClickListener {

    private View tv_yinsi, btn_monster_class, btn_monster_camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tv_yinsi = findViewById(R.id.tv_yinsi);
        btn_monster_class = findViewById(R.id.btn_monster_class);
        btn_monster_camera = findViewById(R.id.btn_monster_camera);

        tv_yinsi.setOnClickListener(this);
        btn_monster_class.setOnClickListener(this);
        btn_monster_camera.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_yinsi:
                DialogAlarmUtils.showYSDialog(AboutActivity.this);
                break;
            case R.id.btn_monster_class:
                downApk("MonsterClass.apk",
                        "http://cn.api.store.sinoangel.cn/apk/apk/monster_classroom_v.apk");
                break;
            case R.id.btn_monster_camera:
                downApk("MonsterCamera.apk",
                        "http://cn.api.store.sinoangel.cn/apk/apk/monstercamera_normal_v_china.apk");
                break;
//            case R.id.btn_monster_alarm:
//                downApk(SinoAlarm.apk,MyApplication.getInstance().getExternalFilesDir("") + "/tool/SinoAlarm.apk",
//                        "http://cn.api.store.sinoangel.cn/apk/apk/monster_clock_v.apk");
//                break;
        }
    }


    private void downApk(String name, String url) {
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("NAME", name);
        intent.putExtra("URL", url);
        startService(intent);

//        File file = new File(path);
//        if (file.exists()) {
//            if (!PackageUtils.installNormal(path))
//                AlarmUtils.showToast(getString(R.string.download));
//        } else {
//
//            DownloadManager.Request request;
//            try {
//                request = new DownloadManager.Request(Uri.parse(url));
//            } catch (Exception e) {
//                e.printStackTrace();
//                return;
//            }
//
//            request.setTitle(name);
//
//            //在通知栏显示下载进度
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//                request.allowScanningByMediaScanner();
//                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            }
//
//            request.setMimeType("application/vnd.android.package-archive");
//
//            //设置网络下载环境为wifi
//            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
//
//            //设置保存下载apk保存路径
//            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name + ".apk");
//
//            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//            //进入下载队列
//            downloadManager.enqueue(request);
//            AlarmUtils.showToast(getString(R.string.download));
//        }
    }

}
