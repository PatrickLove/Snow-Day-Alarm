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

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class AlarmTemplateInterface {

    private static final String LOG_TAG = "AlarmTemplateDBInterface";
    private SnowDayDatabase dbHelper;
    private SQLiteDatabase database;
    private static final String[] ALL_COLUMNS = new String[] {
            SnowDayDatabase.COLUMN_ID,
            SnowDayDatabase.COLUMN_ACTION_DELAY,
            SnowDayDatabase.COLUMN_ACTION_CANCEL,
            SnowDayDatabase.COLUMN_ALARM_TIME,
            SnowDayDatabase.COLUMN_DAYS.MONDAY,
            SnowDayDatabase.COLUMN_DAYS.TUESDAY,
            SnowDayDatabase.COLUMN_DAYS.WEDNESDAY,
            SnowDayDatabase.COLUMN_DAYS.THURSDAY,
            SnowDayDatabase.COLUMN_DAYS.FRIDAY,
            SnowDayDatabase.COLUMN_DAYS.SATURDAY,
            SnowDayDatabase.COLUMN_DAYS.SUNDAY
    };

    public AlarmTemplateInterface(Context c){
        dbHelper = new SnowDayDatabase(c);
    }

    public AlarmTemplateInterface(SnowDayDatabase d){
        dbHelper = d;
    }

    public void open(){
        database = dbHelper.getWritableDatabase();
    }
    public void close(){
        dbHelper.close();
    }

    private AlarmTemplate templateFromCursor(Cursor c){
        long id = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ID));
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


        return new AlarmTemplate(id, AlarmAction.getFromCode(cancelCode), AlarmAction.getFromCode(delayCode), new Date(timeMillis),
                monday, tuesday, wednesday, thursday, friday, saturday, sunday);
    }

    public long add(AlarmTemplate temp){
        ContentValues values = new ContentValues();
        values.put(SnowDayDatabase.COLUMN_ACTION_CANCEL, temp.getActionCancel().getCode());
        values.put(SnowDayDatabase.COLUMN_ACTION_DELAY, temp.getActionDelay().getCode());
        values.put(SnowDayDatabase.COLUMN_ALARM_TIME, temp.getTime().getTime());
        values.put(SnowDayDatabase.COLUMN_DAYS.MONDAY, temp.isMonday());
        values.put(SnowDayDatabase.COLUMN_DAYS.TUESDAY, temp.isTuesday());
        values.put(SnowDayDatabase.COLUMN_DAYS.WEDNESDAY, temp.isWednesday());
        values.put(SnowDayDatabase.COLUMN_DAYS.THURSDAY, temp.isThursday());
        values.put(SnowDayDatabase.COLUMN_DAYS.FRIDAY, temp.isFriday());
        values.put(SnowDayDatabase.COLUMN_DAYS.SATURDAY, temp.isSaturday());
        values.put(SnowDayDatabase.COLUMN_DAYS.SUNDAY, temp.isSunday());

        return database.insert(SnowDayDatabase.TABLE_ALL_ALARMS, null, values);
    }

    public void delete(AlarmTemplate temp){
        Log.w(LOG_TAG, "Deleting template of id " + temp.getId() + " and dependent alarms");
        DailyAlarmInterface dailyAlarmHelper = new DailyAlarmInterface(dbHelper);
        dailyAlarmHelper.open();
        dailyAlarmHelper.deleteDependents(temp);
        dailyAlarmHelper.close();

        database.delete(SnowDayDatabase.TABLE_ALL_ALARMS, SnowDayDatabase.idEquals(temp.getId()), null);
    }

    public List<AlarmTemplate> getAll(){
        return query(null);
    }

    public List<AlarmTemplate> query(String selection){
        Log.i(LOG_TAG, "Request processing for Alarm Templates WHERE " + selection);
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
}
