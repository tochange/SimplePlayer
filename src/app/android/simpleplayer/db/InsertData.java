package app.android.simpleplayer.db;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class InsertData
{
    String TAG = "InsertData";

    AfterWriteDbCallBack mRefreshCallBack;

    String mDdSelection[] = { "path", "name" };

    HashMap<String, ArrayList<String>> mDataMap = new HashMap<String, ArrayList<String>>();

    List<String> mPathList = new ArrayList<String>();

    List<String> mWithoutAnyMp3PathList = new ArrayList<String>();

    Context mContext;

    public InsertData(Context c, List<String> path,
            AfterWriteDbCallBack callback)
    {
        Log.e(TAG, "InsertData() called");
        mContext = c;
        mRefreshCallBack = callback;
        mPathList = path;
        loadData(path);
        new InsertTask().execute();

    }

    private void loadData(List<String> pathList)
    {
        // Log.e(TAG, "loadData() called pathList=" + pathList.size());
        Log.e(TAG, "loadData() called pathList=" + pathList.toString());
        for (int i = 0; i < pathList.size(); i++)
        {
            String path = pathList.get(i);
            ArrayList<String> thisPathDataList = new ArrayList<String>();
            mDataMap.put(path, loadDataImp(path, thisPathDataList));
        }

    }

    ArrayList<String> loadDataImp(String path,
            ArrayList<String> thisPathDataList)
    {
        Log.e(TAG, "loadDataImp path=" + path);
        File str[] = new File(path).listFiles();

        // a directory
        if (str != null && str.length > 0)
        {
            for (File f : str)
            {

                if (f.isDirectory())
                {
                    path = f.getAbsolutePath();
                    loadDataImp(path, thisPathDataList);
                    path = path.substring(0, path.lastIndexOf('/'));

                }
                else
                {
                    String s = f.toString();
                    if (s.endsWith(".mp3"))
                    {
                        thisPathDataList.add(s);
                    }
                }
            }
        }
        // a file
        else if (path.endsWith(".mp3"))
        {
            thisPathDataList.add(path);
        }
        else
        {

            Toast.makeText(
                    mContext,
                    "your path(last setting) is either a file or a directory,please selete another path.",
                    Toast.LENGTH_LONG).show();
        }
        int afterSize = thisPathDataList.size();
        // Log.e(TAG, "beforSize=" + beforSize);
        // Log.e(TAG, "afterSize=" + afterSize);

        if (afterSize == 0)
        {
            mWithoutAnyMp3PathList.add(path);
        }

        return thisPathDataList;

    }

    private class InsertTask extends AsyncTask<String, Integer, String>
    {
        @Override
        protected void onPreExecute()
        {
            Log.e(TAG, "onPreExecute() called");
            // Log.e(TAG, mDataMap.toString());
        }

        @Override
        protected String doInBackground(String... params)
        {
            Log.e(TAG, "doInBackground() called");

            Cursor cursor = mContext.getContentResolver().query(
                    MyContentProvider.CONTENT_URI, mDdSelection, null, null,
                    null);
            for (int j = 0; j < mPathList.size(); j++)
            {
                String tmpPath = mPathList.get(j);
                ArrayList<String> tmpDataList = mDataMap.get(tmpPath);
                File f = new File(tmpPath);
                String selection = "path" + "='" + tmpPath.replace("'", "''")
                        + "'";

                // String selection = "path" + "='" + mPathList.get(j)
                // + "' OR name='" + mPathList.get(j) + "'";

                Cursor c = mContext.getContentResolver().query(
                        MyContentProvider.CONTENT_URI, new String[] { "path" },
                        selection, null, null);

                Log.e(TAG, "insert count=" + c.getCount());

                if (c.getCount() == 0)
                {
                    if (f.isDirectory())
                    {
                        for (int i = 0; i < tmpDataList.size(); i++)
                        {
                            ContentValues values = new ContentValues();
                            values.put("path", tmpPath);
                            values.put("name", tmpDataList.get(i));
                            values.put("rank", 0);
                            // Log.e(TAG, "insert" + mPathList.get(j));
                            // Log.e(TAG, "insert" + mDataList.get(i));

                            cursor.moveToNext();

                            mContext.getContentResolver().insert(
                                    MyContentProvider.CONTENT_URI, values);
                        }
                    }
                    else if (f.isFile())
                    {
                        ContentValues values = new ContentValues();
                        values.put("path", tmpPath);
                        values.put("name", tmpPath);
                        values.put("rank", 0);
                        cursor.moveToNext();

                        mContext.getContentResolver().insert(
                                MyContentProvider.CONTENT_URI, values);
                    }
                    else
                        Log.e(TAG, "unkonw type");
                }
                if (c != null && !c.isClosed())
                {
                    c.close();
                }
            }
            if (cursor != null && !cursor.isClosed())
            {
                cursor.close();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress)
        {
        }

        @Override
        protected void onPostExecute(String result)
        {
            mRefreshCallBack.refresh(mWithoutAnyMp3PathList);
        }

        @Override
        protected void onCancelled()
        {
        }

    }

}