package patricklove.com.snowdayalarm.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by Patrick Love on 2/20/2015.
 */
public class FileUtils {
    public static final String LAST_UPDATE_FILE = "lastUpdate";
    public static final String LAST_TWEET_FILE = "lastTweet";

    private Context c;

    public FileUtils(Context c){
        this.c = c;
    }

    public boolean setLastTweet(String tweet){
        return writeFile(LAST_TWEET_FILE, tweet);
    }

    public boolean setLastUpdate(Date d){
        String longStr = d.getTime() + "";
        return writeFile(LAST_UPDATE_FILE, longStr);
    }

    public Date readLastUpdate(){
        String file = readFile(LAST_UPDATE_FILE);
        try {
            return new Date(Long.parseLong(file));
        } catch (NumberFormatException e){
            return null;
        }
    }

    public String readLastTweet(){
        return readFile(LAST_TWEET_FILE);
    }

    private String readFile(String fileName){
        try {
            FileInputStream in = c.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(isr);
            String file = "";
            String line;
            while((line = reader.readLine()) != null){
                file += line + "\n";
            }
            reader.close();
            isr.close();
            in.close();
            if(file.equals("")){
                return null;
            }
            return file.substring(0, file.length()-1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";  //This ensures that if we are checking for a recent tweet when it does not exist, we will initially load all tweets
                        //most of which will quickly be removed since they are before now
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    private boolean writeFile(String fileName, String text){
        try {
            FileOutputStream out = c.openFileOutput(fileName, Context.MODE_PRIVATE);
            out.write(text.getBytes());
            out.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
