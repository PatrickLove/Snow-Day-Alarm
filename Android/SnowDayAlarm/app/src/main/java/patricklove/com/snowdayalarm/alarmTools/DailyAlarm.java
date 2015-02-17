package patricklove.com.snowdayalarm.alarmTools;

import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmHandlingService;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.databases.DailyAlarmInterface;
import patricklove.com.snowdayalarm.databases.SnowDayDatabase;
import twitter.DayState;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class DailyAlarm {

    private long id;

    public long getId() {
        return id;
    }

    public Calendar getTriggerTime() {
        return triggerTime;
    }

    public AlarmAction getState() {
        return state;
    }

    public AlarmTemplate getAssociatedAlarm() {
        return associatedAlarm;
    }

    private Calendar triggerTime;
    private AlarmAction state;
    private AlarmTemplate associatedAlarm;

    public DailyAlarm(long id, Calendar time, AlarmAction state, AlarmTemplate alarm){
        this.id = id;
        this.state = state;
        this.triggerTime = time;
        this.associatedAlarm = alarm;
    }

    public Calendar updateActionTime(DayState state){
        AlarmAction action = associatedAlarm.getAction(state);
        if(action == AlarmAction.DISABLE){
            return null;
        }
        if(action == AlarmAction.DELAY_2_HR){
            Calendar ret = associatedAlarm.getTime();
            ret.add(Calendar.HOUR, 2);
            return ret;
        }
        return associatedAlarm.getTime();
    }

    public void save(DailyAlarmInterface helper){
        helper.add(this);
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
}
