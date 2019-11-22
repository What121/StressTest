package com.bestom.stresstest.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

    public static long getCurTime(){

//        return  System.currentTimeMillis()/1000;
        return  System.currentTimeMillis();
    }

    public static String timestampToDateStr(Long timestamp) {
        Date date = new Date(timestamp);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = dateFormat.format(date);
        return format;
    }

    public static String getCurDate(){
        Date date = new Date(getCurTime());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String format = dateFormat.format(date);
        return format;
    }



}
