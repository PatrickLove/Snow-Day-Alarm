package patricklove.com.snowdayalarm.alarmTools.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import patricklove.com.snowdayalarm.database.DailyAlarmInterface;
import patricklove.com.snowdayalarm.database.SnowDayDatabase;
import patricklove.com.snowdayalarm.database.models.AlarmTemplate;
import patricklove.com.snowdayalarm.database.models.DailyAlarm;
import patricklove.com.snowdayalarm.twitter.DayState;
import patricklove.com.snowdayalarm.utils.DateUtils;

/**
 * Created by Patrick Love on 2/17/2015.
 */
public class AlarmScheduler {

    private static String LOG_TAG = "AlarmScheduler";
    public static final String EXTRA_ALARM_ID = "alarm.id";
    public static String INTENT_TRIGGER_ALARM = "com.patricklove.snowdayalarm.triggerAlarm";

    private Context context;
    private long createTime;
    private DailyAlarmInterface dbHelper;
    private AlarmManager manager;

    public AlarmScheduler(Context c){
        this.createTime = System.currentTimeMillis();
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

    public void updatePast(){
        List<DailyAlarm> toUpdate = dbHelper.query(SnowDayDatabase.COLUMN_ALARM_TIME + "<=" + createTime);
        for(DailyAlarm alarm : toUpdate){
            scheduleNextAlarm(alarm.getAssociatedAlarm());
        }
    }

    public void scheduleNextAlarm(AlarmTemplate t){
        DailyAlarm next = t.generateNextAlarm();
        next.saveIfNew(dbHelper);
        schedule(next); //schedule intent
    }

    public void scheduleFirstAlarm(AlarmTemplate t) {
        DailyAlarm next = t.generateFirstAlarm();
        next.saveIfNew(dbHelper);
        schedule(next); //schedule intent

    }

    public void updateTodaysAlarms(DayState state){
        List<DailyAlarm> alarms = dbHelper.getForDay(DateUtils.getNow());
        for(DailyAlarm alarm : alarms){
            alarm.updateActionTime(state);
            alarm.updateDB(dbHelper);
            schedule(alarm);
        }
    }

    public void scheduleAllFuture(){
        List<DailyAlarm> toSchedule = dbHelper.query(SnowDayDatabase.COLUMN_ALARM_TIME + ">=" + createTime);
        for(DailyAlarm alarm : toSchedule){
            scheduleNextAlarm(alarm.getAssociatedAlarm());
        }
    }

    public void scheduleDependents(AlarmTemplate t){
        List<DailyAlarm> toSchedule = dbHelper.query(SnowDayDatabase.COLUMN_ASSOCIATED_ALARM + "=" + t.getId());
        for(DailyAlarm alarm : toSchedule){
            schedule(alarm);
        }
    }

    public boolean schedule(DailyAlarm alarm){
        if(!alarm.isCancelled() && !alarm.isPast()) {
            Intent broadcastIntent = alarm.getTriggerIntent(context);
            Log.i(LOG_TAG, "Scheduling " + alarm.getName() + " to trigger at " + alarm.getTriggerTime().toString());
            PendingIntent action = PendingIntent.getService(context, (int) alarm.getId(), broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            manager.set(AlarmManager.RTC_WAKEUP, alarm.getTriggerTime().getTime(), action);
            return true;
        }
        return false;
    }
}
