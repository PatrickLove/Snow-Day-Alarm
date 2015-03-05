package patricklove.com.snowdayalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import patricklove.com.snowdayalarm.alarmTools.AlarmAction;
import patricklove.com.snowdayalarm.database.models.AlarmTemplate;
import patricklove.com.snowdayalarm.database.models.DailyAlarm;
import patricklove.com.snowdayalarm.twitter.DayState;
import patricklove.com.snowdayalarm.twitter.TwitterAnalysisBridge;
import patricklove.com.snowdayalarm.utils.DateUtils;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class DailyAlarmInterface {

    private static final String LOG_TAG = "DailyAlarmDBInterface";
    private Context c;
    private SnowDayDatabase dbHelper;
    private SQLiteDatabase database;
    private static final String[] ALL_COLUMNS = new String[] {
            SnowDayDatabase.COLUMN_ID,
            SnowDayDatabase.COLUMN_ALARM_TIME,
            SnowDayDatabase.COLUMN_STATUS,
            SnowDayDatabase.COLUMN_ASSOCIATED_ALARM,
            SnowDayDatabase.COLUMN_NAME,
            SnowDayDatabase.COLUMN_ALARM_DATE
    };

    public DailyAlarmInterface(Context c){
        this.c = c;
    }

    public void open(){
        dbHelper = new SnowDayDatabase(c);
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
        dbHelper = null;
    }

    public List<DailyAlarm> query(String selection){
        Log.d(LOG_TAG, "Request processing for Daily Alarms WHERE " + selection);
        Cursor c = database.query(SnowDayDatabase.TABLE_DAILY_ALARMS, ALL_COLUMNS, selection, null, null, null, null);
        ArrayList<DailyAlarm> ret = new ArrayList<>();
        AlarmTemplateInterface alarmDbHelper = new AlarmTemplateInterface(this.c);
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

    public List<DailyAlarm> getForDay(Date date){
        return query(SnowDayDatabase.COLUMN_ALARM_DATE + "=" + DateUtils.stripTime(date).getTime());
    }

    public void deleteDependents(AlarmTemplate t){
        database.delete(SnowDayDatabase.TABLE_DAILY_ALARMS, SnowDayDatabase.COLUMN_ASSOCIATED_ALARM + "=" + t.getId(), null);
    }

    private DailyAlarm alarmFromDb(Cursor c, AlarmTemplateInterface t){
        long id = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ID));
        String name = c.getString(c.getColumnIndex(SnowDayDatabase.COLUMN_NAME));
        int statusCode = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_STATUS));
        long dateMillis = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ALARM_DATE));
        long timeMillis = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ALARM_TIME));

        long alarmId = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ASSOCIATED_ALARM));
        AlarmTemplate associatedAlarm = t.query(SnowDayDatabase.idEquals(alarmId)).get(0);//TODO handle missing associated alarm

        return new DailyAlarm(id, name, new Date(dateMillis), timeMillis, AlarmAction.getFromCode(statusCode), associatedAlarm);
    }

    public long add(DailyAlarm dailyAlarm) {
        ContentValues values = encodeToValues(dailyAlarm);
        return database.insert(SnowDayDatabase.TABLE_DAILY_ALARMS, null, values);
    }

    public void update(DailyAlarm dailyAlarm) {
        ContentValues values = encodeToValues(dailyAlarm);
        database.update(SnowDayDatabase.TABLE_DAILY_ALARMS, values, SnowDayDatabase.idEquals(dailyAlarm.getId()), null);
    }

    private ContentValues encodeToValues(DailyAlarm dailyAlarm){
        ContentValues values = new ContentValues();
        values.put(SnowDayDatabase.COLUMN_ALARM_TIME, dailyAlarm.getTriggerTime());
        values.put(SnowDayDatabase.COLUMN_ALARM_DATE, dailyAlarm.getTriggerDate().getTime());
        values.put(SnowDayDatabase.COLUMN_NAME, dailyAlarm.getName());
        values.put(SnowDayDatabase.COLUMN_STATUS, dailyAlarm.getState().getCode());
        values.put(SnowDayDatabase.COLUMN_ASSOCIATED_ALARM, dailyAlarm.getAssociatedAlarm().getId());
        return values;
    }

    public void clearAll(){
        database.delete(SnowDayDatabase.TABLE_DAILY_ALARMS, null, null);
    }

    public void delete(String selection) {
        Log.w(LOG_TAG, "Clearing all daily alarms WHERE " + selection);
        database.delete(SnowDayDatabase.TABLE_DAILY_ALARMS, selection, null);
    }
}
