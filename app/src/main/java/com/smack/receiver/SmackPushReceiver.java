package com.smack.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.smack.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 *
 * @author MyPC
 * @date 2018/6/4
 */

public class SmackPushReceiver extends BroadcastReceiver {

    private NotificationManager notificationManager;
    private int notificationId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        String content = "";
        switch (intent.getAction()){
            case IntentReceiver.IntentEnum.CONNECTION:
                content = "云网推送连接成功";
                break;
            case IntentReceiver.IntentEnum.REGISTRATION:
                content = "云网推送注册成功";
                break;
            case IntentReceiver.IntentEnum.AUTHENTICATED:
                content = "云网推送认证成功";
                break;
            case IntentReceiver.IntentEnum.MESSAGE_RECEIVED:
                content = intent.getStringExtra(IntentReceiver.EXTRA);
                break;
            case IntentReceiver.IntentEnum.NOTIFICATION_RECEIVED:
                content = intent.getStringExtra(IntentReceiver.EXTRA);
                notificationId = (int) (System.currentTimeMillis() / 1000);
                Intent clickIntent = new Intent(context, SmackPushReceiver.class);
                clickIntent.setAction(IntentReceiver.IntentEnum.NOTIFICATION_OPENED);
                PendingIntent pi = PendingIntent.getBroadcast(context, notificationId, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = showNotification(context,"ticker",pi,"这是标题","这是内容");
                notificationManager.notify(notificationId,notification);
                break;
            case IntentReceiver.IntentEnum.NOTIFICATION_OPENED:
                Log.d("SmackPushReceiver","用户打开了该推送！");
                break;
            default:
                break;
        }
        Log.d("SmackPushReceiver",content);
    }

    private Notification showNotification(Context context,String ticker,PendingIntent pi,String contentTitle, String contentText){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE);
        }
        Bitmap icon =  BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
        Notification notify = builder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(icon)
                .setContentTitle(contentTitle)
                .setTicker(ticker)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setAutoCancel(true)
                .setContentIntent(pi)
                .setContentText(contentText)
                .setFullScreenIntent(pi, true)
                .build();
        return notify;
    }

}
