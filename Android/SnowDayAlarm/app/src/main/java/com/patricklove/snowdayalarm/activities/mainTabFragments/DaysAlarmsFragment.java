package patricklove.com.snowdayalarm.activities.mainTabFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import patricklove.com.snowdayalarm.R;
import patricklove.com.snowdayalarm.alarmTools.DailyAlarmListAdapter;
import patricklove.com.snowdayalarm.database.DailyAlarmInterface;
import patricklove.com.snowdayalarm.database.models.DailyAlarm;
import patricklove.com.snowdayalarm.utils.DateUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * interface.
 */
public class DaysAlarmsFragment extends ListFragment implements Refreshable{


    private DailyAlarmListAdapter adapter;

    public static DaysAlarmsFragment newInstance() {
        DaysAlarmsFragment fragment = new DaysAlarmsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DaysAlarmsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new DailyAlarmListAdapter(getActivity(), getAlarmList());
        this.setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        this.setEmptyText(getString(R.string.message_no_alarms));
        super.onActivityCreated(savedInstanceState);
    }

    private List<DailyAlarm> getAlarmList(){
        DailyAlarmInterface dbHelp = new DailyAlarmInterface(getActivity().getApplicationContext());
        dbHelp.open();
        List<DailyAlarm> allAlarms = dbHelp.getForDay(DateUtils.getNow());
        dbHelp.close();
        return allAlarms;
    }

    public void refresh(){
        adapter.updateList(getAlarmList());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}
