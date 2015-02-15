package patricklove.com.snowdayalarm.alarmTools;

import android.util.TimeUtils;

import java.util.Calendar;
import java.util.Date;

import twitter.DayState;
import twitter.SpecialDate;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class AlarmTemplate {

    private long id;
    private AlarmAction actionCancel;
    private AlarmAction actionDelay;
    private Calendar time;
    private boolean isMonday;

    public long getId() {
        return id;
    }

    public boolean isSunday() {
        return isSunday;
    }

    public AlarmAction getActionCancel() {
        return actionCancel;
    }

    public AlarmAction getActionDelay() {
        return actionDelay;
    }

    public boolean isMonday() {
        return isMonday;
    }

    public boolean isTuesday() {
        return isTuesday;
    }

    public boolean isWednesday() {
        return isWednesday;
    }

    public boolean isThursday() {
        return isThursday;
    }

    public boolean isFriday() {
        return isFriday;
    }

    public boolean isSaturday() {
        return isSaturday;
    }

    private boolean isTuesday;
    private boolean isWednesday;
    private boolean isThursday;
    private boolean isFriday;
    private boolean isSaturday;
    private boolean isSunday;

    public AlarmTemplate(long id, AlarmAction cancel, AlarmAction delay, Calendar time,
                         boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday){
        this.id = id;
        this.actionCancel = cancel;
        this.actionDelay = delay;
        this.time = time;
        this.isMonday = monday;
        this.isTuesday = tuesday;
        this.isWednesday = wednesday;
        this.isThursday = thursday;
        this.isFriday = friday;
        this.isSaturday = saturday;
        this.isSunday = sunday;
    }

    public boolean isActiveForDate(Calendar cal){
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        switch (weekDay){
            case Calendar.MONDAY:
                return isMonday;
            case Calendar.TUESDAY:
                return isTuesday;
            case Calendar.WEDNESDAY:
                return isWednesday;
            case Calendar.THURSDAY:
                return isThursday;
            case Calendar.FRIDAY:
                return isFriday;
            case Calendar.SATURDAY:
                return isSaturday;
            case Calendar.SUNDAY:
                return isSunday;
        }
        return false;
    }

    public DailyAlarm generateDefaultAlarm(){
        Calendar nowTime = Calendar.getInstance();
        nowTime.setTimeInMillis(System.currentTimeMillis());
        nowTime.set(Calendar.HOUR_OF_DAY, this.time.get(Calendar.HOUR_OF_DAY));
        nowTime.set(Calendar.MINUTE, this.time.get(Calendar.MINUTE));
        return new DailyAlarm(0L, this.time, AlarmAction.NO_CHANGE, this);
    }

    public Calendar getTime(){
        return time;
    }

    public AlarmAction getAction(DayState state){
        if(state == DayState.CANCELLATION){
            return actionCancel;
        }
        if(state == DayState.DELAY_2_HR){
            return actionDelay;
        }
        return AlarmAction.NO_CHANGE;
    }

}
