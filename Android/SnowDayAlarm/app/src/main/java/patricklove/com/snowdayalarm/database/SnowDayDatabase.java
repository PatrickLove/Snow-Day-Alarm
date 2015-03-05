package patricklove.com.snowdayalarm.database;

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
    protected static final String TABLE_SPECIAL_DAYS = "Special_Days";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ALARM_TIME = "alarm_time";
    public static final String COLUMN_ALARM_DATE = "alarm_date";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_ASSOCIATED_ALARM = "associated_alarm";
    public static final String COLUMN_ACTION_CANCEL = "action_cancel";
    public static final String COLUMN_ACTION_DELAY = "action_delay";

    public static final class COLUMN_DAYS {
        public static final String MONDAY = "monday";
        public static final String TUESDAY = "tuesday";
        public static final String WEDNESDAY = "wednesday";
        public static final String THURSDAY = "thursday";
        public static final String FRIDAY = "friday";
        public static final String SATURDAY = "saturday";
        public static final String SUNDAY = "sunday";
    }

    private static final String SQL_CREATE_ALL_ALARMS = "create table " + TABLE_ALL_ALARMS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_NAME + " text not null, " +
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
            COLUMN_NAME + " text not null, " +
            COLUMN_ALARM_DATE + " integer not null, " +
            COLUMN_ALARM_TIME + " integer not null, " +
            COLUMN_STATUS + " integer not null, " +
            COLUMN_ASSOCIATED_ALARM + " integer not null, " +
            "FOREIGN KEY(" + COLUMN_ASSOCIATED_ALARM + ") REFERENCES " + TABLE_ALL_ALARMS + "(" + COLUMN_ID + ")" +
        ");";
    private static final String SQL_CREATE_SPECIAL_DAYS = "create table " + TABLE_SPECIAL_DAYS + "(" +
            COLUMN_ID + " integer primary key autoincrement, " +
            COLUMN_DATE + " integer not null, " +
            COLUMN_STATUS + " integer not null " +
        ");";

    public SnowDayDatabase(Context c){
        super(c,DATABASE_NAME,null,DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ALL_ALARMS);
        db.execSQL(SQL_CREATE_DAILY_ALARMS);
        db.execSQL(SQL_CREATE_SPECIAL_DAYS);
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
