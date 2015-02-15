package twitter;

import java.util.Date;

/**
 * Stores a Java Date object and a {@link DayState} to represent a closing or cancelation
 * @author Patrick Love
 *
 */
public class SpecialDate {
	public Date date;
	public DayState state;
	public SpecialDate(Date d, DayState s){
		this.date = d;
		this.state = s;
	}
	
	public boolean isCancelled(){
		return state == DayState.CANCELLATION;
	}
	public boolean isDelay(){
		return state == DayState.DELAY_2_HR;
	}
}
