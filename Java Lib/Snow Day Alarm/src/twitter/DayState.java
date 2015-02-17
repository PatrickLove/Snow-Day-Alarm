package twitter;

/**
 * Data type referring to the state of a school day<br>
 * Can hold the following values
 * <ol>
 * 	<li>NO_CHANGE</li>
 * 	<li>DELAY_2_HR</li>
 * 	<li>CANCELLATION</li>
 * </ol>
 * @author Patrick Love
 *
 */
public enum DayState {

	CANCELLATION(2),
	DELAY_2_HR(1),
	NO_CHANGE(0);
	/**
	 * Unique code for each type of DayState
	 */
	public final int code;
	DayState(int code){
		this.code = code;
	}
	
	public static DayState getFromCode(int code){
		switch(code){
			case 0:
				return NO_CHANGE;
			case 1:
				return DELAY_2_HR;
			case 2:
				return CANCELLATION;
		}
		return NO_CHANGE;
	}
}
