package patricklove.com.snowdayalarm.twitter;

import java.util.Date;

/**
 * Stores a Java Date object and a {@link DayState} to represent a closing or cancellation
 * @author Patrick Love
 *
 */
public class SpecialDate {
	private Date date;
	
	public Date getDate() {
		return date;
	}
	public DayState getState() {
		return state;
	}
	
	private DayState state;
	
	public SpecialDate(Date date, DayState s){
		this.date = date;
		this.state = s;
	}
	
	public boolean isCancelled(){
		return state == DayState.CANCELLATION;
	}
	public boolean isDelay(){
		return state == DayState.DELAY_2_HR;
	}
}
