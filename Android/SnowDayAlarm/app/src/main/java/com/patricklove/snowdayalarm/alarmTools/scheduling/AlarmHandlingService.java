package com.patricklove.snowdayalarm.alarmTools.scheduling;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.util.Log;

import com.patricklove.snowdayalarm.activities.RingingActivity;
import com.patricklove.snowdayalarm.alarmTools.AlarmAction;
import com.patricklove.snowdayalarm.database.DailyAlarmInterface;
import com.patricklove.snowdayalarm.database.SnowDayDatabase;
import com.patricklove.snowdayalarm.database.SpecialDayInterface;
import com.patricklove.snowdayalarm.database.models.AlarmTemplate;
import com.patricklove.snowdayalarm.database.models.DailyAlarm;
import com.patricklove.snowdayalarm.twitter.DayState;
import com.patricklove.snowdayalarm.twitter.TwitterAnalysisBridge;
import com.patricklove.snowdayalarm.utils.DateUtils;
import com.patricklove.snowdayalarm.utils.FileUtils;

import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AlarmHandlingService extends IntentService {

    private static final String LOG_TAG = "AlarmHandler";
    private static final String WAKE_LOCK_TAG = "Alarm_Wake_Lock";
    public static final String EXTRA_WARNING_DATE = "warning.date";
    public static final String EXTRA_DAY_STATE = "day.state";
    public static final String EXTRA_ALARM_NAME = "alarm_name";
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
                final AlarmAction actionWhenScheduled = AlarmAction.getFromCode(intent.getExtras().getInt(AlarmScheduler.EXTRA_STATE_WHEN_SCHEDULED));
                AlarmHandler handler = new AlarmHandler(this.getApplicationContext(), actionWhenScheduled);
                if(handler.initialize(alarmId)) {
                    handler.handleAlarm();
                }
            }
        }
        wakeLock.release();
    }

    private class AlarmHandler{

        private AlarmAction scheduledAction;
        private DailyAlarm alarm;
        private DailyAlarmInterface dbHelper;
        private AlarmScheduler scheduler;
        private Context context;
        private Date warnLastUpdate = null;

        public AlarmHandler(Context c, AlarmAction scheduledAction){
            context = c;
            dbHelper = new DailyAlarmInterface(c);
            scheduler = new AlarmScheduler(c);
            this.scheduledAction = scheduledAction;
        }

        public boolean initialize(long alarmId) {
            Log.d(LOG_TAG, "Initializing alarm of id " + alarmId + " for firing");
            dbHelper.open();
            List<DailyAlarm> alarms = dbHelper.query(SnowDayDatabase.idEquals(alarmId));
            dbHelper.close();
            if(alarms.size() == 0){
                return false;
            }
            alarm = alarms.get(0);
            return true;
        }

        public void handleAlarm() {
            //TODO implement alarm handling
            Log.i(LOG_TAG, "Checking " + alarm.getName());

            DayState currentState = retrieveCurrentDayState();

            if(alarm.shouldTrigger(currentState, scheduledAction)){
                Log.i(LOG_TAG, "Firing " + alarm.getName() + " for state " + currentState.toString());
                startAlarmActivity(currentState);
                scheduleNextAlarm();
            }
            else{
                Log.i(LOG_TAG, "Not firing " + alarm.getName() + " for state " + currentState.toString());
            }
            scheduler.open();
            scheduler.updateTodaysAlarms(currentState);
            scheduler.close();
        }

        private DayState retrieveCurrentDayState(){
            Log.d(LOG_TAG, "Retrieving tweets");
            TwitterAnalysisBridge twitterComp = new TwitterAnalysisBridge(context);
            if(twitterComp.updateSpecialDays() == -1){
                warnLastUpdate = new FileUtils(context).readLastUpdate();
            }
            SpecialDayInterface helper = new SpecialDayInterface(context);
            helper.open();
            DayState ret =  helper.getStateForDay(DateUtils.getNow());
            helper.close();
            return ret;
        }

        private void scheduleAgain(){
            scheduler.open();
            scheduler.schedule(alarm);
            scheduler.close();
        }

        private void scheduleNextAlarm() {
            scheduler.open();
            scheduler.scheduleNextAlarm(alarm.getAssociatedAlarm());
            scheduler.close();
        }

        private void startAlarmActivity(DayState currentState){
            Intent dialogIntent = new Intent(getBaseContext(), RingingActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if(warnLastUpdate != null){
                dialogIntent.putExtra(EXTRA_WARNING_DATE, warnLastUpdate.getTime());
            }
            dialogIntent.putExtra(EXTRA_DAY_STATE, currentState.code);
            dialogIntent.putExtra(EXTRA_ALARM_NAME, alarm.getName());
            getApplication().startActivity(dialogIntent);
        }

    }
}
