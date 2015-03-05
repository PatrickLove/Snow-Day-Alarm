package patricklove.com.snowdayalarm.database.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import java.text.DateFormat;
import java.util.Date;

import patricklove.com.snowdayalarm.R;
import patricklove.com.snowdayalarm.alarmTools.AlarmAction;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmHandlingService;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.database.DailyAlarmInterface;
import patricklove.com.snowdayalarm.database.SnowDayDatabase;
import patricklove.com.snowdayalarm.twitter.DayState;
import patricklove.com.snowdayalarm.utils.DateUtils;

public class DailyAlarm {

    private static final String LOG_TAG = "DailyAlarm";
    public static final long CANCEL_TIME = -1;
    private long id = -1;

    private String name;
    private Date triggerDate;
    private long triggerTime;
    private AlarmAction state;
    private AlarmTemplate associatedAlarm;

    public long getId() {
        return id;
    }

    public Date getTriggerDate() {
        return triggerDate;
    }

    public long getTriggerTime() {
        return triggerTime;
    }

    public Date getCombinedTime(){
        return new Date(triggerDate.getTime() + triggerTime);
    }

    public AlarmAction getState() {
        return state;
    }

    public AlarmTemplate getAssociatedAlarm() {
        return associatedAlarm;
    }

    public String getName() {
        return name;
    }

    public DailyAlarm(String name, Date date, long time, AlarmAction state, AlarmTemplate alarm){
        this.name = name;
        this.state = state;
        this.triggerDate = DateUtils.stripTime(date);
        this.triggerTime = time;
        this.associatedAlarm = alarm;
    }

    public DailyAlarm(long id, String name, Date date, long time, AlarmAction state, AlarmTemplate alarm){
        this(name, date, time, state, alarm);
        this.id = id;
    }

    public void updateActionTime(DayState state){
        AlarmAction action = associatedAlarm.getAction(state);
        if(action == AlarmAction.DISABLE){
            this.triggerTime = CANCEL_TIME;
        }
        if(action == AlarmAction.DELAY_2_HR){
            this.triggerTime = associatedAlarm.getTime() + 2 * DateUtils.MILLIS_PER_HOUR;
        }
        this.state = action;
    }

    public void save(DailyAlarmInterface helper){
        this.id = helper.add(this);
        Log.i(LOG_TAG, this.name +  " saved to database");
    }

    public boolean saveIfNew(DailyAlarmInterface dbHelper) {
        if(dbHelper.query(SnowDayDatabase.idEquals(this.id)).size() == 0){
            save(dbHelper);
            return true;
        }
        return false;
    }

    public void updateDB(DailyAlarmInterface dbHelper) {
        if(!saveIfNew(dbHelper)){
            dbHelper.update(this);
            Log.i(LOG_TAG, "Entry for " + this.name + " updated");
        }
    }

    public boolean shouldTrigger(DayState state){
        return associatedAlarm.getAction(state).atOrBefore(this.state);
    }

    public boolean isCancelled(){
        return state == AlarmAction.DISABLE;
    }

    public Intent getTriggerIntent(Context c){
        Intent ret = new Intent(c, AlarmHandlingService.class);
        ret.setAction(AlarmScheduler.INTENT_TRIGGER_ALARM);
        ret.putExtra(AlarmScheduler.EXTRA_ALARM_ID, this.id);
        return ret;
    }

    public boolean isPast() {
        return this.getCombinedTime().before(DateUtils.getNow());
    }

    public int getStatusColorID() {
        if(state == AlarmAction.NO_CHANGE){
            return R.color.state_on_time;
        }
        if(state == AlarmAction.DELAY_2_HR){
            return R.color.state_delay;
        }
        if(state == AlarmAction.DISABLE){
            return R.color.state_cancel;
        }
        return Color.WHITE;
    }

    public String getTimeString() {
        if(!isCancelled()) {
            return DateUtils.formatMilliTime(triggerTime);
        }
        return "--:-- NA";
    }
}
