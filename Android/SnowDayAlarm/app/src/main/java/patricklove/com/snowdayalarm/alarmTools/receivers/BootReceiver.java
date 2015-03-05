package patricklove.com.snowdayalarm.alarmTools.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.util.List;

import patricklove.com.snowdayalarm.activities.RefreshStatesTask;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.database.AlarmTemplateInterface;
import patricklove.com.snowdayalarm.database.DailyAlarmInterface;
import patricklove.com.snowdayalarm.database.models.AlarmTemplate;

public class BootReceiver extends WakefulBroadcastReceiver {
    private static String LOG_TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "Boot intent received");
        DailyAlarmInterface dailyAlarmInterface = new DailyAlarmInterface(context);
        dailyAlarmInterface.open();
        dailyAlarmInterface.delete(null);
        dailyAlarmInterface.close();
        AlarmTemplateInterface alarmTemplateInterface = new AlarmTemplateInterface(context);
        alarmTemplateInterface.open();
        List<AlarmTemplate> templates = alarmTemplateInterface.getAll();
        alarmTemplateInterface.close();
        AlarmScheduler scheduler = new AlarmScheduler(context);
        scheduler.open();
        for(AlarmTemplate t : templates){
            scheduler.scheduleAsNew(t);
        }
        scheduler.close();
        new RefreshStatesTask(context).execConcurrent();
    }
}
