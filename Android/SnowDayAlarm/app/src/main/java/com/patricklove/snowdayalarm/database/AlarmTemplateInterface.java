package com.patricklove.snowdayalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.patricklove.snowdayalarm.alarmTools.AlarmAction;
import com.patricklove.snowdayalarm.database.models.AlarmTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class AlarmTemplateInterface {

    private static final String LOG_TAG = "AlarmTemplateInterface";
    private Context c;
    private SnowDayDatabase dbHelper;
    private SQLiteDatabase database;
    private static final String[] ALL_COLUMNS = new String[] {
            SnowDayDatabase.COLUMN_ID,
            SnowDayDatabase.COLUMN_NAME,
            SnowDayDatabase.COLUMN_ACTION_DELAY,
            SnowDayDatabase.COLUMN_ACTION_CANCEL,
            SnowDayDatabase.COLUMN_ALARM_TIME,
            SnowDayDatabase.COLUMN_DAYS.MONDAY,
            SnowDayDatabase.COLUMN_DAYS.TUESDAY,
            SnowDayDatabase.COLUMN_DAYS.WEDNESDAY,
            SnowDayDatabase.COLUMN_DAYS.THURSDAY,
            SnowDayDatabase.COLUMN_DAYS.FRIDAY,
            SnowDayDatabase.COLUMN_DAYS.SATURDAY,
            SnowDayDatabase.COLUMN_DAYS.SUNDAY,
            SnowDayDatabase.COLUMN_ENABLED
    };

    public AlarmTemplateInterface(Context c){
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

    private AlarmTemplate templateFromCursor(Cursor c){
        long id = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ID));
        String name = c.getString(c.getColumnIndex(SnowDayDatabase.COLUMN_NAME));
        int cancelCode = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_ACTION_CANCEL));
        int delayCode = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_ACTION_DELAY));
        long timeMillis = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ALARM_TIME));

        boolean monday = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_DAYS.MONDAY)) == 1;
        boolean tuesday = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_DAYS.TUESDAY)) == 1;
        boolean wednesday = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_DAYS.WEDNESDAY)) == 1;
        boolean thursday = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_DAYS.THURSDAY)) == 1;
        boolean friday = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_DAYS.FRIDAY)) == 1;
        boolean saturday = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_DAYS.SATURDAY)) == 1;
        boolean sunday = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_DAYS.SUNDAY)) == 1;
        boolean enabled = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_ENABLED)) == 1;


        return new AlarmTemplate(id, name, AlarmAction.getFromCode(cancelCode), AlarmAction.getFromCode(delayCode), timeMillis,
                monday, tuesday, wednesday, thursday, friday, saturday, sunday, enabled);
    }

    public long add(AlarmTemplate temp){
        ContentValues values = encodeToValues(temp);
        return database.insert(SnowDayDatabase.TABLE_ALL_ALARMS, null, values);
    }

    private ContentValues encodeToValues(AlarmTemplate temp){
        ContentValues values = new ContentValues();
        values.put(SnowDayDatabase.COLUMN_NAME, temp.getName());
        values.put(SnowDayDatabase.COLUMN_ACTION_CANCEL, temp.getActionCancel().getCode());
        values.put(SnowDayDatabase.COLUMN_ACTION_DELAY, temp.getActionDelay().getCode());
        values.put(SnowDayDatabase.COLUMN_ALARM_TIME, temp.getTime());
        values.put(SnowDayDatabase.COLUMN_DAYS.MONDAY, temp.isMonday());
        values.put(SnowDayDatabase.COLUMN_DAYS.TUESDAY, temp.isTuesday());
        values.put(SnowDayDatabase.COLUMN_DAYS.WEDNESDAY, temp.isWednesday());
        values.put(SnowDayDatabase.COLUMN_DAYS.THURSDAY, temp.isThursday());
        values.put(SnowDayDatabase.COLUMN_DAYS.FRIDAY, temp.isFriday());
        values.put(SnowDayDatabase.COLUMN_DAYS.SATURDAY, temp.isSaturday());
        values.put(SnowDayDatabase.COLUMN_DAYS.SUNDAY, temp.isSunday());
        values.put(SnowDayDatabase.COLUMN_ENABLED, temp.isEnabled());
        return values;
    }

    public void update(AlarmTemplate alarm) {
        Log.i(LOG_TAG, "Updating " + alarm.getName());
        ContentValues values = encodeToValues(alarm);
        database.update(SnowDayDatabase.TABLE_ALL_ALARMS, values, SnowDayDatabase.idEquals(alarm.getId()), null);
    }

    public void delete(AlarmTemplate temp){
        Log.w(LOG_TAG, "Deleting " + temp.getName() + " and dependent alarms");
        clearDependants(temp);
        database.delete(SnowDayDatabase.TABLE_ALL_ALARMS, SnowDayDatabase.idEquals(temp.getId()), null);
    }

    public List<AlarmTemplate> getAll(){
        return query(null);
    }

    public List<AlarmTemplate> query(String selection){
        Log.d(LOG_TAG, "Request processing for Alarm Templates WHERE " + selection);
        Cursor c = database.query(SnowDayDatabase.TABLE_ALL_ALARMS, ALL_COLUMNS, selection, null, null, null, null);
        ArrayList<AlarmTemplate> ret = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            ret.add(templateFromCursor(c));
            c.moveToNext();
        }
        c.close();
        return ret;
    }

    public void clearDependants(AlarmTemplate temp) {
        Log.w(LOG_TAG, "Deleting dependents for " + temp.getName());
        this.close();
        DailyAlarmInterface dailyAlarmHelper = new DailyAlarmInterface(this.c);
        dailyAlarmHelper.open();
        dailyAlarmHelper.deleteDependents(temp);
        dailyAlarmHelper.close();
        this.open();
    }

    public List<AlarmTemplate> getAllEnabled() {
        return query(SnowDayDatabase.COLUMN_ENABLED + "=1");
    }
}
