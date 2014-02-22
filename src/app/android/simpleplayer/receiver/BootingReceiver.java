package app.android.simpleplayer.receiver;

import java.io.DataOutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootingReceiver extends BroadcastReceiver
{

    public final static String TAG = "BootingReceiver";

    private final String BOOT_ACTION = "android.intent.action.BOOT_COMPLETED";

    public static final String GET_PATH_ACTION = "android.intent.action.SEND";

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        Log.e(TAG, ".......onReceive message:" + intent.getAction());

        if (intent.getAction().equals(GET_PATH_ACTION))
        {

            new Thread(new Runnable() {
                @Override
                public void run()
                {
                }
            }).start();

        }
        else if (intent.getAction().equals(BOOT_ACTION))
        {
            Log.e(TAG, ".......onReceive:" + intent.getAction());
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // AlarmManager am = (AlarmManager) context
            // .getSystemService(Context.ALARM_SERVICE);
            // Intent ii = new Intent(
            // "com.android.settings.action.REQUEST_POWER_OFF");
            // PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
            // 0, ii, PendingIntent.FLAG_CANCEL_CURRENT);
            // am = (AlarmManager)
            // context.getSystemService(Context.ALARM_SERVICE);
            // am.set(AlarmManager.RTC_WAKEUP, 1000, pendingIntent);

            // Intent i = new Intent(Intent.ACTION_SHUTDOWN);
            // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // context.startActivity(i);

//            PowerManager pm = (PowerManager) context
//                    .getSystemService(Context.POWER_SERVICE);
//            pm.reboot("reboot -p");

//             ShellUtil.runRootCmd("reboot -p");
            // ShellUtil.runCommand("poweroff");
            
            
            try {
                Process p = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(p.getOutputStream());    
                os.writeBytes("reboot");
                os.flush();
                os.close();                 
                p.waitFor();
                p.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
            
//            try
//            {
//                RootTools.sendShell("reboot -p", 1000);
//            }
//            catch (IOException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            catch (RootToolsException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            catch (TimeoutException e)
//            {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            // try
            // {
            //
            // // ���ServiceManager��
            // Class<?> ServiceManager = Class
            // .forName("android.os.ServiceManager");
            //
            // // ���ServiceManager��getService����
            // Method getService = ServiceManager.getMethod("getService",
            // java.lang.String.class);
            //
            // // ����getService��ȡRemoteService
            // Object oRemoteService = getService.invoke(null,
            // Context.POWER_SERVICE);
            //
            // // ���IPowerManager.Stub��
            // Class<?> cStub = Class.forName("android.os.IPowerManager$Stub");
            // // ���asInterface����
            // Method asInterface = cStub.getMethod("asInterface",
            // android.os.IBinder.class);
            // // ����asInterface������ȡIPowerManager����
            // Object oIPowerManager = asInterface
            // .invoke(null, oRemoteService);
            // // ���shutdown()����
            // Method shutdown = oIPowerManager.getClass().getMethod(
            // "shutdown", boolean.class, boolean.class);
            // // ����shutdown()����
            // shutdown.invoke(oIPowerManager, false, true);
            //
            // }
            // catch (Exception e)
            // {
            // Log.e(TAG, e.toString(), e);
            // }

        }

    }

}
