package patricklove.com.snowdayalarm.alarmTools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.List;

import patricklove.com.snowdayalarm.R;
import patricklove.com.snowdayalarm.database.models.DailyAlarm;

/**
 * Created by Patrick Love on 2/21/2015.
 */
public class DailyAlarmListAdapter extends BaseAdapter {

    private List<DailyAlarm> alarms;
    private Context context;

    public DailyAlarmListAdapter(Context context, List<DailyAlarm> alarms){
        this.alarms = alarms;
        this.context = context;
    }

    public void updateList(List<DailyAlarm> list){
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
    public View getView(int position, View convertView, ViewGroup parent) {
        DailyAlarm alarm = alarms.get(position);
        View view = convertView;
        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.daily_alarm_list_element, null);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView time = (TextView) view.findViewById(R.id.time);
        time.setBackgroundColor(context.getResources().getColor(alarm.getStatusColorID()));
        title.setText(alarm.getName());
        String timeStr = alarm.getTimeString();
        time.setText(timeStr);
        return view;
    }
}
