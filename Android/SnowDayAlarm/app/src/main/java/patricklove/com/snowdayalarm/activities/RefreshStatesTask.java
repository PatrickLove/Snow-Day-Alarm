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
        TwitterAnalysisBridge twitterBridge = new TwitterAnalysisBridge(context);
        if(twitterBridge.updateSpecialDays() > 0) { //if there are new special days since the last new date was found
            SpecialDayInterface dbLookup = new SpecialDayInterface(context);
            dbLookup.open();
            DayState state = dbLookup.getStateForDay(DateUtils.getNow());
            dbLookup.close();
            AlarmScheduler scheduler = new AlarmScheduler(context);
            scheduler.open();
            scheduler.updateTodaysAlarms(state);
            scheduler.close();
            return true;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result.booleanValue() && parentAct != null){
            parentAct.refreshAllLists();
        }
    }
}