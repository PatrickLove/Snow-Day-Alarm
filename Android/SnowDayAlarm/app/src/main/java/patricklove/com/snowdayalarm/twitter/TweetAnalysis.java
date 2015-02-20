package patricklove.com.snowdayalarm.twitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class TweetAnalysis {

	private String tweetText;
	private DayState dayState;
	private Date date;
	private String dateSource;
	public String getTweetText() {
		return tweetText;
	}
	public void setTweetText(String tweetText) {
		this.tweetText = tweetText;
	}
	/**
	 * @return DayState indicated by the tweet
	 */
	public DayState getDayState() {
		return dayState;
	}
	/**
	 * @return Code of the day's state (as per {@link DayState#code})
	 */
	public int getDayStateCode() {
		return dayState.code;
	}
	public void setDayState(DayState dayState) {
		this.dayState = dayState;
	}
	/**
	 * @return Date when the given state will be in effect (or null if dayState is NO_CHANGE)
	 */
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	/**
	 * @return The subsection of the tweet text from which the date was pulled
	 */
	public String getDateSource() {
		return dateSource;
	}
	public void setDateSource(String dateSource) {
		this.dateSource = dateSource;
	}
	public boolean isCancelled(){
		return dayState == DayState.CANCELLATION;
	}
	public boolean isDelay(){
		return dayState == DayState.DELAY_2_HR;
	}
	/**
	 * @return SpecialDate object containing this analysis' date and state
	 */
	public SpecialDate getSpecialDate(){
		return new SpecialDate(date, dayState);
	}
	
	/**
	 * @param list List of analyses to pull SpecialDays from
	 * @return List of special days with a dayState other than NO_CHANGE contained in list.
	 */
	public static List<SpecialDate> daysFromAnalysis(List<TweetAnalysis> list){
		List<SpecialDate> ret = new ArrayList<>();
		for(TweetAnalysis analysis : list){
			if(analysis.dayState != DayState.NO_CHANGE){
				ret.add(analysis.getSpecialDate());
			}
		}
		return ret;
	}
}
