package twitter;

import java.io.IOException;
import java.util.List;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class SnowDayTwitter {
			
	public static final String OAUTH_CONSUMER_KEY = "fKic7d6P8ZriFr1uZTzgHMFTk";
	public static final String OAUTH_CONSUMER_SECRET = "cmdvFAfTUqFmJTMGzuxGrrkfC99lCP5trG2Pxi4kX7ygs3NlZe";
	public static final String OAUTH_ACCESS_KEY = "3019016038-KL0Zz4TWE23Of8RTMsmpSuOkUu8v7x4tuK0ddH4";
	public static final String OAUTH_ACCESS_SECRET = "TjAIqQwyh9GSfWVGOAJfHfZBI6xwwj8FQE53ERK6QHxYE";
	
	public static void main(String[] args) throws IOException, TwitterException {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setOAuthConsumerKey(OAUTH_CONSUMER_KEY);
		cb.setOAuthConsumerSecret(OAUTH_CONSUMER_SECRET);
		cb.setOAuthAccessToken(OAUTH_ACCESS_KEY);
		cb.setOAuthAccessTokenSecret(OAUTH_ACCESS_SECRET);
		
		Twitter twit = new TwitterFactory(cb.build()).getInstance();
		Paging pages = new Paging(1, 200);
		List<Status> statuses = twit.getUserTimeline("CBSDInfo", pages);
		for(Status s : statuses){
			String tweet = s.getText().toLowerCase();
			if((tweet.contains("will be closed") ||
					tweet.contains("are closed"))&&
					tweet.contains("all")){
				Parser p = new Parser();
				System.out.println(tweet);
				List<DateGroup> dates = p.parse(tweet.replace("now", ""));
				if(dates.size()>0){
						for(DateGroup d : dates){
						System.out.println("There was a snow day on " + d.getDates().get(0).toString());
						System.out.println("This information was pulled from \"" + d.getText() + "\"\n");
					}
				}
			}
				
		}
	}
}
