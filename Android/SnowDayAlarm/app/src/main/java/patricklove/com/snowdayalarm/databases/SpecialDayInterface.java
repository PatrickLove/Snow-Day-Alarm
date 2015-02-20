package patricklove.com.snowdayalarm.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import patricklove.com.snowdayalarm.alarmTools.DateUtils;
import twitter.DayState;
import twitter.SpecialDate;

/**
 * Created by Patrick Love on 2/17/2015.
 */
public class SpecialDayInterface {

    private SnowDayDatabase dbHelper;
    private SQLiteDatabase database;
    private static final String[] ALL_COLUMNS = new String[] {
            SnowDayDatabase.COLUMN_ID,
            SnowDayDatabase.COLUMN_DATE,
            SnowDayDatabase.COLUMN_STATUS
    };

    public SpecialDayInterface(Context c){
        dbHelper = new SnowDayDatabase(c);
    }

    public SpecialDayInterface(SnowDayDatabase d){
        dbHelper = d;
    }

    public void open(){
        database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public List<SpecialDateDBWrapper> query(String selection){
        Cursor c = database.query(SnowDayDatabase.TABLE_SPECIAL_DAYS, ALL_COLUMNS, selection, null, null, null, null);
        ArrayList<SpecialDateDBWrapper> ret = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            ret.add(specialDateFromCursor(c));
            c.moveToNext();
        }
        c.close();
        return ret;
    }

    public List<SpecialDateDBWrapper> getForDay(Date date){
        return query(DateUtils.getSearchStringForDay(date, SnowDayDatabase.COLUMN_DATE));
    }

    public long add(SpecialDate date){
        ContentValues values = new ContentValues();
        values.put(SnowDayDatabase.COLUMN_DATE, date.getDate().getTime());
        values.put(SnowDayDatabase.COLUMN_STATUS, date.getState().code);

        return database.insert(SnowDayDatabase.TABLE_SPECIAL_DAYS, null, values);
    }

    public void delete(SpecialDateDBWrapper date){
        database.delete(SnowDayDatabase.TABLE_SPECIAL_DAYS, SnowDayDatabase.idEquals(date.getId()), null);
    }

    private SpecialDateDBWrapper specialDateFromCursor(Cursor c) {
        long id = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ID));
        int stateCode = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_STATUS));
        long dateMillis = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_DATE));

        return new SpecialDateDBWrapper(id, new Date(dateMillis), DayState.getFromCode(stateCode));
    }
}
