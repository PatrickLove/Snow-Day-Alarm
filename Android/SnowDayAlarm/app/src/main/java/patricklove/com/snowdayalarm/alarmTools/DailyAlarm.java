package patricklove.com.snowdayalarm.alarmTools;

import java.util.Calendar;

import twitter.DayState;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public class DailyAlarm {

    private long id;

    public long getId() {
        return id;
    }

    public Calendar getTriggerTime() {
        return triggerTime;
    }

    public AlarmAction getState() {
        return state;
    }

    public AlarmTemplate getAssociatedAlarm() {
        return associatedAlarm;
    }

    private Calendar triggerTime;
    private AlarmAction state;
    private AlarmTemplate associatedAlarm;

    public DailyAlarm(long id, Calendar time, AlarmAction state, AlarmTemplate alarm){
        this.id = id;
        this.state = state;
        this.triggerTime = time;
        this.associatedAlarm = alarm;
    }

    public Calendar updateTime(DayState state){
        AlarmAction action = associatedAlarm.getAction(state);
        if(action == AlarmAction.DISABLE){
            return null;
        }
        if(action == AlarmAction.DELAY_2_HR){
            Calendar ret = associatedAlarm.getTime();
            ret.add(Calendar.HOUR, 2);
            return ret;
        }
        return associatedAlarm.getTime();
    }

    public boolean isCancelled(){
        return triggerTime == null;
    }
}
