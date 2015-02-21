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
import patricklove.com.snowdayalarm.database.models.AlarmTemplate;

/**
 * Created by Patrick Love on 2/21/2015.
 */
public class AlarmTemplateListAdapter extends BaseAdapter {

    private List<AlarmTemplate> alarms;
    private Context context;

    public AlarmTemplateListAdapter(Context context, List<AlarmTemplate> alarms){
        this.alarms = alarms;
        this.context = context;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlarmTemplate alarm = alarms.get(position);
        View view = convertView;
        if(view == null){
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.alarm_template_list_element, null);
        }
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView time = (TextView) view.findViewById(R.id.time);
        TextView days = (TextView) view.findViewById(R.id.activeDays);
        title.setText(alarm.getName());
        String timeStr = DateFormat.getTimeInstance(DateFormat.SHORT).format(alarm.getTime());
        time.setText(timeStr);
        days.setText(alarm.getActiveDayStr());
        return view;
    }
}
