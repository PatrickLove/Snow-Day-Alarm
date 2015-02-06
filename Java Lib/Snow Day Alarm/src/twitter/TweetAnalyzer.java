package twitter;

import java.util.List;

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
	
	
	private List<Status> rawTweets;

	public TweetAnalyzer(List<Status> tweets){
		this.rawTweets = tweets;
	}
	
	public TweetAnalysis analyze(){
		TweetAnalysis analysis = new TweetAnalysis();
		
		return analysis;
	}
	
	/**
	 * Returns analysis of all more recent tweets than the given one (exclusive)
	 * @param recentTweet The body of the most recent known tweet
	 * @return TweetAnalysis of all more recent tweets
	 * 
	 * @see TweetAnalysis
	 */
	public TweetAnalysis analyzeFrom(String recentTweet){
		TweetAnalysis analysis = new TweetAnalysis();
		
		return analysis;
	}
	
	public static TweetAnalysis analyzeTweet(Status tweet){
		return analyzeTweet(tweet, new TweetAnalysis());
	}
	
	public static TweetAnalysis analyzeTweet(Status tweet, TweetAnalysis analysis){
		
	}
}