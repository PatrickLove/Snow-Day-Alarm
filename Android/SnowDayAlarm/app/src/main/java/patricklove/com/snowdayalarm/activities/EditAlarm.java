package patricklove.com.snowdayalarm.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.List;

import patricklove.com.snowdayalarm.R;
import patricklove.com.snowdayalarm.alarmTools.AlarmAction;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.database.AlarmTemplateInterface;
import patricklove.com.snowdayalarm.database.SnowDayDatabase;
import patricklove.com.snowdayalarm.database.models.AlarmTemplate;
import patricklove.com.snowdayalarm.utils.DateUtils;

public class EditAlarm extends ActionBarActivity {

    public static final String EXTRA_ALARM_ID = "alarm.id";
    public static final long NO_PREVIOUS_ALARM = -1;

    private static final int NO_ERROR = 0;
    private static final int MISSING_NAME = 1;
    private static final int MISSING_DAY_OF_WEEK = 2;

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
    private Spinner cancelAction;
    private Spinner delayAction;
    private ToggleButton enabledButton;
    private ProgressDialog progressDialog;

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
        delayAction = (Spinner) findViewById(R.id.delaySpinner);
        cancelAction = (Spinner) findViewById(R.id.cancelSpinner);
        enabledButton = (ToggleButton) findViewById(R.id.enableToggle);


        delayAction.setAdapter(AlarmAction.getSpinnerAdapter(this));
        cancelAction.setAdapter(AlarmAction.getSpinnerAdapter(this));
        cancelAction.setSelection(AlarmAction.DISABLE.getCode());
        delayAction.setSelection(AlarmAction.DELAY_2_HR.getCode());

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
                delayAction.setSelection(priorTemp.getActionDelay().getCode());
                cancelAction.setSelection(priorTemp.getActionCancel().getCode());
                enabledButton.setChecked(priorTemp.isEnabled());
            }
            else{
                alarmId = NO_PREVIOUS_ALARM;
            }
            findViewById(R.id.deleteButton).setVisibility(View.VISIBLE);
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
        return (int) (timeMillis/DateUtils.MILLIS_PER_HOUR);
    }
    private int getMinute(){
        return (int) (timeMillis/DateUtils.MILLIS_PER_MINUTE%60);
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

    private void showProgress(int message){
        if(progressDialog!=null) progressDialog.dismiss();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(message));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
    }

    public void saveAndFinish(View v){
        AlarmTemplate temp = getTemplate();
        int resultCode = canSaveAlarm(temp);
        if(resultCode == NO_ERROR){
            showProgress(R.string.saving);
            new SaveAlarmTask().execute(temp);
        }
        else if(resultCode == MISSING_NAME){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.missing_value_title)
                    .setMessage(R.string.missing_name_message)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nameEntry.requestFocus();
                        }
                    })
                    .show();
        }
        else if(resultCode == MISSING_DAY_OF_WEEK){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.missing_value_title)
                    .setMessage(R.string.missing_date_message)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }

    public void deleteAndFinish(View v){
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_are_you_sure)
                .setMessage(R.string.message_are_you_sure)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAlarmAndFinish();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deleteAlarmAndFinish(){
        showProgress(R.string.deleting);
        new DeleteAlarmTask().execute();
    }

    private int canSaveAlarm(AlarmTemplate temp) {
        if(temp.getName().equals("")) return MISSING_NAME;
        if(temp.getActiveDayStr().equals("Never")) return MISSING_DAY_OF_WEEK;
        return NO_ERROR;
    }

    private class DeleteAlarmTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] params) {
            AlarmTemplateInterface dbHelp = new AlarmTemplateInterface(getApplicationContext());
            dbHelp.open();
            dbHelp.delete(dbHelp.query(SnowDayDatabase.idEquals(alarmId)).get(0));
            dbHelp.close();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            finish();
        }
    }

    private class SaveAlarmTask extends AsyncTask<AlarmTemplate, Object, Object> {
        @Override
        protected Object doInBackground(AlarmTemplate... params) {
            AlarmTemplate temp = params[0];
            AlarmTemplateInterface dbHelp = new AlarmTemplateInterface(getApplicationContext());
            AlarmScheduler scheduler = new AlarmScheduler(getApplicationContext());
            dbHelp.open();
            if(alarmId == NO_PREVIOUS_ALARM){
                temp.save(dbHelp);
                dbHelp.close();
                scheduler.open();
                scheduler.scheduleAsNew(temp);
            }
            else {
                dbHelp.update(temp);
                dbHelp.clearDependants(temp);
                dbHelp.close();
                scheduler.open();
                if (temp.isEnabled()) scheduler.scheduleAsNew(temp);
            } //Properly delay/schedule new alarms.  No twitter refresh as MainActivity probably launched recently and performed one
            scheduler.close();
            new RefreshStatesTask(getApplicationContext()).execWithoutTwitter();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            finish();
        }
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
        AlarmAction delay = (AlarmAction) delayAction.getSelectedItem();
        AlarmAction cancel = (AlarmAction) cancelAction.getSelectedItem();
        boolean enabled = enabledButton.isChecked();
        return new AlarmTemplate(alarmId, name, cancel, delay, timeMillis, monday, tuesday, wednesday, thursday, friday, saturday, sunday, enabled);
    }

    @Override
    protected void onDestroy() {
        if(progressDialog != null) progressDialog.dismiss();
        super.onDestroy();
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
