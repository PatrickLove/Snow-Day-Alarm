package com.patricklove.snowdayalarm.twitter;

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

    public boolean atOrBefore(DayState d){
        return this.code <= d.code;
    }


    @Override
    public String toString() {
        switch (code){
            case 2:
                return "Cancellation";
            case 1:
                return "Delay";
            case 0:
                return "Normal";
        }
        return "Unknown";
    }
}
