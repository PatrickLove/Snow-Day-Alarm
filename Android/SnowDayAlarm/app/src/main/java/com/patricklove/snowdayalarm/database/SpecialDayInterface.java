package com.patricklove.snowdayalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.patricklove.snowdayalarm.database.models.SpecialDate;
import com.patricklove.snowdayalarm.twitter.DayState;
import com.patricklove.snowdayalarm.utils.DateUtils;

/**
 * Created by Patrick Love on 2/17/2015.
 */
public class SpecialDayInterface {

    private static final String LOG_TAG = "SpecialDayDBInterface";
    private Context c;
    private SnowDayDatabase dbHelper;
    private SQLiteDatabase database;
    private static final String[] ALL_COLUMNS = new String[] {
            SnowDayDatabase.COLUMN_ID,
            SnowDayDatabase.COLUMN_DATE,
            SnowDayDatabase.COLUMN_STATUS
    };

    public SpecialDayInterface(Context c){
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

    public List<SpecialDate> query(String selection){
        Log.d(LOG_TAG, "Request processing for Special Days WHERE " + selection);
        Cursor c = database.query(SnowDayDatabase.TABLE_SPECIAL_DAYS, ALL_COLUMNS, selection, null, null, null, null);
        ArrayList<SpecialDate> ret = new ArrayList<>();
        c.moveToFirst();
        while(!c.isAfterLast()){
            ret.add(specialDateFromCursor(c));
            c.moveToNext();
        }
        c.close();
        return ret;
    }

    public List<SpecialDate> getForDay(Date date){
        return query(DateUtils.getSearchStringForDay(date, SnowDayDatabase.COLUMN_DATE));
    }

    public DayState getStateForDay(Date date){
        List<SpecialDate> entries = getForDay(date);
        if(entries == null || entries.size()==0){
            return DayState.NO_CHANGE;
        }
        DayState ret = entries.get(0).getState();
        for(SpecialDate day : entries){
            if(!ret.atOrBefore(day.getState())){ //If any one is before the latest, use it
                ret = day.getState();
            }
            if(ret == DayState.NO_CHANGE){ //might as well stop if we've reached the earliest option
                return DayState.NO_CHANGE;
            }
        }
        return ret;
    }

    public long add(SpecialDate date){
        ContentValues values = new ContentValues();
        values.put(SnowDayDatabase.COLUMN_DATE, date.getDate().getTime());
        values.put(SnowDayDatabase.COLUMN_STATUS, date.getState().code);

        return database.insert(SnowDayDatabase.TABLE_SPECIAL_DAYS, null, values);
    }

    public void delete(String where){
        Log.w(LOG_TAG, "Clearing Special Dates WHERE " + where);
        database.delete(SnowDayDatabase.TABLE_SPECIAL_DAYS, where, null);
    }

    public void delete(SpecialDate date){
        delete(SnowDayDatabase.idEquals(date.getId()));
    }

    private SpecialDate specialDateFromCursor(Cursor c) {
        long id = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_ID));
        int stateCode = c.getInt(c.getColumnIndex(SnowDayDatabase.COLUMN_STATUS));
        long dateMillis = c.getLong(c.getColumnIndex(SnowDayDatabase.COLUMN_DATE));

        return new SpecialDate(id, new Date(dateMillis), DayState.getFromCode(stateCode));
    }

    public void cleanUp() {
        delete(SnowDayDatabase.COLUMN_DATE + "<" + DateUtils.getToday().getTime());
    }
}
