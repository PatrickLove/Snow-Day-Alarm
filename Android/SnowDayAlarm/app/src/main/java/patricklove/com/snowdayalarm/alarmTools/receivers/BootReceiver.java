package patricklove.com.snowdayalarm.alarmTools.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;

public class BootReceiver extends BroadcastReceiver {
    private static String LOG_TAG = "BootReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_TAG, "Boot intent received");
        AlarmScheduler scheduler = new AlarmScheduler(context);
        scheduler.updatePast();
        scheduler.scheduleAllFuture();
    }
}
