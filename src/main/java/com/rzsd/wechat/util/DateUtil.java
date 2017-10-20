package com.rzsd.wechat.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(Calendar.getInstance().getTimeInMillis());
    }

    public static String format(Date date) {
        return format(date, "yyyy-MM-dd");
    }

    public static String format(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String cdate = formatter.format(date);
        return cdate;
    }

    public static Date addDays(Date date, int addDays) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, addDays);
        return cal.getTime();
    }
}
