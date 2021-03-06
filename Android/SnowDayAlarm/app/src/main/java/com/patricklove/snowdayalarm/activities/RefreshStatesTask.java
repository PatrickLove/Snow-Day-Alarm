package com.patricklove.snowdayalarm.activities;

import android.content.Context;
import android.os.AsyncTask;

import com.patricklove.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import com.patricklove.snowdayalarm.database.SpecialDayInterface;
import com.patricklove.snowdayalarm.twitter.DayState;
import com.patricklove.snowdayalarm.twitter.TwitterAnalysisBridge;
import com.patricklove.snowdayalarm.utils.DateUtils;

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
        execWithoutTwitter();
    }

    public void execWithoutTwitter(){
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