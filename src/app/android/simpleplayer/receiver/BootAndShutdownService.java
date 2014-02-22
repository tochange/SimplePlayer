package app.android.simpleplayer.receiver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import app.android.simpleplayer.log.MyLog;

public class BootAndShutdownService extends Service
{
    String TAG = "BootAndShutdownService";

    @Override
    public void onCreate()
    {
        Log.e(TAG, ".....................start..3..5...");
        super.onCreate();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        Log.e(TAG, ".....................start..5..5...");
        MyLog.e(".....................start.....4..");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, ".....................start..53..5...");
        MyLog.e(".....................start..1.....");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        Log.e(TAG, ".....................start...2.5...");
        MyLog.e(".....................start....3...");
        return null;
    }

}