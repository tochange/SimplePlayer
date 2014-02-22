package app.android.simpleplayer.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class ExitAllActivityApplication extends Application
{
    private List<Activity> activityList = new LinkedList<Activity>();

    private static ExitAllActivityApplication instance;

    public static ExitAllActivityApplication getInstance()
    {
        if (null == instance)
        {
            instance = new ExitAllActivityApplication();
        }
        return instance;
    }

    public void addActivity(Activity a)
    {
        activityList.add(a);

    }

    public void exit()
    {
        for (Activity a : activityList)
        {
            a.finish();
        }
        System.exit(0);
    }
}
