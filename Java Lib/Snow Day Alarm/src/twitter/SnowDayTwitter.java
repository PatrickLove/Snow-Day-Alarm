package twitter;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class SnowDayTwitter {
			
	public static final String OAUTH_CONSUMER_KEY = "fKic7d6P8ZriFr1uZTzgHMFTk";
	public static final String OAUTH_CONSUMER_SECRET = "cmdvFAfTUqFmJTMGzuxGrrkfC99lCP5trG2Pxi4kX7ygs3NlZe";
	public static final String OAUTH_ACCESS_KEY = "3019016038-KL0Zz4TWE23Of8RTMsmpSuOkUu8v7x4tuK0ddH4";
	public static final String OAUTH_ACCESS_SECRET = "TjAIqQwyh9GSfWVGOAJfHfZBI6xwwj8FQE53ERK6QHxYE";
	
	public static final String TARGET_USERNAME = "CBSDInfo";
	
	private static final Configuration CONFIG;
	static {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(OAUTH_CONSUMER_KEY);
		cb.setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET);
		cb.setOAuthAccessToken(OAUTH_ACCESS_KEY);
		cb.setOAuthAccessTokenSecret(OAUTH_ACCESS_SECRET);
		CONFIG = cb.build();
	}
	
	public static void main(String[] args) {
		List<SpecialDate> dates = analyzeRecent(300);
		for(SpecialDate date : dates){
			if(date.isCancelled()){
				System.out.println("    There was a cancellation on " + date.date);
			}
			else if(date.isDelay()){
				System.out.println("    There was a delay on " + date.date);
			}
		}
	}
	
	
	public static List<SpecialDate> analyzeRecent(int num){
		Twitter twit = new TwitterFactory(CONFIG).getInstance();
		List<Status> allTweets = new ArrayList<>();
		Paging page;
		int pages = (num/200) + 1;
		int remainderTweets = num%200;
		if(remainderTweets == 0){
			pages--;
			remainderTweets = 201;
		}
		int tweetCount;
		for(int i = 1; i <= pages; i++){
			page = new Paging(i, 200);
			try{
				List<Status> pageTweets = twit.getUserTimeline(TARGET_USERNAME, page);
				tweetCount = pageTweets.size();
				if(i == pages){
					tweetCount = (tweetCount < remainderTweets) ? tweetCount : remainderTweets;
				}
				for(int j = 0; j < tweetCount; j++){
					allTweets.add(pageTweets.get(j));
				}
				if(i < pages && tweetCount < 200){ 	//Stop paging if there wasn't the max on this page and it isn't the last
					break;							//The next pages will just be empty and not worth retrieving
				}
			}
			catch(TwitterException e){
				e.printStackTrace();
				break;
			}
		}
		
		TweetAnalyzer analyzer = TweetAnalyzer.getDefault();
		return TweetAnalysis.daysFromAnalysis(analyzer.analyzeTweetGroup(allTweets));
	}
	
}
