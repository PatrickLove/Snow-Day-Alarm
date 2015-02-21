package patricklove.com.snowdayalarm.twitter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.util.Log;

import java.util.List;

import patricklove.com.snowdayalarm.database.SpecialDayInterface;
import patricklove.com.snowdayalarm.database.models.SpecialDate;
import patricklove.com.snowdayalarm.twitter.tweetAnalysis.TweetAnalysis;
import patricklove.com.snowdayalarm.twitter.tweetAnalysis.TweetAnalyzer;
import patricklove.com.snowdayalarm.utils.DateUtils;
import patricklove.com.snowdayalarm.utils.FileUtils;
import twitter4j.Status;

/**
 * Created by Patrick Love on 2/20/2015.
 */
public class TwitterAnalysisBridge {

    private static final String LOG_TAG = "TwitterAnalysisBridge";

    private Context c;

    public TwitterAnalysisBridge(Context c){
        this.c = c;
    }

    public boolean updateSpecialDays(){
        ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getActiveNetworkInfo().isConnected()) {
            List<SpecialDate> dates = getAnalysis();
            if(dates == null){
                return false;
            }
            if(dates.size() != 0) {
                SpecialDayInterface dbInterface = new SpecialDayInterface(c);
                dbInterface.open();
                for (SpecialDate date : dates) {
                    date.save(dbInterface);
                }
                dbInterface.close();
            }
            return true;
        }
        else{
            Log.w(LOG_TAG, "Device not connected to the internet, tweets for this alarm are not up to date");
            return false;
        }
    }

    private List<SpecialDate> getAnalysis(){
        FileUtils fileManager = new FileUtils(c);
        TweetAnalyzer analyzer = TweetAnalyzer.getDefault();
        int attempts = 0;
        String lastTweet;
        do {
            lastTweet = fileManager.readLastTweet();
            attempts++;
        } while(lastTweet == null && attempts <= 20);
        if(lastTweet == null){
            Log.e(LOG_TAG, "Recent tweet failed to load from file after 20 attempts");
            return null;
        }
        List<Status> allTweets = CBSDTwitter.getTweetsSince(lastTweet);
        if(allTweets.size()!=0){
            fileManager.setLastTweet(allTweets.get(0).getText());
        }
        fileManager.setLastUpdate(DateUtils.getNow());
        return TweetAnalysis.daysFromAnalysis(analyzer.analyzeTweetGroup(allTweets));
    }
}
