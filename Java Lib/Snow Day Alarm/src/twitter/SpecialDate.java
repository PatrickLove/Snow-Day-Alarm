package twitter;

import java.util.Calendar;

/**
 * Stores a Java Date object and a {@link DayState} to represent a closing or cancellation
 * @author Patrick Love
 *
 */
public class SpecialDate {
	private Calendar date;
	
	public Calendar getDate() {
		return date;
	}
	public DayState getState() {
		return state;
	}
	
	private DayState state;
	
	public SpecialDate(Calendar d, DayState s){
		this.date = Calendar.getInstance();
		date.setTimeInMillis(d.getTimeInMillis());
		this.state = s;
	}
	
	public boolean isCancelled(){
		return state == DayState.CANCELLATION;
	}
	public boolean isDelay(){
		return state == DayState.DELAY_2_HR;
	}
}
