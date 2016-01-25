package com.patricklove.snowdayalarm.activities;

import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.patricklove.snowdayalarm.R;
import com.patricklove.snowdayalarm.alarmTools.scheduling.AlarmHandlingService;
import com.patricklove.snowdayalarm.twitter.DayState;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RingingActivity extends ActionBarActivity {

    private Ringtone ringer;
    private Date warnDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringer = RingtoneManager.getRingtone(this, alarmUri);
        ringer.setStreamType(AudioManager.STREAM_ALARM);
        ringer.play();

        String stateText = getString(R.string.no_state_text);
        String name = getString(R.string.no_name);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if (extras.containsKey(AlarmHandlingService.EXTRA_WARNING_DATE)) {
                warnDate = new Date(extras.getLong(AlarmHandlingService.EXTRA_WARNING_DATE));
            }
            if (extras.containsKey(AlarmHandlingService.EXTRA_DAY_STATE)){
                stateText = getTextForState(DayState.getFromCode(extras.getInt(AlarmHandlingService.EXTRA_DAY_STATE)));
            }
            if (extras.containsKey(AlarmHandlingService.EXTRA_ALARM_NAME)){
                name = extras.getString(AlarmHandlingService.EXTRA_ALARM_NAME);
            }
        }
        ((TextView) findViewById(R.id.status_view)).setText(stateText);
        ((TextView) findViewById(R.id.alarm_name)).setText(name);

        if(warnDate != null){
            TextView warningText = (TextView) findViewById(R.id.warning_view);
            DateFormat formatter = new SimpleDateFormat();
            String date = formatter.format(warnDate);
            warningText.setText(getString(R.string.warning_message) + date);
        }

    }

    private String getTextForState(DayState state) {
        if(state == DayState.DELAY_2_HR){
            return getString(R.string.message_delay);
        }
        return getString(R.string.message_normal);
    }

    public void onStopButtonPressed(View v){
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if(ringer.isPlaying()) {
            ringer.stop();
        }
        super.onDestroy();
    }
}
