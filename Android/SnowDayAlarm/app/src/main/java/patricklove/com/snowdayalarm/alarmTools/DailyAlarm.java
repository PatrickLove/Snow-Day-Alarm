package patricklove.com.snowdayalarm.alarmTools;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmHandlingService;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.databases.DailyAlarmInterface;
import patricklove.com.snowdayalarm.databases.SnowDayDatabase;
import patricklove.com.snowdayalarm.twitter.DayState;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class DailyAlarm {

    private static final String LOG_TAG = "DailyAlarm";
    private long id;

    public long getId() {
        return id;
    }

    public Date getTriggerTime() {
        return triggerTime;
    }

    public AlarmAction getState() {
        return state;
    }

    public AlarmTemplate getAssociatedAlarm() {
        return associatedAlarm;
    }

    private Date triggerTime;
    private AlarmAction state;
    private AlarmTemplate associatedAlarm;

    public DailyAlarm(Date time, AlarmAction state, AlarmTemplate alarm){
        this.state = state;
        this.triggerTime = time;
        this.associatedAlarm = alarm;
    }

    public DailyAlarm(long id, Date time, AlarmAction state, AlarmTemplate alarm){
        this(time, state, alarm);
        this.id = id;
    }

    public void updateActionTime(DayState state){
        AlarmAction action = associatedAlarm.getAction(state);
        if(action == AlarmAction.DISABLE){
            this.triggerTime = null;
        }
        if(action == AlarmAction.DELAY_2_HR){
            Calendar ret = DateUtils.dateToCal(associatedAlarm.getTime());
            ret.add(Calendar.HOUR, 2);
            this.triggerTime = ret.getTime();
        }
        this.state = action;
    }

    public void save(DailyAlarmInterface helper){
        this.id = helper.add(this);
        Log.i(LOG_TAG, "Alarm of id " + this.id + " saved to database");
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
            Log.i(LOG_TAG, "Entry for alarm of id " + this.id + " updated");
        }
    }

    public boolean shouldTrigger(DayState state){
        return associatedAlarm.getAction(state).atOrBefore(this.state);
    }

    public boolean isCancelled(){
        return triggerTime == null;
    }

    public Intent getTriggerIntent(Context c){
        Intent ret = new Intent(c, AlarmHandlingService.class);
        ret.setAction(AlarmScheduler.INTENT_TRIGGER_ALARM);
        ret.putExtra(AlarmScheduler.EXTRA_ALARM_ID, this.id);
        return ret;
    }

    public boolean isPast() {
        return this.triggerTime.before(DateUtils.getNow());
    }
}
