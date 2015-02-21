package patricklove.com.snowdayalarm.activities.mainTabFragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import patricklove.com.snowdayalarm.alarmTools.AlarmTemplateListAdapter;
import patricklove.com.snowdayalarm.database.AlarmTemplateInterface;
import patricklove.com.snowdayalarm.database.models.AlarmTemplate;

/**
 * A fragment representing a list of Items.
 * <p/>
 * <p/>
 * interface.
 */
public class AlarmTemplateFragment extends ListFragment implements Refreshable{


    private AlarmTemplateListAdapter adapter;

    public static AlarmTemplateFragment newInstance() {
        AlarmTemplateFragment fragment = new AlarmTemplateFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AlarmTemplateFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adapter = new AlarmTemplateListAdapter(getActivity(), getAlarmList());
        this.setListAdapter(adapter);
    }

    private List<AlarmTemplate> getAlarmList(){
        AlarmTemplateInterface dbHelp = new AlarmTemplateInterface(getActivity().getApplicationContext());
        dbHelp.open();
        List<AlarmTemplate> allAlarms = dbHelp.getAll();
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
