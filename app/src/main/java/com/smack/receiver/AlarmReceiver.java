package com.smack.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.smack.service.SmackPushService;

import java.util.List;

import static android.content.Context.ALARM_SERVICE;

/**
 *
 * @author MyPC
 * @date 2018/6/6
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if ( info != null && info.isAvailable()){
            switch (intent.getAction()){
                case Intent.ACTION_USER_PRESENT:
                case Intent.ACTION_PACKAGE_ADDED:
                case Intent.ACTION_PACKAGE_REMOVED:
                    startServiceRepeated(context);
                    break;
                default:
                    break;
            }
        }
    }

    private void startServiceRepeated(Context context) {
        Intent intent = getSmackPushServiceIntent(context);
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        PendingIntent mPendingIntent = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);
        long now = System.currentTimeMillis();
        mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 20 * 1000, mPendingIntent);
    }

    private Intent getSmackPushServiceIntent(Context context) {
        Intent intent = new Intent(SmackPushService.class.getName());
        intent.setPackage(context.getPackageName());

        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServices(intent, 0);
        if (resolveInfos != null) {
            for (ResolveInfo info : resolveInfos) {
                Intent i = new Intent();
                i.setClassName(info.serviceInfo.packageName, info.serviceInfo.name);
                return i;
            }
        }
        return null;
    }
}
