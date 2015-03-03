package patricklove.com.snowdayalarm.activities;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.List;

import patricklove.com.snowdayalarm.R;
import patricklove.com.snowdayalarm.alarmTools.AlarmAction;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.database.AlarmTemplateInterface;
import patricklove.com.snowdayalarm.database.SnowDayDatabase;
import patricklove.com.snowdayalarm.database.SpecialDayInterface;
import patricklove.com.snowdayalarm.database.models.AlarmTemplate;
import patricklove.com.snowdayalarm.twitter.DayState;
import patricklove.com.snowdayalarm.utils.DateUtils;

public class EditAlarm extends ActionBarActivity {

    public static final String EXTRA_ALARM_ID = "alarm.id";
    public static final long NO_PREVIOUS_ALARM = -1;

    private long alarmId = NO_PREVIOUS_ALARM;
    private long timeMillis = 12*DateUtils.MILLIS_PER_HOUR; //default to noon
    private EditText nameEntry;
    private CheckBox mondayBox;
    private CheckBox tuesdayBox;
    private CheckBox wednesdayBox;
    private CheckBox thursdayBox;
    private CheckBox fridayBox;
    private CheckBox saturdayBox;
    private CheckBox sundayBox;
    private TextView timeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_alarm);
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey(EXTRA_ALARM_ID)){
            alarmId = extras.getLong(EXTRA_ALARM_ID);
        }

        nameEntry = (EditText) findViewById(R.id.nameText);
        mondayBox = (CheckBox) findViewById(R.id.mondayBox);
        tuesdayBox = (CheckBox) findViewById(R.id.tuesdayBox);
        wednesdayBox = (CheckBox) findViewById(R.id.wednesdayBox);
        thursdayBox = (CheckBox) findViewById(R.id.thursdayBox);
        fridayBox = (CheckBox) findViewById(R.id.fridayBox);
        saturdayBox = (CheckBox) findViewById(R.id.saturdayBox);
        sundayBox = (CheckBox) findViewById(R.id.sundayBox);
        timeText = (TextView) findViewById(R.id.time);

        if(alarmId != NO_PREVIOUS_ALARM){
            AlarmTemplate priorTemp = getPriorAlarm();
            if(priorTemp != null){
                nameEntry.setText(priorTemp.getName());
                timeMillis = priorTemp.getTime();
                mondayBox.setChecked(priorTemp.isMonday());
                tuesdayBox.setChecked(priorTemp.isTuesday());
                wednesdayBox.setChecked(priorTemp.isWednesday());
                thursdayBox.setChecked(priorTemp.isThursday());
                fridayBox.setChecked(priorTemp.isFriday());
                saturdayBox.setChecked(priorTemp.isSaturday());
                sundayBox.setChecked(priorTemp.isSunday());
            }
            else{
                alarmId = NO_PREVIOUS_ALARM;
            }
        }

        updateTimeText();
    }

    private void setTime(long time){
        timeMillis = time;
        updateTimeText();
    }

    private void updateTimeText() {
        timeText.setText(DateUtils.formatMilliTime(timeMillis));
    }

    private int getHour(){
        return (int) (timeMillis%DateUtils.MILLIS_PER_HOUR);
    }
    private int getMinute(){
        return (int) (timeMillis%DateUtils.MILLIS_PER_MINUTE&60);
    }

    public void onChangeTimePress(View v){
        new TimePickerFragment().show(getSupportFragmentManager(), "timePicker");
    }

    public AlarmTemplate getPriorAlarm(){
        AlarmTemplateInterface dbHelp = new AlarmTemplateInterface(this.getApplicationContext());
        dbHelp.open();
        List<AlarmTemplate> alarms = dbHelp.query(SnowDayDatabase.idEquals(alarmId));
        dbHelp.close();
        return alarms.size() > 0 ? alarms.get(0) : null;
    }

    public void saveAndFinish(View v){
        saveAlarm();
        this.finish();
    }

    private void saveAlarm() {
        SpecialDayInterface dayLookupHelp = new SpecialDayInterface(this.getApplicationContext());
        dayLookupHelp.open();
        DayState state = dayLookupHelp.getStateForDay(DateUtils.getNow());
        dayLookupHelp.close();
        AlarmTemplateInterface dbHelp = new AlarmTemplateInterface(this.getApplicationContext());
        AlarmScheduler scheduler = new AlarmScheduler(this.getApplicationContext());
        AlarmTemplate temp = getTemplate();
        dbHelp.open();
        if(alarmId == NO_PREVIOUS_ALARM){
            temp.save(dbHelp);
            dbHelp.close();
            scheduler.open();
            scheduler.scheduleFirstAlarm(temp);
        }
        else{
            dbHelp.update(temp);
            dbHelp.close();
            scheduler.open();
            scheduler.scheduleDependents(temp);
        }
        scheduler.updateTodaysAlarms(state); //Properly delay new alarms.  No twitter refresh as MainActivity probably launched recently and performed one
        scheduler.close();
    }

    private AlarmTemplate getTemplate(){
        String name = String.valueOf(nameEntry.getText());
        boolean monday = mondayBox.isChecked();
        boolean tuesday = tuesdayBox.isChecked();
        boolean wednesday = wednesdayBox.isChecked();
        boolean thursday = thursdayBox.isChecked();
        boolean friday = fridayBox.isChecked();
        boolean saturday = saturdayBox.isChecked();
        boolean sunday = sundayBox.isChecked();
        //TODO Add custom alarm actions
        AlarmAction delay = AlarmAction.DELAY_2_HR;
        AlarmAction cancel = AlarmAction.DISABLE;
        return new AlarmTemplate(alarmId, name, cancel, delay, timeMillis, monday, tuesday, wednesday, thursday, friday, saturday, sunday);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_edit_alarm, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    public static class TimePickerFragment extends DialogFragment implements android.app.TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            EditAlarm activity = (EditAlarm) getActivity();
            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this,  activity.getHour(), activity.getMinute(),
                    DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            ((EditAlarm) getActivity()).setTime(hourOfDay * DateUtils.MILLIS_PER_HOUR + minute * DateUtils.MILLIS_PER_MINUTE);
        }
    }
}
