package com.patricklove.snowdayalarm.database.models;

import com.patricklove.snowdayalarm.database.SpecialDayInterface;
import com.patricklove.snowdayalarm.twitter.DayState;

import java.util.Date;

/**
 * Stores a Java Date object and a {@link com.patricklove.snowdayalarm.twitter.DayState} to represent a closing or cancellation
 * @author Patrick Love
 *
 */
public class SpecialDate {
	private Date date;
    private long id = -1;

    public long getId() {
        return id;
    }
	
	public Date getDate() {
		return date;
	}
	public DayState getState() {
		return state;
	}
	
	private DayState state;
	
	public SpecialDate(long id, Date date, DayState s){
		this.date = date;
		this.state = s;
	}

    public SpecialDate(Date date, DayState s){
        this(0, date, s);
    }
	
	public boolean isCancelled(){
		return state == DayState.CANCELLATION;
	}
	public boolean isDelay(){
		return state == DayState.DELAY_2_HR;
	}

    public void save(SpecialDayInterface dbInterface) {
        this.id = dbInterface.add(this);
    }
}
