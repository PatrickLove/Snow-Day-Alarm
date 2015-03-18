package com.patricklove.snowdayalarm.activities.mainTabFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.patricklove.snowdayalarm.R;
import com.patricklove.snowdayalarm.alarmTools.DailyAlarmListAdapter;
import com.patricklove.snowdayalarm.database.DailyAlarmInterface;
import com.patricklove.snowdayalarm.database.models.DailyAlarm;
import com.patricklove.snowdayalarm.utils.DateUtils;

import java.util.List;

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
