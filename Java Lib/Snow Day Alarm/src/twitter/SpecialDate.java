package twitter;

import java.util.Date;

public class SpecialDate {
	public Date date;
	public DayState state;
	public SpecialDate(Date d, DayState s){
		this.date = d;
		this.state = s;
	}
	
	public boolean isCancelled(){
		return state.equals(DayState.CANCELLATION);
	}
	public boolean isDelay(){
		return state.equals(DayState.DELAY_2_HR);
	}
}
