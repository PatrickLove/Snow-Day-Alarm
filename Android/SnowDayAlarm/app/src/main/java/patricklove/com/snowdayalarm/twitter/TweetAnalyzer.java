package patricklove.com.snowdayalarm.twitter;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import java.util.ArrayList;
import java.util.List;

import patricklove.com.snowdayalarm.database.models.SpecialDate;
import twitter4j.Status;

/**
 * Tool which takes a set of tweets and will analyze them to determine snow days, 2hr delays, etc.
 * Resulting data is in the form of a {@link TweetAnalysis} object.
 * 
 * @author Patrick Love
 *
 * @see TweetAnalysis
 */
public class TweetAnalyzer {
	
	/**
	 * Returns a TweetAnalyzer with the default settings, recommended unless your are doing something weird
	 * @return Default TweetAnalyzer
	 */
	public static TweetAnalyzer getDefault(){
		return new TweetAnalyzer(
			KeywordSet.MASTER_CANCEL_FILTER,
			KeywordSet.MASTER_DELAY_FILTER,
			new String[] {"\n","now","two hour","2 hour"}
		);
	}
	
	/**
	 * KeywordSet filter to trigger on a delay
	 */
	private KeywordSet filterDelay;
	/**
	 * KeywordSet filter to trigger on a cancellation
	 */
	private KeywordSet filterCancel;
	/**
	 * Words to be removed before the date parser pulls a date (used to remove potentially distracting words like "2 hour" or "now").
	 * These words WILL be present when matching to the KeywordSet filters
	 */
	private String[] removeWords = new String[0];

	
	public TweetAnalyzer(KeywordSet filterCancel, KeywordSet filterDelay){
		this.filterCancel = filterCancel;
		this.filterDelay = filterDelay;
	}
	
	public TweetAnalyzer(KeywordSet filterCancel, KeywordSet filterDelay, String[] removed){
		this(filterCancel, filterDelay);
		this.removeWords = removed;
	}
	
	/**
	 * Similar to {@link #analyzeTweet(Status)} except applied recursively over an array of Statuses
	 * @param tweets Array of tweets to be analyzed
	 * @return Array of TweetAnalyses for these tweets
	 */
	public List<TweetAnalysis> analyzeTweetGroup(List<Status> tweets){
		List<TweetAnalysis> ret = new ArrayList<>();
		for(Status s : tweets){
			ret.add(this.analyzeTweet(s));
		}
		return ret;
	}
	
	/**
	 * Analyzes a particular tweet for snow day information
	 * @param tweet Status object containing the tweet
	 * @return TweetAnalysis of tweet
	 */
	public TweetAnalysis analyzeTweet(Status tweet){
		TweetAnalysis analysis = new TweetAnalysis();
		analysis.setDayState(DayState.NO_CHANGE);
		String text = tweet.getText().toLowerCase();
		analysis.setTweetText(text);
		if(filterDelay.isTriggered(text)){ 	//This will ensure that if there is confustion as to a delay or cancellation,
											//that the delay is chosen (better for the alarm to sound 2hr late on a day
											//off than not to trigger on a delay day.
			analysis.setDayState(DayState.DELAY_2_HR);
			analysis = analyzeDate(text, analysis);
		}
		else if(filterCancel.isTriggered(text)){
			analysis.setDayState(DayState.CANCELLATION);
			analysis = analyzeDate(text, analysis);
		}
		return analysis;
	}
	
	/**
	 * Utility function for parsing the date to which a given tweet refers
	 * @param text Tweet text
	 * @param analysis TweetAnalysis to add date information to
	 * @return TweetAnalysis object containing the information from analysis plus date information
	 */
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
	
	/**
	 * Convenience method for identifying snow days
	 * Analyzes all tweets which are more recent than the given one, or all existing tweets if it does not exist.
	 * Returns only special days.  For more detailed analysis look at using a {@link TweetAnalyzer}.
	 * @param tweet Text of the most recent tweet which can be ignored
	 * @return List of all special dates which were detected in the given tweet range
	 */
	public static List<SpecialDate> specialDaysSince(String tweet){
		List<Status> allTweets = CBSDTwitter.getTweetsSince(tweet);
		TweetAnalyzer analyzer = TweetAnalyzer.getDefault();
		return TweetAnalysis.daysFromAnalysis(analyzer.analyzeTweetGroup(allTweets));
	}
	
	/**
	 * Convenience method for identifying snow days
	 * Analyzes the most recent n tweets for Special days, or every tweet if there are less than n tweets total
	 * @param num Number of recent tweets to analyze
	 * @return List of all special dates which were detected in the given tweet range
	 */
	public static List<SpecialDate> analyzeRecent(int num){
		List<Status> allTweets = CBSDTwitter.getRecent(num);
		TweetAnalyzer analyzer = TweetAnalyzer.getDefault();
		return TweetAnalysis.daysFromAnalysis(analyzer.analyzeTweetGroup(allTweets));
	}
}
