package com.patricklove.snowdayalarm.utils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Patrick Love on 2/16/2015.
 */
public class DateUtils {

    public static long MILLIS_PER_MINUTE = 1000*60;
    public static long MILLIS_PER_HOUR = MILLIS_PER_MINUTE * 60;
    public static long MILLIS_PER_DAY = MILLIS_PER_HOUR * 24;

    public static Calendar dateToCal(Date d){
        Calendar ret = Calendar.getInstance();
        ret.setTime(d);
        return ret;
    }

    public static Date getToday(){
        return stripTime(getNow());
    }

    public static Date getNow(){
        return new Date(System.currentTimeMillis());
    }

    public static Date stripTime(Date initial){
        Calendar workingCal = dateToCal(initial);
        workingCal.set(Calendar.HOUR_OF_DAY, 0);
        workingCal.set(Calendar.MINUTE, 0);
        workingCal.set(Calendar.SECOND, 0);
        workingCal.set(Calendar.MILLISECOND, 0);
        return workingCal.getTime();
    }

    public static Date dateTime(Date date, long time){
        Calendar calDate = dateToCal(stripTime(date));
        calDate.set(Calendar.MILLISECOND, (int) time);
        return calDate.getTime();
    }

    /**
     * Returns the SQL WHERE clause to search a given data column for any time on the date given.
     * The string returned is in parentheses so it will not interfere with other logical operations in a longer where clause
     * @param day Day (with any time) to search for
     * @param columnName Column name in the database where long representations of dates are stored
     * @return SQL WHERE clause
     */
    public static String getSearchStringForDay(Date day, String columnName){
        long lowerBound = stripTime(day).getTime();
        long upperBound = lowerBound + MILLIS_PER_DAY;
        return "(" + columnName + ">=" + lowerBound + " AND " + columnName + "<" + upperBound + ")";
    }

    public static String formatMilliTime(long d){
        DateFormat formatter = DateFormat.getTimeInstance(DateFormat.SHORT);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.format(new Date(d));
    }
}
