package patricklove.com.snowdayalarm.activities;

import android.content.Context;
import android.os.AsyncTask;

import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.database.SpecialDayInterface;
import patricklove.com.snowdayalarm.twitter.DayState;
import patricklove.com.snowdayalarm.twitter.TwitterAnalysisBridge;
import patricklove.com.snowdayalarm.utils.DateUtils;

/**
 * Created by Patrick Love on 3/2/2015.
 */
public class RefreshStatesTask extends AsyncTask<Object, Object, Boolean> {

    private MainActivity parentAct;
    private Context context;

    public RefreshStatesTask(Context c){
        this.context = c;
    }

    public RefreshStatesTask(MainActivity act){
        this.parentAct = act;
        this.context = act.getApplicationContext();
    }

    @Override
    protected Boolean doInBackground(Object[] params) {
        execConcurrent();
        return null;
    }

    public void execConcurrent() {
        TwitterAnalysisBridge twitterBridge = new TwitterAnalysisBridge(context);
        twitterBridge.updateSpecialDays();
        SpecialDayInterface dbLookup = new SpecialDayInterface(context);
        dbLookup.open();
        DayState state = dbLookup.getStateForDay(DateUtils.getNow());
        dbLookup.close();
        AlarmScheduler scheduler = new AlarmScheduler(context);
        scheduler.open();
        scheduler.updateTodaysAlarms(state);
        scheduler.close();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(parentAct != null){
            parentAct.refreshAllLists();
        }
    }
}