package patricklove.com.snowdayalarm.alarmTools.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmScheduler scheduler = new AlarmScheduler(context);
        scheduler.scheduleAll();
    }
}
