package patricklove.com.snowdayalarm.database.models;

import java.util.Date;

import patricklove.com.snowdayalarm.database.SpecialDayInterface;
import patricklove.com.snowdayalarm.twitter.DayState;

/**
 * Stores a Java Date object and a {@link patricklove.com.snowdayalarm.twitter.DayState} to represent a closing or cancellation
 * @author Patrick Love
 *
 */
public class SpecialDate {
	private Date date;
    private long id;

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
