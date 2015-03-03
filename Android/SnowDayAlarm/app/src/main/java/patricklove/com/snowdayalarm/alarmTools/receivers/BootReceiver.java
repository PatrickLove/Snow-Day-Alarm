package patricklove.com.snowdayalarm.alarmTools.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import patricklove.com.snowdayalarm.activities.RefreshStatesTask;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;

public class BootReceiver extends WakefulBroadcastReceiver {
    private static String LOG_TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "Boot intent received");
        AlarmScheduler scheduler = new AlarmScheduler(context);
        scheduler.updatePast();
        scheduler.scheduleAllFuture();
        new RefreshStatesTask(context).execute();
    }
}
