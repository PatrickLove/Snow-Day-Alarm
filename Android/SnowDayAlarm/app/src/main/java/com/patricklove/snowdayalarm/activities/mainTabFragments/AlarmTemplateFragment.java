package com.patricklove.snowdayalarm.activities.mainTabFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.patricklove.snowdayalarm.R;
import com.patricklove.snowdayalarm.activities.EditAlarmActivity;
import com.patricklove.snowdayalarm.activities.MainActivity;
import com.patricklove.snowdayalarm.alarmTools.AlarmTemplateListAdapter;
import com.patricklove.snowdayalarm.database.AlarmTemplateInterface;
import com.patricklove.snowdayalarm.database.SnowDayDatabase;
import com.patricklove.snowdayalarm.database.models.AlarmTemplate;

import java.util.List;

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

        adapter = new AlarmTemplateListAdapter(getActivity(), getAlarmList(), (MainActivity) getActivity());
        this.setListAdapter(adapter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        registerForContextMenu(this.getListView());
        this.setEmptyText(getString(R.string.message_no_templates));
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.equals(getListView())) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.alarm_longclick_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        long alarmId = ((AdapterView.AdapterContextMenuInfo)item.getMenuInfo()).id;
        if(item.getItemId()==R.id.edit_menu_item){
            startEditActivity(alarmId);
        }
        else if(item.getItemId()==R.id.delete_menu_item){
            AlarmTemplateInterface dbHelp = new AlarmTemplateInterface(getActivity().getApplicationContext());
            dbHelp.open();
            AlarmTemplate template = dbHelp.query(SnowDayDatabase.idEquals(alarmId)).get(0);
            dbHelp.delete(template);
            dbHelp.close();
            ((MainActivity)getActivity()).refreshAllLists();
        }
        return super.onContextItemSelected(item);
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
        startEditActivity(id);
        super.onListItemClick(l, v, position, id);
    }

    private void startEditActivity(long id){
        Intent intent = new Intent(this.getActivity(), EditAlarmActivity.class);
        intent.putExtra(EditAlarmActivity.EXTRA_ALARM_ID, id);
        this.startActivity(intent);
    }
}
