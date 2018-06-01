package com.smack.utils;

import android.content.Context;
import android.content.Intent;

import com.smack.service.BadgeIntentService;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * 与BadgeIntentService配合使用
 *
 * @author MyPC
 * @date 2018/6/1
 */

public class ShortcutBadgerUtil {

    private static int notificationId = 0;

    public static void startBadgeService(Context context,int badgeCount){
        notificationId++;
        Intent intent = new Intent();
        intent.setClass(context, BadgeIntentService.class);
        intent.putExtra("badgeCount", badgeCount);
        intent.putExtra("notificationId",notificationId);
        context.startService(intent);
    }

    public static void removeBadge(Context context){
        boolean success = ShortcutBadger.removeCount(context);
    }
}
