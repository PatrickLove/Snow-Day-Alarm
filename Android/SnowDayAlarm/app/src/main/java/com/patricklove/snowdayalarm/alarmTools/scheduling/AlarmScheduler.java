package com.patricklove.snowdayalarm.alarmTools.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.patricklove.snowdayalarm.database.DailyAlarmInterface;
import com.patricklove.snowdayalarm.database.models.AlarmTemplate;
import com.patricklove.snowdayalarm.database.models.DailyAlarm;
import com.patricklove.snowdayalarm.twitter.DayState;
import com.patricklove.snowdayalarm.utils.DateUtils;

import java.util.List;

/**
 * Created by Patrick Love on 2/17/2015.
 */
public class AlarmScheduler {

    public static final String EXTRA_STATE_WHEN_SCHEDULED = "alarm.schedulestate";
    private static String LOG_TAG = "AlarmScheduler";
    public static final String EXTRA_ALARM_ID = "alarm.id";
    public static String INTENT_TRIGGER_ALARM = "com.patricklove.snowdayalarm.triggerAlarm";

    private Context context;
    private DailyAlarmInterface dbHelper;
    private AlarmManager manager;

    public AlarmScheduler(Context c){
        this.context = c;
        this.dbHelper = new DailyAlarmInterface(c);
        this.manager = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
    }

    public void open(){
        dbHelper.open();
    }

    public void close(){
        dbHelper.close();
    }

    public void scheduleAsNew(AlarmTemplate t){
        if(t.isEnabled() && !scheduleTodayAlarm(t)){
            scheduleNextAlarm(t);
        }
    }

    public void scheduleNextAlarm(AlarmTemplate t){
        if(t.isEnabled()){
            DailyAlarm next = t.generateNextAlarm();
            next.save(dbHelper);
            schedule(next); //schedule intent
        }
    }

    public boolean scheduleTodayAlarm(AlarmTemplate t) {
        if(t.isEnabled()) {
            DailyAlarm next = t.generateTodayAlarm();
            if (next != null) {
                next.save(dbHelper);
                return schedule(next);
            }
        }
        return false;
    }

    public void updateTodaysAlarms(DayState state){
        List<DailyAlarm> alarms = dbHelper.getForDay(DateUtils.getNow());
        for(DailyAlarm alarm : alarms){
            alarm.updateActionTime(state);
            alarm.updateDB(dbHelper);
            schedule(alarm);
        }
    }

    public boolean schedule(DailyAlarm alarm){
        if(!alarm.isCancelled() && !alarm.isPast()) {
            Log.i(LOG_TAG, "Scheduling " + alarm.getName() + " to trigger at " + alarm.getCombinedTime());
            Intent broadcastIntent = alarm.getTriggerIntent(context);
            PendingIntent action = PendingIntent.getService(context, (int) alarm.getId(), broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.set(AlarmManager.RTC_WAKEUP, alarm.getCombinedTime().getTime(), action);
            return true;
        }
        return false;
    }
}
