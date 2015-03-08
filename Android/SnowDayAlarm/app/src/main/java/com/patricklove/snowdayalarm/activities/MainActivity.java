package patricklove.com.snowdayalarm.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.Locale;

import patricklove.com.snowdayalarm.R;
import patricklove.com.snowdayalarm.activities.mainTabFragments.AlarmTemplateFragment;
import patricklove.com.snowdayalarm.activities.mainTabFragments.DaysAlarmsFragment;
import patricklove.com.snowdayalarm.activities.mainTabFragments.Refreshable;
import patricklove.com.snowdayalarm.alarmTools.AlarmTemplateListAdapter;
import patricklove.com.snowdayalarm.database.CleanupJob;
import patricklove.com.snowdayalarm.utils.FileUtils;


public class MainActivity extends ActionBarActivity implements ActionBar.TabListener, AlarmTemplateListAdapter.OnEnableChangeListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new RefreshStatesTask(this).execute();

        FileUtils fileReader = new FileUtils(this);
        if(!fileReader.getHasRun()){
            Log.i("FirstLaunch", "Performing first launch setup");
            CleanupJob.schedule(this.getApplicationContext());
            fileReader.setHasRun(true);
        }

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    @Override
    protected void onStart() {
        refreshAllLists();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add_alarm){
            Intent addIntent = new Intent(this, EditAlarmActivity.class);
            this.startActivity(addIntent);
//            AlarmScheduler scheduler = new AlarmScheduler(this);
//            scheduler.open();
//            DailyAlarmInterface dbInterface = new DailyAlarmInterface(this.getApplicationContext());
//            AlarmTemplateInterface alarmInterface = new AlarmTemplateInterface(this.getApplicationContext());
//            Log.d("TEST_CODE", "Creating template");
//            alarmInterface.open();
//            AlarmTemplate temp = new AlarmTemplate("Test Template", AlarmAction.NO_CHANGE, AlarmAction.NO_CHANGE, new Date(512), true, false, true, false, true, false, true);
//            temp.save(alarmInterface);
//            alarmInterface.close();
//            Log.d("TEST_CODE", "Creating alarm 1");
//            Calendar now = DateUtils.dateToCal(DateUtils.getNow());
//            now.add(Calendar.SECOND, 15);
//            DailyAlarm testAlarm1 = new DailyAlarm("Test Alarm", now.getTime(), AlarmAction.NO_CHANGE, temp);
////        Log.d("TEST_CODE", "Creating alarm 2");
////        now.add(Calendar.SECOND, 15);
////        DailyAlarm testAlarm2 = new DailyAlarm(now.getTime(), AlarmAction.NO_CHANGE, temp);
//            dbInterface.open();
//            testAlarm1.save(dbInterface);
//            Log.d("TEST_CODE", "" + testAlarm1.getId());
////        testAlarm2.save(dbInterface);
////        Log.d("TEST_CODE", ""+testAlarm2.getId());
//            dbInterface.close();
//            scheduler.close();
//            Log.d("TEST_CODE", "Scheduling 1");
//            Log.d("TEST CODE", Boolean.toString(scheduler.schedule(testAlarm1)));
////        Log.d("TEST_CODE", "Scheduling 2");
////        Log.d("TEST CODE", Boolean.toString(scheduler.schedule(testAlarm2)));
//            refreshAllLists();
        }
        if(id == R.id.action_refresh_states){
            new RefreshStatesTask(this).execute();
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshAllLists(){
        List<Fragment> frags = getSupportFragmentManager().getFragments();
        if(frags != null) {
            for (Fragment frag : frags) {
                if (frag instanceof Refreshable) {
                    ((Refreshable) frag).refresh();
                }
            }
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onEnabledStateChanged() {
        refreshAllLists();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return DaysAlarmsFragment.newInstance();
                case 1:
                    return AlarmTemplateFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }
}
