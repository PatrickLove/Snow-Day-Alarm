package patricklove.com.snowdayalarm.alarmTools.scheduling;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.List;

import patricklove.com.snowdayalarm.alarmTools.AlarmTemplate;
import patricklove.com.snowdayalarm.alarmTools.DailyAlarm;
import patricklove.com.snowdayalarm.alarmTools.DateUtils;
import patricklove.com.snowdayalarm.databases.DailyAlarmInterface;
import patricklove.com.snowdayalarm.databases.SnowDayDatabase;

/**
 * Created by Patrick Love on 2/17/2015.
 */
public class AlarmScheduler {

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

    public void updatePast(){
        dbHelper.open();
        List<DailyAlarm> toUpdate = dbHelper.query(SnowDayDatabase.COLUMN_ALARM_TIME + "<=" + createTime);
        for(DailyAlarm alarm : toUpdate){
            scheduleNextAlarm(alarm);
        }
    }

    public void scheduleNextAlarm(DailyAlarm now){
        DailyAlarm next = now.getAssociatedAlarm().generateNextAlarm();
        new AlarmScheduler(context).schedule(next); //schedule intent

        dbHelper.open(); //save to database (for ui display and
        next.saveIfNew(dbHelper);
        dbHelper.close();
    }

    public void scheduleAllFuture(){
        dbHelper.open();
        List<DailyAlarm> toSchedule = dbHelper.query(SnowDayDatabase.COLUMN_ALARM_TIME + ">=" + createTime);
        dbHelper.close();
        for(DailyAlarm alarm : toSchedule){
            scheduleNextAlarm(alarm);
        }
    }

    public void schedule(DailyAlarm alarm){
        Intent broadcastIntent = alarm.getTriggerIntent(context);
        PendingIntent action = PendingIntent.getService(context, (int) alarm.getId(), broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.set(AlarmManager.RTC_WAKEUP, alarm.getTriggerTime().getTimeInMillis(), action);
    }
}
