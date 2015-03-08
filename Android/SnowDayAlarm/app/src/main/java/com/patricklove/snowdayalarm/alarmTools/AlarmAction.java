package com.patricklove.snowdayalarm.alarmTools;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

/**
 * Created by Patrick Love on 2/14/2015.
 */
public enum AlarmAction {

    NO_CHANGE(0, "Ring on time"),
    DELAY_2_HR(1, "Delay 2 hours"),
    DISABLE(2, "Cancel for the day");

    private final int code;
    private final String name;
    AlarmAction(int code, String name){
        this.code = code;
        this.name = name;
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
                return DISABLE;
        }
        return null;
    }



    public boolean atOrBefore(AlarmAction action) {
        return this.code <= action.code;
    }

    @Override
    public String toString() {
        return name;
    }

    public static SpinnerAdapter getSpinnerAdapter(Context c){
        return new ArrayAdapter<AlarmAction>(c, android.R.layout.simple_spinner_dropdown_item,
                new AlarmAction[] {NO_CHANGE, DELAY_2_HR, DISABLE});
    }
}
