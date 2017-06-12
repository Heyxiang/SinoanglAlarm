package com.sinoangel.sazalarm;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.sinoangel.sazalarm.base.MyApplication;

import java.io.File;

public class PackageUtils {

    /**
     * install package normal by system intent
     *
     * @param filePath file path of package
     * @return whether apk exist
     */
    public static boolean installNormal(String filePath) {
        boolean isFull = isFull(filePath);
        if (isFull) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse("file://" + filePath),
                    "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getInstance().startActivity(i);
        } else {
            File file = new File(filePath);
            if (file.exists())
                file.delete();
        }
        return true;
    }

    /**
     * 方法说明：判断下载的apk的完整性
     *
     * @param apkPath
     * @return
     */
    public static boolean isFull(String apkPath) {
        if (apkPath == null) {
            return false;
        }
        PackageManager pm = MyApplication.getInstance().getPackageManager();
        PackageInfo pakinfo = pm.getPackageArchiveInfo(apkPath,
                PackageManager.GET_ACTIVITIES);
        if (pakinfo == null) {
            return false;
        }
        String versionName = null;
        String appName = null;
        String pakName = null;
        if (pakinfo != null) {
            ApplicationInfo appinfo = pakinfo.applicationInfo;
            versionName = pakinfo.versionName;
            appName = (String) pm.getApplicationLabel(appinfo);
            pakName = appinfo.packageName;

        }
        if (TextUtils.isEmpty(versionName) || TextUtils.isEmpty(appName)
                || TextUtils.isEmpty(pakName)) {
            return false;
        }
        return true;
    }

}
