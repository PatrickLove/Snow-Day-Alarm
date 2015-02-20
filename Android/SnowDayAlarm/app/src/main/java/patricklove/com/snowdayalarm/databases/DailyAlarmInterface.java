package patricklove.com.snowdayalarm.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import patricklove.com.snowdayalarm.alarmTools.AlarmAction;
import patricklove.com.snowdayalarm.alarmTools.AlarmTemplate;
import patricklove.com.snowdayalarm.alarmTools.DailyAlarm;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class DailyAlarmInterface {

    private static final String LOG_TAG = "DailyAlarmDBInterface";
    private SnowDayDatabase dbHelper;
    private SQLiteDatabase database;
    private static final String[] ALL_COLUMNS = new String[] {
            SnowDayDatabase.COLUMN_ID,
            SnowDayDatabase.COLUMN_ALARM_TIME,
            SnowDayDatabase.COLUMN_STATUS,
            SnowDayDatabase.COLUMN_ASSOCIATED_ALARM
    };

    public DailyAlarmInterface(Context c){
        dbHelper = new SnowDayDatabase(c);
    }

    public DailyAlarmInterface(SnowDayDatabase d){
        dbHelper = d;
    }

    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public List<DailyAlarm> query(String selection){
        Log.i(LOG_TAG, "Request processing for Daily Alarms WHERE " + selection);
        Cursor c = database.query(SnowDayDatabase.TABLE_DAILY_ALARMS, ALL_COLUMNS, selection, null, null, null, null);
        ArrayList<DailyAlarm> ret = new ArrayList<>();
        AlarmTemplateInterface alarmDbHelper = new AlarmTemplateInterface(dbHelper);
        alarmDbHelper.open();
        c.moveToFirst();
        while(!c.isAfterLast()){
            ret.add(alarmFromDb(c, alarmDbHelper));
            c.moveToNext();
        }
        c.close();
        alarmDbHelper.close();
        return ret;
    }

    public void deleteDependents(AlarmTemplate t){
        database.delete(SnowDayDatabase.TABLE_DAILY_ALARMS, SnowDayDatabase.idEquals(t.getId()), null);
    }

    private DailyAlarm alarmFromDb(Cursor c, AlarmTemplateInterface t){
        long id = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ID));
        int statusCode = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_STATUS));
        long timeMillis = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ALARM_TIME));

        long alarmId = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ASSOCIATED_ALARM));
        AlarmTemplate associatedAlarm = t.query(SnowDayDatabase.idEquals(alarmId)).get(0);

        return new DailyAlarm(id, new Date(timeMillis), AlarmAction.getFromCode(statusCode), associatedAlarm);
    }

    public long add(DailyAlarm dailyAlarm) {
        ContentValues values = new ContentValues();
        values.put(SnowDayDatabase.COLUMN_ALARM_TIME, dailyAlarm.getTriggerTime().getTime());
        values.put(SnowDayDatabase.COLUMN_STATUS, dailyAlarm.getState().getCode());
        values.put(SnowDayDatabase.COLUMN_ASSOCIATED_ALARM, dailyAlarm.getAssociatedAlarm().getId());

        return database.insert(SnowDayDatabase.TABLE_DAILY_ALARMS, null, values);
    }

    public void update(DailyAlarm dailyAlarm) {
        ContentValues values = new ContentValues();
        values.put(SnowDayDatabase.COLUMN_ALARM_TIME, dailyAlarm.getTriggerTime().getTime());
        values.put(SnowDayDatabase.COLUMN_STATUS, dailyAlarm.getState().getCode());
        values.put(SnowDayDatabase.COLUMN_ASSOCIATED_ALARM, dailyAlarm.getAssociatedAlarm().getId());

        database.update(SnowDayDatabase.TABLE_DAILY_ALARMS, values, SnowDayDatabase.idEquals(dailyAlarm.getId()), null);
    }

    public void clearAll(){
        database.delete(SnowDayDatabase.TABLE_DAILY_ALARMS, null, null);
    }

    public void delete(String selection) {
        Log.w(LOG_TAG, "Clearing all daily alarms WHERE " + selection);
        database.delete(SnowDayDatabase.TABLE_DAILY_ALARMS, selection, null);
    }
}
