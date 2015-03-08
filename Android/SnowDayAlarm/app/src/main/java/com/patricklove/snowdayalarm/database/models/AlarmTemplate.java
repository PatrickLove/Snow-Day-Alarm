package patricklove.com.snowdayalarm.database.models;

import android.content.Context;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import patricklove.com.snowdayalarm.activities.RefreshStatesTask;
import patricklove.com.snowdayalarm.alarmTools.AlarmAction;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.database.AlarmTemplateInterface;
import patricklove.com.snowdayalarm.twitter.DayState;
import patricklove.com.snowdayalarm.utils.DateUtils;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class AlarmTemplate {

    private static final String LOG_TAG = "AlarmTemplate";
    private long id = -1;
    private AlarmAction actionCancel;
    private AlarmAction actionDelay;
    private long time;
    private boolean isMonday;
    private String name;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

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

    public AlarmTemplate(String name, AlarmAction cancel, AlarmAction delay, long time,
                         boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday, boolean enabled){
        this.name = name;
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
        this.enabled = enabled;
    }

    public AlarmTemplate(long id, String name, AlarmAction cancel, AlarmAction delay, long time,
                         boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, boolean sunday, boolean enabled){
        this(name, cancel, delay, time, monday, tuesday, wednesday, thursday, friday, saturday, sunday, enabled);
        this.id = id;
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
        Calendar date = DateUtils.dateToCal(DateUtils.getNow());
        do{
            date.add(Calendar.DATE, 1);
        } while(!isActiveForDate(date));
        return new DailyAlarm(name, date.getTime(), this.time, AlarmAction.NO_CHANGE, this);
    }



    public DailyAlarm generateTodayAlarm() {
        Date now = DateUtils.getNow();
        if(this.isActiveForDate(DateUtils.dateToCal(now))){
            return new DailyAlarm(name, now, this.time, AlarmAction.NO_CHANGE, this);
        }
        return null;
    }

    public long getTime(){
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

    public String getActiveDayStr(){
        String ret = "";
        if(isMonday){
            ret += "Mon, ";
        }
        if(isTuesday){
            ret += "Tue, ";
        }
        if(isWednesday){
            ret += "Wed, ";
        }
        if(isThursday){
            ret += "Thu, ";
        }
        if(isFriday){
            ret += "Fri, ";
        }
        if(isSaturday){
            ret += "Sat, ";
        }
        if(isSunday){
            ret += "Sun, ";
        }
        if(ret.length() == 0){
            ret = "Never  ";
        }
        return ret.substring(0, ret.length()-2);
    }

    public void save(AlarmTemplateInterface dbHelp) {
        this.id = dbHelp.add(this);
        Log.i(LOG_TAG, "Added " + name + " to database");
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void updateDBEnabled(Context context) {
        AlarmTemplateInterface dbInt = new AlarmTemplateInterface(context);
        dbInt.open();
        dbInt.update(this);
        dbInt.clearDependants(this);
        dbInt.close();
        if(enabled) {
            AlarmScheduler scheduler = new AlarmScheduler(context);
            scheduler.open();
            scheduler.scheduleAsNew(this);
            scheduler.close();
            new RefreshStatesTask(context).execWithoutTwitter();
        }
    }
}
