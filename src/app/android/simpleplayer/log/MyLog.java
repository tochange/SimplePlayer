package app.android.simpleplayer.log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.Log;

//Log.d(TAG,new Exception().getStackTrace()[0].getMethodName()); //函数名
//Log.d(TAG, Thread.currentThread().getStackTrace()[2].getMethodName()); //函数名
//Log.d(TAG, ""+Thread.currentThread().getStackTrace()[2].getLineNumber()); //行号
//Log.d(TAG, Thread.currentThread().getStackTrace()[2].getFileName()); //文件名
//
//Log.d(TAG, "["+Thread.currentThread().getStackTrace()[2].getFileName()
//      +","+Thread.currentThread().getStackTrace()[2].getLineNumber()+"]"); //文件名+行号

public class MyLog
{
    final static String TAG = "MyLogcatCapture";

    final static String[] LOGCATCMD_PREFIX = new String[] { "logcat", "-d",
            "-v", "time", "-s" };

    final static String[] LOGCATERRORCMD = new String[] { "logcat", "-d", "-v",
            "time", "-s", "AndroidRuntime:E", "-p" };

    final static String LOGPATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/mydebug";

    final static String LOGCONFIGPATH = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + "/mydebug/config";

    static String mSingleFileName = "";

    static boolean mLoopCaptureEnd = false;

    public static void e(String message)
    {
        Log.e(Thread.currentThread().getStackTrace()[3].getFileName().replace(
                ".java", ".")
                + Thread.currentThread().getStackTrace()[3].getMethodName()
                + ":"
                + Thread.currentThread().getStackTrace()[3].getLineNumber(),
                message);
    }

    private static boolean WriteLogToFile(String s, String filePath,
            String fileName)
    {
        boolean res = false;

        File logDir = new File(filePath);
        if (!logDir.exists())
        {
            logDir.mkdirs();
        }

        File f = new File(filePath + "/" + fileName);
        try
        {
            if (!f.exists())
            {
                f.createNewFile();
                Log.d(TAG, "create new file:" + filePath + "/" + fileName);
            }

            FileOutputStream fos = new FileOutputStream(f, true);
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(fos, "Unicode"));
            bufferedWriter.write(s);
            bufferedWriter.flush();
            bufferedWriter.close();
            fos.close();
            res = true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋嫹澶遍敓鏂ゆ嫹");
        }

