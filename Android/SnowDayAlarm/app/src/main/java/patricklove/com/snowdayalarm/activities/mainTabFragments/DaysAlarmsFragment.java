package patricklove.com.snowdayalarm.activities.mainTabFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import patricklove.com.snowdayalarm.alarmTools.DailyAlarmListAdapter;
import patricklove.com.snowdayalarm.database.DailyAlarmInterface;
import patricklove.com.snowdayalarm.database.models.DailyAlarm;
import patricklove.com.snowdayalarm.utils.DateUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class DaysAlarmsFragment extends ListFragment {


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

        DailyAlarmInterface dbHelp = new DailyAlarmInterface(getActivity().getApplicationContext());
        dbHelp.open();
        List<DailyAlarm> allAlarms = dbHelp.getForDay(DateUtils.getNow());
        dbHelp.close();

        DailyAlarmListAdapter adapter = new DailyAlarmListAdapter(getActivity(), allAlarms);
        this.setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }
}
