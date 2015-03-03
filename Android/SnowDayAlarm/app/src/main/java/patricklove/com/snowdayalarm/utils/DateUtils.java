package patricklove.com.snowdayalarm.utils;

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

    private static long getOffset(){
        return Calendar.getInstance().getTimeZone().getRawOffset();
    }

    public static long getTimeSinceMidnight(Date initial){
        long millisSinceLocalEpoch = initial.getTime()+getOffset(); //Milliseconds since Jan 1, 1970 in the local time zone (UTC to local conversion)
        return  (millisSinceLocalEpoch%MILLIS_PER_DAY+MILLIS_PER_DAY)%MILLIS_PER_DAY; //deals with negative modulus millisSinceEpoch < MILLIS_PER_DAY
    }

    public static Date stripTime(Date initial){
        long currentMillis = initial.getTime();
        long extraMillis = getTimeSinceMidnight(initial);
        initial.setTime(currentMillis - extraMillis);
        return initial;
    }

    public static Date dateTime(Date date, long time){
        long totalMillis = stripTime(date).getTime() + time;
        return new Date(totalMillis);
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
