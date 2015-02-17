package patricklove.com.snowdayalarm.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Patrick Love on 2/11/2015.
 */
public class SnowDayDatabase extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "snow_day_alarm.db";
    private static final int DB_VERSION = 1;

    protected static final String TABLE_ALL_ALARMS = "All_Alarms";
    protected static final String TABLE_DAILY_ALARMS = "Daily_Alarms";


    protected static final String COLUMN_ID = "_id";
    protected static final String COLUMN_ALARM_TIME = "alarm_time";
    protected static final String COLUMN_STATUS = "status";
    protected static final String COLUMN_ASSOCIATED_ALARM = "associated_alarm";
    protected static final String COLUMN_ACTION_CANCEL = "action_cancel";
    protected static final String COLUMN_ACTION_DELAY = "action_delay";

    protected static final class COLUMN_DAYS {
        protected static final String MONDAY = "monday";
        protected static final String TUESDAY = "tuesday";
        protected static final String WEDNESDAY = "wednesday";
        protected static final String THURSDAY = "thursday";
        protected static final String FRIDAY = "friday";
        protected static final String SATURDAY = "saturday";
        protected static final String SUNDAY = "sunday";
    }

    private static final String SQL_CREATE_ALL_ALARMS = "create table " + TABLE_ALL_ALARMS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_ACTION_CANCEL + " integer not null, " +
            COLUMN_ACTION_DELAY + " integer not null, " +
            COLUMN_ALARM_TIME + " integer not null, " +
            COLUMN_DAYS.MONDAY + " integer not null, " +
            COLUMN_DAYS.TUESDAY + " integer not null, " +
            COLUMN_DAYS.WEDNESDAY + " integer not null, " +
            COLUMN_DAYS.THURSDAY + " integer not null, " +
            COLUMN_DAYS.FRIDAY + " integer not null, " +
            COLUMN_DAYS.SATURDAY + " integer not null, " +
            COLUMN_DAYS.SUNDAY+ " integer not null " +
        ");";
    private static final String SQL_CREATE_DAILY_ALARMS = "create table " + TABLE_DAILY_ALARMS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_ALARM_TIME + " integer not null, " +
            COLUMN_STATUS + " integer not null, " +
            COLUMN_ASSOCIATED_ALARM + " integer not null, " +
            "FOREIGN KEY(" + COLUMN_ASSOCIATED_ALARM + ") REFERENCES " + TABLE_ALL_ALARMS + "(" + COLUMN_ID + ")" +
        ");";

    public SnowDayDatabase(Context c){
        super(c,DATABASE_NAME,null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALL_ALARMS);
        db.execSQL(SQL_CREATE_DAILY_ALARMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO Implement better update mechanism
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DAILY_ALARMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALL_ALARMS);
        onCreate(db);
    }

    public static String idEquals(long id){
        return "(" + COLUMN_ID + "=" + id + ")";
    }
}
