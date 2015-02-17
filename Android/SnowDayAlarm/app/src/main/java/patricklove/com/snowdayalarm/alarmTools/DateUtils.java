package patricklove.com.snowdayalarm.alarmTools;

import java.util.Calendar;

/**
 * Created by Patrick Love on 2/16/2015.
 */
public class DateUtils {

    public static long MILLIS_PER_DAY = 1000*60*60*24;

    public static Calendar getToday(){
        return stripTime(getNow());
    }

    public static Calendar getNow(){
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(System.currentTimeMillis());
        return now;
    }

    public static long getTimeSinceMidnight(Calendar initial){
        return initial.getTimeInMillis() % MILLIS_PER_DAY;
    }

    public static Calendar stripTime(Calendar initial){
        long currentMillis = initial.getTimeInMillis();
        long extraMillis = currentMillis % MILLIS_PER_DAY;
        initial.setTimeInMillis(currentMillis-extraMillis);
        return initial;
    }

    public static Calendar getDateTimeToday(Calendar time){
        return dateTime(getToday(), time);
    }

    public static Calendar dateTime(Calendar date, Calendar time){
        long totalMillis = stripTime(date).getTimeInMillis() + getTimeSinceMidnight(time);
        date.setTimeInMillis(totalMillis);
        return date;
    }

    /**
     * Returns the SQL WHERE clause to search a given data column for any time on the date given.
     * The string returned is in parentheses so it will not interfere with other logical operations in a longer where clause
     * @param day Day (with any time) to search for
     * @param columnName Column name in the database where long representations of dates are stored
     * @return SQL WHERE clause
     */
    public static String getSearchStringForDay(Calendar day, String columnName){
        long lowerBound = stripTime(day).getTimeInMillis();
        long upperBound = lowerBound + MILLIS_PER_DAY;
        return "(" + columnName + ">=" + lowerBound + " AND " + columnName + "<" + upperBound + ")";
    }
}
