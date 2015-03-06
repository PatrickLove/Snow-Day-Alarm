package patricklove.com.snowdayalarm.alarmTools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import patricklove.com.snowdayalarm.R;
import patricklove.com.snowdayalarm.alarmTools.scheduling.AlarmScheduler;
import patricklove.com.snowdayalarm.database.AlarmTemplateInterface;
import patricklove.com.snowdayalarm.database.SnowDayDatabase;
import patricklove.com.snowdayalarm.database.models.AlarmTemplate;
import patricklove.com.snowdayalarm.utils.DateUtils;

/**
 * Created by Patrick Love on 2/21/2015.
 */
public class AlarmTemplateListAdapter extends BaseAdapter {

    private List<AlarmTemplate> alarms;
    private Context context;
    private int position;
    private OnEnableChangeListener enableChangeListener;

    public interface OnEnableChangeListener {
        public void onEnabledStateChanged();
    }

    public AlarmTemplateListAdapter(Context context, List<AlarmTemplate> alarms, OnEnableChangeListener listener){
        this.alarms = alarms;
        this.context = context;
        this.enableChangeListener = listener;
    }

    public void updateList(List<AlarmTemplate> list){
        alarms = list;
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return alarms.get(position).getId();
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        final AlarmTemplate alarm = alarms.get(position);
        View view = convertView;
        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.alarm_template_list_element, null);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView days = (TextView) view.findViewById(R.id.activeDays);
        title.setText(alarm.getName());
        String timeStr = DateUtils.formatMilliTime(alarm.getTime());
        time.setText(timeStr);
        days.setText(alarm.getActiveDayStr());

        Switch enabledSwitch = (Switch) view.findViewById(R.id.enableToggle);
        enabledSwitch.setChecked(alarm.isEnabled());
        enabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarm.setEnabled(isChecked);
                alarm.updateDBEnabled(context);
                enableChangeListener.onEnabledStateChanged();
            }
        });
        return view;
    }


}
