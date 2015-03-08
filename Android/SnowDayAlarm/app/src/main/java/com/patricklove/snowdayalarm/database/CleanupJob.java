package com.patricklove.snowdayalarm.database;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Patrick Love on 3/6/2015.
 */
public class CleanupJob extends BroadcastReceiver {
    public static final int JOB_ID = 1701;
    public static final String ACTION_CLEAN_DATABASE = "com.patricklove.database.CleanupJob.ACTION_CLEAN_DATABASE";
    private static final String LOG_TAG = "CleanupJob";

    public static void schedule(Context c){
        AlarmManager scheduler = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
        Intent toBroadcast = new Intent(CleanupJob.ACTION_CLEAN_DATABASE);
        PendingIntent intent = PendingIntent.getBroadcast(c, CleanupJob.JOB_ID, toBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
        scheduler.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 0, AlarmManager.INTERVAL_DAY, intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w(LOG_TAG, "Cleaning Databases");
        DailyAlarmInterface alarmDBInterface = new DailyAlarmInterface(context);
        alarmDBInterface.open();
        alarmDBInterface.cleanUp();
        alarmDBInterface.close();
        SpecialDayInterface specialDayInterface = new SpecialDayInterface(context);
        specialDayInterface.open();
        specialDayInterface.cleanUp();
        specialDayInterface.close();
    }
}