        return res;
    }

    public static boolean CaptureException(final Context context)
    {
        new Thread() {
            @Override
            public void run()
            {
                super.run();
                CaptureAppException(context);
            }
        }.start();
        return true;
    }

    public static boolean CaptureAppException(Context context)
    {
        Process logCatProc = null;
        BufferedReader reader = null;
        boolean res = false;

        try
        {
            Log.d(TAG, "CaptureAppException Runtime.getRuntime().exec:"
                    + LOGCATERRORCMD);
            logCatProc = Runtime.getRuntime().exec(LOGCATERRORCMD);

            Log.d(TAG, "CaptureAppException waitFor Begin");
            logCatProc.waitFor();
            Log.d(TAG, "CaptureAppException waitFor End");
            reader = new BufferedReader(new InputStreamReader(
                    logCatProc.getInputStream()));
            String line;
            int lineCount = 0;
            final StringBuilder log = new StringBuilder();

            String separator = System.getProperty("line.separator");
            while ((line = reader.readLine()) != null)
            {
                log.append(line);
                log.append(separator);
                lineCount++;
            }
            Log.d(TAG, "CaptureAppException get exception log,count="
                    + lineCount);
            // --------- beginning of /dev/log/system
            // --------- beginning of /dev/log/main
            if (lineCount > 2)
            {
                Calendar c1 = Calendar.getInstance();
                String suffix = getCurTimeToString(c1, 0, 0);
                Log.d(TAG, "CaptureAppException WriteLogToFile");
                if (WriteLogToFile(log.toString(), LOGPATH, "log_" + suffix
                        + ".log"))
                {
                    Log.d(TAG,
                            "CaptureAppException Runtime.getRuntime().exec logcat -c");
                    Runtime.getRuntime().exec("logcat -c");
                    Log.d(TAG, "CaptureAppException res=true");
                    res = true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i("MyLogcatCapture",
                    "CaptureAppException exception=" + e.toString());
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    Log.d(TAG, "CaptureAppException reader close");
                    reader.close();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    Log.i(TAG, "reader close thread Exception.......");
                }
            }
        }
        return res;
    }

    /**
     * TODO(閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂竴閿熸垝璇濋敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
     * 
     * @param tagFilePath
     * @param tagList
     * @return
     */
    private static boolean getTagListFromFile(String tagFilePath,
            List<String> tagList)
    {
        boolean res = false;
        tagList.clear();
        try
        {
            FileReader in = new FileReader(tagFilePath);
            BufferedReader bufferedReader = new BufferedReader(in);
            String line;
            while (bufferedReader.ready())
            {
                line = bufferedReader.readLine();
                if (line.length() > 0)
                {
                    tagList.add(line);
                }
            }
            in.close();
            if (tagList.size() > 0)
            {
                res = true;
            }
        }
        catch (IOException e)
        {
            Log.d(TAG, e.toString());
        }

        return res;
    }

    /**
     * 閿熸枻鎷烽敓鏂ゆ嫹蹇楅敓鏂ゆ嫹鎭敓鏂ゆ嫹閿熻姤鍒伴敓渚ョ》鎷烽敓鏂ゆ嫹閿熸枻鎷�
     * 
     * @param context 搴旈敓鐭鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熶茎璁规嫹閿熸枻鎷�
     * @param packageName 搴旈敓鐭鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓锟�
     */
    public static void captureLogToFile(Context context, String packageName)
    {
        String appName = packageName
                .substring(packageName.lastIndexOf(".") + 1);

        Log.e(TAG, "appName== " + appName);
        File tagFile = new File(LOGCONFIGPATH + "/" + appName);
        if (!tagFile.exists())
        {
            Log.w(TAG, "no tag config file found !");
            return;
        }
        List<String> tagList = new ArrayList<String>();
        if (!getTagListFromFile(LOGCONFIGPATH + "/" + appName, tagList))
        {
            Log.w(TAG, "get tag config file failed !");
            return;
        }
        String[] LOGCAT_PREFIX = new String[] { "logcat", "-v", "time", "-s" };
        String[] cmdArray = new String[LOGCAT_PREFIX.length + tagList.size()];
        for (int i = 0; i < LOGCAT_PREFIX.length; i++)
        {
            cmdArray[i] = LOGCAT_PREFIX[i];
        }
        for (int j = 0; j < tagList.size(); j++)
        {
            cmdArray[LOGCAT_PREFIX.length + j] = tagList.get(j);
        }
        String logCmd = "";
        for (int k = 0; k < cmdArray.length; k++)
        {
            logCmd = logCmd + cmdArray[k] + " ";
        }
        // create log file
        Calendar c1 = Calendar.getInstance();
        String suffix = getCurTimeToString(c1, 1, 0);
        suffix = suffix.replace(" ", "_").replace(":", ".");
        String logFileName = appName + "_" + suffix + ".log";
        File flog = new File(LOGPATH + "/" + logFileName);
        // start write log file
        String param = logCmd + " -p > " + flog.toString();
        String[] comdline = { "/system/bin/sh", "-c", param };
        String cmd = "pkill logcat";
        Log.w(TAG, "log cmd : " + param);
        try
        {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            os.write(cmd.getBytes());
            os.flush();
            os.close();
            // clear the logcat first
            Runtime.getRuntime().exec("logcat -c");
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
            Log.e(TAG, "log comdline : " + Arrays.toString(comdline) + "");
            Runtime.getRuntime().exec(comdline);
            Log.e(TAG, "log param : " + param);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓绲婣G閿熸枻鎷稬OG閿熸枻鎷峰嵃閿熸枻鎷烽敓渚ョ》鎷烽敓鍙綇鎷烽敓鑺ュ偍浣嶉敓鏂ゆ嫹/mnt/sdcard/
     * mydebug閿熸枻鎷烽敓渚ョ》鎷烽敓鏂ゆ嫹閿熸枻鎷穕og+閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹.log閿熸枻鎷烽敓鏂ゆ嫹
     * 
     * @param context 搴旈敓鐭鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�閿熸枻鎷锋椂鏈娇閿熸枻鎷�
     * @param packageName
     *            搴旈敓鐭鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓绲婣G閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷mnt/sdcard/
     *            mydebug閿熶茎纭锋嫹閿熸枻鎷烽敓鍙綇鎷烽敓鐨嗗府鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
     * @param fAppendLog 閿熻鍑ゆ嫹閿熸枻鎷疯杩介敓鎺ヨ揪鎷峰嵃
     * @return LOG閿熻鍚︽崟浼欐嫹鏅掗敓鏂ゆ嫹璋嬮敓缁烇拷
     */
    public static boolean CaptureLogByTag(Context context, String packageName,
            boolean fAppendLog)
    {
        Process logCatProc = null;
        BufferedReader reader = null;
        boolean res = false;
        String appName = packageName
                .substring(packageName.lastIndexOf(".") + 1);
        // 閿熷彨璁规嫹TAG閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋嫹閿熻鍑ゆ嫹閿熸枻鎷烽敓锟�
        File tagFile = new File(LOGCONFIGPATH + "/" + appName);
        if (!tagFile.exists())
        {
            return false;
        }
        List<String> tagList = new ArrayList<String>();
        if (!getTagListFromFile(LOGCONFIGPATH + "/" + appName, tagList))
        {
            return false;
        }
        try
        {
            String[] cmdArray = new String[LOGCATCMD_PREFIX.length
                    + tagList.size()];
            for (int i = 0; i < LOGCATCMD_PREFIX.length; i++)
            {
                cmdArray[i] = LOGCATCMD_PREFIX[i];
            }
            for (int j = 0; j < tagList.size(); j++)
            {
                cmdArray[LOGCATCMD_PREFIX.length + j] = tagList.get(j);
            }
            String logCmd = "";
            for (int k = 0; k < cmdArray.length; k++)
            {
                logCmd = logCmd + cmdArray[k] + " ";
            }
            logCatProc = Runtime.getRuntime().exec(cmdArray);

            logCatProc.waitFor();

            // 閿熸枻鎷烽敓鏂ゆ嫹LogCat閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷峰啓閿熸枻鎷峰悓涓�敓鏂ゆ嫹閿熸枻鎷锋伅閿熸枻鎷烽敓鍙揪鎷烽敓鏂ゆ嫹閿熻鎾呮嫹鍕熼敓锟�
            Log.d(TAG, "CaptureLogByTag: exec logcat -c");
            Runtime.getRuntime().exec("logcat -c");
            Log.d(TAG, "CaptureLogByTag: finsh!");

            // 閿熸枻鎷峰彇閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷穕og鍐欓敓鏂ゆ嫹閿熶茎纭锋嫹
            Log.d(TAG, "CaptureLogByTag:read input stream to log");
            reader = new BufferedReader(new InputStreamReader(
                    logCatProc.getInputStream()));
            String line;
            int lineCount = 0;
            final StringBuilder log = new StringBuilder();

            // 閿熸枻鎷峰彇榛橀敓杈冪殑浼欐嫹閿熷彨鍑ゆ嫹 "line.separator" 閿熸枻鎷烽敓鑺傗槄鎷�n閿熸枻鎷�
            String separator = System.getProperty("line.separator");
            // 閿熸枻鎷峰彇閿熸枻鎷烽敓绲僌G鏃堕敓鏂ゆ嫹鍘婚敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熷彨锝忔嫹閿熸枻鎷烽敓鏂ゆ嫹姣忛敓杞胯揪鎷峰嵃閿熸埅闈╂嫹閿熸枻鎷烽敓鏂ゆ嫹鎭敓鏂ゆ嫹
            // --------- beginning of /dev/log/system
            // --------- beginning of /dev/log/main
            while ((line = reader.readLine()) != null)
            {
                if (line.contains("/dev/log/system")
                        || line.contains("/dev/log/main"))
                {
                    continue;
                }
                log.append(line);
                log.append(separator);
                lineCount++;
            }
            Log.d(TAG, "CaptureLogByTag:read input stream, line=" + lineCount);
            if (lineCount > 0)
            {
                Calendar c1 = Calendar.getInstance();
                String suffix = getCurTimeToString(c1, 1, 0);
                suffix = suffix.replace(" ", "_").replace(":", ".");
                String logFileName = "";
                if (fAppendLog)
                {
                    if (mSingleFileName.length() == 0)
                    {
                        mSingleFileName = appName + "_" + suffix + ".log";
                    }
                    logFileName = mSingleFileName;
                }
                else
                {
                    logFileName = appName + "_" + suffix + ".log";
                }
                Log.d(TAG, "CaptureLogByTag: WriteLogToFile=" + logFileName);

                // 閿熸枻鎷稬OG閿熸枻鎷锋伅鍐欓敓鏂ゆ嫹閿熶茎纭锋嫹
                if (WriteLogToFile(log.toString(), LOGPATH, logFileName))
                {
                    res = true;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i("MyLogcatCapture",
                    "CaptureLogByTag Exception=" + e.toString());
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.i(TAG, "reader close thread Exception.......");
                }
            }
            tagList.clear();
        }
        return res;
    }

    /**
     * 閿熸枻鎷峰寰敓鏂ゆ嫹閿熸枻鎷峰嵃LOG閿熸枻鎷烽敓鎹风鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓渚ョ》鎷烽敓鏂ゆ嫹
     * 
     * @param context 搴旈敓鐭鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�閿熸枻鎷锋椂鏈娇閿熸枻鎷�
     * @param packageName
     *            搴旈敓鐭鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓绲婣G閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋嫹閿熸枻鎷烽敓鏂ゆ嫹閿燂拷mnt/sdcard/
     *            mydebug閿熶茎纭锋嫹閿熸枻鎷烽敓鍙綇鎷烽敓鐨嗗府鎷烽敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
     * @param time 姣忛敓杞胯揪鎷峰嵃閿熶茎纭锋嫹閿熺粸鎲嬫嫹閿燂拷
     */
    public static void startLoopCapture(final Context context,
            final String packageName, final int time)
    {
        String appName = packageName
                .substring(packageName.lastIndexOf(".") + 1);
        // 閿熷彨璁规嫹TAG閿熸枻鎷烽敓鏂ゆ嫹閿熶茎纭锋嫹閿熻鍑ゆ嫹閿熸枻鎷烽敓锟�
        File tagFile = new File(LOGCONFIGPATH + "/" + appName);
        if (!tagFile.exists())
        {
            return;
        }
        mLoopCaptureEnd = false;
        // 閿熸枻鎷烽敓鏂ゆ嫹閿熺祪OG buffer,閿熸枻鎷烽敓瑙ｄ繚閿熸枻鎷烽敓鏂ゆ嫹涓�敓杞跨鎷稬OG閿熸枻鎷锋伅
        try
        {
            Runtime.getRuntime().exec("logcat -c");
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
        }
        new Thread() {
            @Override
            public void run()
            {
                super.run();
                while (mLoopCaptureEnd == false)
                {
                    CaptureLogByTag(context, packageName, true);
                    try
                    {
                        sleep(time);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 閿熸枻鎷稢alendar閿熸枻鎷烽敓鏂ゆ嫹閿熺粸鏂ゆ嫹閿熺粸鎲嬫嫹閿熸枻鎷烽敓鏂ゆ嫹鍐㈤敓鏂ゆ嫹閿熸枻鎷峰睉铔归敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹?
     * 閿熸枻鎷烽敓鏂ゆ嫹閿熼摪纰夋嫹鏃堕敓鏂ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹閿熻鍑ゆ嫹
     * 
     * @param c 
     *            杞敓鏂ゆ嫹涓洪敓绉哥▼璁规嫹閿熸枻鎷锋椂閿熸枻鎷峰�(閫氶敓鏂ゆ嫹Calendar.setTimeInMillis閿熸枻鎷烽敓鏂ゆ嫹杞
     *            敓鏂ゆ嫹)
     * @param i i=0閿熸枻鎷风ず閿熸枻鎷烽敓鏂ゆ嫹鍘婚敓鏂ゆ嫹-
     *            閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹閿熻鍑ゆ嫹閿熸枻鎷峰紡閿熸枻鎷�i=1閿熸枻鎷风ず閿熸枻鎷烽敓鎴揪鎷烽敓鏂ゆ嫹-
     *            閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹閿熻鍑ゆ嫹閿熸枻鎷峰紡
     * @param n 
     *            閿熸枻鎷烽敓鏂ゆ嫹閿熺粸鎾呮嫹閿熻绗嗘唻鎷烽敓渚ョ尨鎷穘閿熷眾锛岄敓鏂ゆ嫹閿熸枻鎷烽敓缁炴拝鎷烽敓瑙掔瑔鎲嬫嫹閿熸枻鎷峰墠n閿熸枻鎷
     *            �
     * @return 杞敓鏂ゆ嫹閿熸枻鎷烽敓缁炴唻鎷烽敓鏂ゆ嫹鍧�敓锟�
     */
    public static String getCurTimeToString(Calendar c, int i, int n)
    {
        // 閿熸枻鎷峰�閿熷彨璁规嫹
        if (c == null)
        {
            return null;
        }
        // i=1閿熸枻鎷风ず閿熸枻鎷烽敓鏂ゆ嫹- 閿熸枻鎷烽敓鏂ゆ嫹鏃堕敓鏂ゆ嫹閿熻鍑ゆ嫹閿熸枻鎷峰紡
        // n=7閿熸枻鎷风ず閿熸枻鎷峰墠鏃堕敓鏂ゆ嫹鏆敓鏂ゆ嫹閿熸枻鎷烽敓锟�
        String time;
        String s1 = "-", s2 = ":", s3 = " ";
        c.add(Calendar.DATE, n);// 閿熸枻鎷烽敓鏂ゆ嫹n閿熸枻鎷�
        int mYear = c.get(Calendar.YEAR); // 閿熸枻鎷峰彇閿熸枻鎷峰墠閿熸枻鎷烽敓锟�
        int mMonth = c.get(Calendar.MONTH);// 閿熸枻鎷峰彇閿熸枻鎷峰墠閿熼摪鍑ゆ嫹
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 閿熸枻鎷峰彇閿熸枻鎷峰墠閿熼摪浠界鎷烽敓鏂ゆ嫹閿熻妭鐚存嫹閿熸枻鎷�
        int mHour = c.get(Calendar.HOUR_OF_DAY);// 閿熸枻鎷峰彇閿熸枻鎷峰墠閿熸枻鎷峰皬鏃堕敓鏂ゆ嫹
        int mMinute = c.get(Calendar.MINUTE);// 閿熸枻鎷峰彇閿熸枻鎷峰墠閿熶茎鍑ゆ嫹閿熸枻鎷烽敓鏂ゆ嫹
        int mSecond = c.get(Calendar.SECOND);// 閿熸枻鎷峰彇閿熸枻鎷峰墠閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷�

        // 閿熸枻鎷烽敓锟�
        time = "" + mYear;
        // "-"
        if (1 == i)
        {
            time += s1;
        }
        // 閿熼摪鍑ゆ嫹
        int mon = mMonth + 1;
        if (mon < 10)
        {
            time = time + 0 + mon;
        }
        else
        {
            time += mon;
        }
        // "-"
        if (1 == i)
        {
            time += s1;
        }
        // 閿熸枻鎷烽敓鏂ゆ嫹
        if (mDay < 10)
        {
            time = time + 0 + mDay;
        }
        else
        {
            time += mDay;
        }
        // " "
        if (1 == i)
        {
            time += s3;
        }
        // 灏忔椂
        if (mHour < 10)
        {
            time = time + 0 + mHour;
        }
        else
        {
            time += mHour;
        }
        // ":"
        if (1 == i)
        {
            time += s2;
        }
        // 閿熸枻鎷烽敓鏂ゆ嫹
        if (mMinute < 10)
        {
            time = time + 0 + mMinute;
        }
        else
        {
            time += mMinute;
        }
        // ":"
        if (1 == i)
        {
            time += s2;
        }
        // 閿熸枻鎷烽敓鏂ゆ嫹
        if (mSecond < 10)
        {
            time = time + 0 + mSecond;
        }
        else
        {
            time += mSecond;
        }
        return time;
    }

    /**
     * 鍋滄寰敓鏂ゆ嫹閿熸枻鎷峰嵃
     * 
     */
    public static void endLoopCapture()
    {
        mLoopCaptureEnd = true;
    }
}
