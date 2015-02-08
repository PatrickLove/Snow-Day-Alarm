package twitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import twitter4j.Status;

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
	public DayState getDayState() {
		return dayState;
	}
	public int getDayStateCode() {
		return dayState.code;
	}
	public void setDayState(DayState dayState) {
		this.dayState = dayState;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getDateSource() {
		return dateSource;
	}
	public void setDateSource(String dateSource) {
		this.dateSource = dateSource;
	}
	public boolean isCancelled(){
		return dayState.equals(DayState.CANCELLATION);
	}
	public boolean isDelay(){
		return dayState.equals(DayState.DELAY_2_HR);
	}
	public SpecialDate getSpecialDate(){
		return new SpecialDate(date, dayState);
	}
	
	public static List<SpecialDate> daysFromAnalysis(List<TweetAnalysis> list){
		List<SpecialDate> ret = new ArrayList<>();
		for(TweetAnalysis analysis : list){
			if(!analysis.dayState.equals(DayState.NO_CHANGE)){
				ret.add(analysis.getSpecialDate());
			}
		}
		return ret;
	}
}
