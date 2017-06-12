package com.sinoangel.sazalarm;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Z on 2017/6/12.
 */

public class DownloadService extends Service {
    private DownloadManager dm;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long myDwonloadID = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String name = idMap.get(myDwonloadID);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(new File(getExternalFilesDir("") + "/download/" + name)),
                    "application/vnd.android.package-archive");
            startActivity(intent);

            pathList.remove(name);
            idMap.remove(myDwonloadID);
            if (pathList.size() == 0)
                stopSelf();
        }
    };

    private Map<Long, String> idMap = new HashMap<>();
    private List<String> pathList = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopSelf();
            return Service.START_STICKY;
        }
        final String url = intent.getStringExtra("URL");
        final String name = intent.getStringExtra("NAME");

        if (pathList.contains(name)) {
            AlarmUtils.showToast(getString(R.string.download));
            return Service.START_STICKY;
        }

        new File(getExternalFilesDir("") + "/download/" + name).delete();

        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalFilesDir(getApplicationContext(), Environment.DIRECTORY_DOWNLOADS, name);

        //在通知栏显示下载进度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        request.setMimeType("application/vnd.android.package-archive");

        //设置网络下载环境为wifi
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);

        idMap.put(dm.enqueue(request), name);
        pathList.add(name);

        AlarmUtils.showToast(getString(R.string.download));
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

}
