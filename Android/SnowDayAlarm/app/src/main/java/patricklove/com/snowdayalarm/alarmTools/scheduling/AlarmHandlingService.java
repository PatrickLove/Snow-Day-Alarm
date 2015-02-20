package patricklove.com.snowdayalarm.alarmTools.scheduling;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;

import patricklove.com.snowdayalarm.alarmTools.AlarmTemplate;
import patricklove.com.snowdayalarm.alarmTools.DailyAlarm;
import patricklove.com.snowdayalarm.databases.DailyAlarmInterface;
import patricklove.com.snowdayalarm.databases.SnowDayDatabase;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AlarmHandlingService extends IntentService {

    private static final String LOG_TAG = "AlarmHandler (Service or class)";
    private static final String WAKE_LOCK_TAG = "Alarm_Wake_Lock";
    private PowerManager.WakeLock wakeLock;

    public AlarmHandlingService() {
        super("AlarmHandlingService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PowerManager power = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = power.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        wakeLock.acquire();
        if (intent != null) {
            final String action = intent.getAction();
            if(action.equals(AlarmScheduler.INTENT_TRIGGER_ALARM)){
                final long alarmId = intent.getExtras().getLong(AlarmScheduler.EXTRA_ALARM_ID);
                AlarmHandler handler = new AlarmHandler(this.getApplicationContext());
                if(handler.initialize(alarmId)) {
                    handler.handleAlarm();
                }
            }
        }
        wakeLock.release();
    }

    private class AlarmHandler{

        private AlarmTemplate template;
        private DailyAlarm alarm;
        private DailyAlarmInterface dbHelper;
        private Context context;

        public AlarmHandler(Context c){
            context = c;
            dbHelper = new DailyAlarmInterface(c);
        }

        public boolean initialize(long alarmId) {
            Log.d(LOG_TAG, "Initializing alarm of id " + alarmId);
            dbHelper.open();
            List<DailyAlarm> alarms = dbHelper.query(SnowDayDatabase.idEquals(alarmId));
            dbHelper.close();
            if(alarms.size() == 0){
                return false;
            }
            alarm = alarms.get(0);
            template = alarm.getAssociatedAlarm();
            return true;
        }

        public void handleAlarm() {
            //TODO implement alarm handling
            Log.d(LOG_TAG, "Firing alarm of id " + alarm.getId());


            //throw new UnsupportedOperationException("ALARM NOT YET IMPLEMENTED");
            /*
            DayState currentState = DayState.NO_CHANGE;

            if(alarm.shouldTrigger(currentState)){
                soundAlarm();
                scheduleNextAlarm();
            }
            else{
                alarm.updateActionTime(currentState);
                if(alarm.isCancelled()){
                    scheduleNextAlarm();
                }
                else{
                    scheduleAgain();
                }
                dbHelper.open();
                alarm.updateDB(dbHelper);
                dbHelper.close();
            }
            */
        }

        private void scheduleAgain(){
            AlarmScheduler scheduler = new AlarmScheduler(context);
            scheduler.schedule(alarm);
        }

        private void scheduleNextAlarm() {
            AlarmScheduler scheduler = new AlarmScheduler(context);
            scheduler.scheduleNextAlarm(alarm);
        }

        private void sendAlarmText(String text){
            //TODO Create launched activity (with recent tweets, etc)
        }

        private void soundAlarm(){
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
            ringtone.play();
        }
    }
}
