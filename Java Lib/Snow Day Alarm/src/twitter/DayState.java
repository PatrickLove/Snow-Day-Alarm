package twitter;

public enum DayState {

	CANCELLATION(2),
	DELAY_2_HR(1),
	NO_CHANGE(0);
	public final int code;
	DayState(int code){
		this.code = code;
	}
	
	public boolean equals(DayState d){
		return d.code == this.code;
	}
}
