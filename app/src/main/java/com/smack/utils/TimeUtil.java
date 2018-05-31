package com.smack.utils;

import java.text.SimpleDateFormat;

/**
 * Created by MyPC on 2018/5/30.
 */

public class TimeUtil {

    public static String long2yyMMddhhmmss(long time){
        SimpleDateFormat sdf=  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(time);
    }
}
