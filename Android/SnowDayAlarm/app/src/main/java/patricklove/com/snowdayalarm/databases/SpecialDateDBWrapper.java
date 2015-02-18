package patricklove.com.snowdayalarm.databases;

import java.util.Calendar;
import java.util.Date;

import twitter.DayState;
import twitter.SpecialDate;

/**
 * Created by Patrick Love on 2/17/2015.
 */
public class SpecialDateDBWrapper extends SpecialDate  {

    private long id;

    public long getId() {
        return id;
    }


    public SpecialDateDBWrapper(long id, Calendar date, DayState state){
        super(date, state);
        this.id = id;
    }

    public SpecialDateDBWrapper(long id, SpecialDate special){
        this(id, special.getDate(), special.getState());
    }
}
