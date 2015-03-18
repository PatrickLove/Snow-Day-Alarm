package com.patricklove.snowdayalarm.alarmTools.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.patricklove.snowdayalarm.activities.RefreshStatesTask;
import com.patricklove.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import com.patricklove.snowdayalarm.database.AlarmTemplateInterface;
import com.patricklove.snowdayalarm.database.CleanupJob;
import com.patricklove.snowdayalarm.database.DailyAlarmInterface;
import com.patricklove.snowdayalarm.database.models.AlarmTemplate;

import java.util.List;

public class BootReceiver extends WakefulBroadcastReceiver {
    private static String LOG_TAG = "BootReceiver";
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(LOG_TAG, "Boot intent received");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(LOG_TAG, "Running Boot Setup");
                DailyAlarmInterface dailyAlarmInterface = new DailyAlarmInterface(context);
                dailyAlarmInterface.open();
                dailyAlarmInterface.delete(null);
                dailyAlarmInterface.close();
                AlarmTemplateInterface alarmTemplateInterface = new AlarmTemplateInterface(context);
                alarmTemplateInterface.open();
                List<AlarmTemplate> templates = alarmTemplateInterface.getAllEnabled();
                alarmTemplateInterface.close();
                AlarmScheduler scheduler = new AlarmScheduler(context);
                scheduler.open();
                for(AlarmTemplate t : templates){
                    scheduler.scheduleAsNew(t);
                }
                scheduler.close();
                CleanupJob.schedule(context);
                new RefreshStatesTask(context).execConcurrent();
            }
        }).start();
    }
}
