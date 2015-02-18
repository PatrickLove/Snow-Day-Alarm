package patricklove.com.snowdayalarm.alarmTools;

import java.util.Calendar;

import patricklove.com.snowdayalarm.databases.AlarmTemplateInterface;
import twitter.DayState;

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
        this.time = DateUtils.createClone(time);
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

    public DailyAlarm generateNextAlarm(){
        Calendar date = DateUtils.getToday();
        do{
            date.add(Calendar.DATE, 1);
        } while(!isActiveForDate(date));
        Calendar alarm = DateUtils.dateTime(date, time);
        return new DailyAlarm(0L, alarm, AlarmAction.NO_CHANGE, this);
    }

    public void save(AlarmTemplateInterface helper){
        this.id = helper.add(this);
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
