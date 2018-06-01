package com.smack.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.smack.R;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * 与ShortcutBadgerUtil配合使用
 * Created by MyPC on 2018/6/1.
 */

public class BadgeIntentService extends IntentService {

    private int notificationId = 0;

    public BadgeIntentService() {
        super("BadgeIntentService");
    }

    private NotificationManager mNotificationManager;

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            notificationId = intent.getIntExtra("notificationId",0);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int badgeCount = intent.getIntExtra("badgeCount", 0);
            NotificationCompat.Builder builder1 = new NotificationCompat.Builder(getApplicationContext());
            builder1.setContentTitle("标题");
            builder1.setContentText("内容内容内容");
            builder1.setSmallIcon(R.mipmap.ic_launcher);
            Notification notification = builder1.build();
            ShortcutBadger.applyCount(getApplicationContext(),badgeCount);
            ShortcutBadger.applyNotification(getApplicationContext(), notification, badgeCount);
            mNotificationManager.notify(notificationId, notification);
        }
    }
}
