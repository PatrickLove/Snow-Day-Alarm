package twitter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import twitter4j.Status;

/**
 * Tools which takes a set of tweets and will analyze them to determine snow days, 2hr delays, etc.
 * Resulting data is in the form of a {@link TweetAnalysis} object
 * 
 * @author Patrick Love
 *
 * @see TweetAnalysis
 */
public class TweetAnalyzer {
	
	public static TweetAnalyzer getDefault(){
		return new TweetAnalyzer(
			KeywordSet.MASTER_CANCEL_FILTER,
			KeywordSet.MASTER_DELAY_FILTER,
			new String[] {"\n","now","two hour","2 hour"}
		);
	}
	
	private KeywordSet filterCancel;
	private String[] removeWords = new String[0];
	private KeywordSet filterDelay;
	
	public TweetAnalyzer(KeywordSet filterCancel, KeywordSet filterDelay){
		this.filterCancel = filterCancel;
		this.filterDelay = filterDelay;
	}
	
	public TweetAnalyzer(KeywordSet filterCancel, KeywordSet filterDelay, String[] removed){
		this(filterCancel, filterDelay);
		this.removeWords = removed;
	}
	
	public List<TweetAnalysis> analyzeTweetGroup(List<Status> tweets){
		List<TweetAnalysis> ret = new ArrayList<>();
		for(Status s : tweets){
			ret.add(this.analyzeTweet(s));
		}
		return ret;
	}
	
	public TweetAnalysis analyzeTweet(Status tweet){
		TweetAnalysis analysis = new TweetAnalysis();
		analysis.setDayState(DayState.NO_CHANGE);
		String text = tweet.getText().toLowerCase();
		analysis.setTweetText(text);
		if(filterCancel.isTriggered(text)){
			analysis.setDayState(DayState.CANCELLATION);
			analysis = analyzeDate(text, analysis);
		}
		else if(filterDelay.isTriggered(text)){
			analysis.setDayState(DayState.DELAY_2_HR);
			analysis = analyzeDate(text, analysis);
		}
		return analysis;
	}
	
	private TweetAnalysis analyzeDate(String text, TweetAnalysis analysis){
		Parser dateParser = new Parser();
		for(String word : removeWords){
			text = text.replaceAll(word, "");
		}
		List<DateGroup> groups = dateParser.parse(text);
		if(groups.size() == 0){
			return null;
		}
		DateGroup date = groups.get(0);
		analysis.setDate(date.getDates().get(0));
		analysis.setDateSource(date.getText());
		return analysis;
	}
}
