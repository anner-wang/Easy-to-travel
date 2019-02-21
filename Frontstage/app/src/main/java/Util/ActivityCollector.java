package Util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

public class ActivityCollector {

    private static HashMap<String,Activity> activities = new HashMap<String,Activity>();

    public static void addActivity(String activityName ,Activity activity){
        activities.put(activityName,activity);
    }

    public static Activity removeActivity(String activityName){
        return activities.remove(activityName);
    }

    public static void finishActivity(String activityName){
        Activity activity = removeActivity(activityName);
        activity.finish();
    }

    public static boolean isContains(String activityName) {
        return activities.containsKey(activityName);
    }
}
