package patricklove.com.snowdayalarm.activities;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import patricklove.com.snowdayalarm.R;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmHandlingService;
import patricklove.com.snowdayalarm.twitter.DayState;

public class RingingActivity extends ActionBarActivity {

    private Ringtone ringer;
    private Date warnDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing);


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        ringer = RingtoneManager.getRingtone(this, alarmUri);
        ringer.play();

        String stateText = getString(R.string.no_state_text);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if (extras.containsKey(AlarmHandlingService.EXTRA_WARNING_DATE)) {
                warnDate = new Date(extras.getLong(AlarmHandlingService.EXTRA_WARNING_DATE));
            }
            if (extras.containsKey(AlarmHandlingService.EXTRA_DAY_STATE)){
                stateText = getTextForState(DayState.getFromCode(extras.getInt(AlarmHandlingService.EXTRA_DAY_STATE)));
            }
        }
        ((TextView) findViewById(R.id.status_view)).setText(stateText);

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