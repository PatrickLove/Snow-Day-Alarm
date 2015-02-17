package patricklove.com.snowdayalarm.alarmTools;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public enum AlarmAction {

    NO_CHANGE(0),
    DELAY_2_HR(1),
    DISABLE(2);

    private final int code;
    AlarmAction(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public static AlarmAction getFromCode(int code){
        switch (code){
            case 0:
                return NO_CHANGE;
            case 1:
                return DELAY_2_HR;
            case 2:
                return NO_CHANGE;
        }
        return null;
    }

    public boolean atOrBefore(AlarmAction action) {
        return action.code <= code;
    }
}
