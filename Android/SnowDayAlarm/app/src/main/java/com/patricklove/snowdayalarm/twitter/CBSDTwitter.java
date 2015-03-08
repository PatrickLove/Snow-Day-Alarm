package com.patricklove.snowdayalarm.twitter;

import java.util.ArrayList;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Contains methods for retrieving tweets from @CBSDInfo
 * @author Patrick Love
 *
 */
public class CBSDTwitter {
			
	private static final String OAUTH_CONSUMER_KEY = "fKic7d6P8ZriFr1uZTzgHMFTk";
	private static final String OAUTH_CONSUMER_SECRET = "cmdvFAfTUqFmJTMGzuxGrrkfC99lCP5trG2Pxi4kX7ygs3NlZe";
	private static final String OAUTH_ACCESS_KEY = "3019016038-KL0Zz4TWE23Of8RTMsmpSuOkUu8v7x4tuK0ddH4";
	private static final String OAUTH_ACCESS_SECRET = "TjAIqQwyh9GSfWVGOAJfHfZBI6xwwj8FQE53ERK6QHxYE";
	
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
	
	public static List<Status> getTweetsSince(String tweet){
		Twitter twit = new TwitterFactory(CONFIG).getInstance();
		List<Status> allTweets = new ArrayList<>();
		Paging page;
		int i = 1;
		int tweetCount;
		grabPages:
		do{
			page = new Paging(i, 200);
			try{
				List<Status> pageTweets = twit.getUserTimeline(TARGET_USERNAME, page);
				tweetCount = pageTweets.size();
				for(int j = 0; j < tweetCount; j++){
					Status s = pageTweets.get(j);
					if(s.getText().equals(tweet)){
						break grabPages;
					}
					allTweets.add(s);
				}
			}
			catch(TwitterException e){
				e.printStackTrace();
				break;
			}
		} while (tweetCount == 200);
		return allTweets;
	}
}
