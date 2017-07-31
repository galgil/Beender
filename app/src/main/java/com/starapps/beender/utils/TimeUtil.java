package com.starapps.beender.utils;

import android.app.AlarmManager;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;



public class TimeUtil {

    private static final String TAG = "TimeUtil";

    private static DateFormat photoStamp = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private static DateFormat yearFormat = new SimpleDateFormat("yy");
    private static DateFormat fullYearFormat = new SimpleDateFormat("yyyy");

    public static String getStamp() {
        return photoStamp.format(new Date(System.currentTimeMillis()));
    }

    public static String getYear(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        Date dt = null;
        try {
            dt = photoStamp.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (dt == null) {
            return "";
        } else {
            return fullYearFormat.format(dt);
        }
    }

    public static boolean isSameDay(String dtStart, String dtEnd) {
        if (TextUtils.isEmpty(dtStart) || TextUtils.isEmpty(dtEnd)) {
            return true;
        }
        if (dtStart.equals(dtEnd)) {
            return true;
        }
        Calendar st = get(dtStart);
        Calendar end = get(dtEnd);

        long diff = end.getTimeInMillis() - st.getTimeInMillis();
        long days = diff / AlarmManager.INTERVAL_DAY;
        return days == 0 && get(st, Calendar.DAY_OF_MONTH) == get(end, Calendar.DAY_OF_MONTH);
    }

    public static int getDiffDays(String dtStart, String dtEnd) {
        if (TextUtils.isEmpty(dtStart) || TextUtils.isEmpty(dtEnd)) {
            return 1;
        }
        if (dtStart.equals(dtEnd)) {
            return 1;
        }
        Calendar st = get(dtStart);
        Calendar end = get(dtEnd);

        long diff = end.getTimeInMillis() - st.getTimeInMillis();
        long days = diff / AlarmManager.INTERVAL_DAY;
        if (days == 0 && get(st, Calendar.DAY_OF_MONTH) == get(end, Calendar.DAY_OF_MONTH)) {
            return 1;
        } else if (days == 0) {
            return 2;
        }
        return (int) days;
    }

    public static String getRange(String dtStart, String dtEnd) {
        if (TextUtils.isEmpty(dtStart) || TextUtils.isEmpty(dtEnd)) {
            return "";
        }
        if (dtStart.equals(dtEnd)) {
            return getFormatted(dtStart);
        }
        Calendar st = get(dtStart);
        Calendar end = get(dtEnd);
        int d1 = get(st, Calendar.DAY_OF_MONTH);
        int d2 = get(end, Calendar.DAY_OF_MONTH);

        int m1 = get(st, Calendar.MONTH);
        int m2 = get(end, Calendar.MONTH);

        int y1 = get(st, Calendar.YEAR);
        int y2 = get(end, Calendar.YEAR);
        if (m1 == m2 && y1 == y2 && d1 == d2) {
            return getFormatted(dtStart);
        } else if (m1 == m2 && y1 == y2) {
            return String.format(Locale.getDefault(), "%d-%d/%d/%s", d1, d2, m1, yearFormat.format(st.getTime()));
        } else {
            return dateFormat.format(st.getTime()) + "-" + dateFormat.format(end.getTime());
        }
    }

    private static int get(Calendar calendar, int field) {
        return calendar.get(field);
    }

    public static String getFormatted(String date) {
        if (TextUtils.isEmpty(date)) {
            return "";
        }
        Date dt = new Date(System.currentTimeMillis());
        try {
            dt = photoStamp.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateFormat.format(dt);
    }

    private static Calendar get(String date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (!TextUtils.isEmpty(date)) {
            try {
                calendar.setTime(photoStamp.parse(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return calendar;
    }
}
