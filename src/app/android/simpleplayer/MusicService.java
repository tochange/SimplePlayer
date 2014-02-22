package app.android.simpleplayer;

import java.io.File;
import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import app.android.simpleplayer.db.MyContentProvider;

public class MusicService extends Service

{

    private String TAG = "MusicService";

    public static MediaPlayer mPlayer;

    public boolean isLoop = false;

    private String mNextPath;

    public boolean isCompleted = false;

    private IServicePlayer.Stub stub = new IServicePlayer.Stub() {

        @Override
        public void play(final int progress, final String s)
                throws RemoteException
        {
            if (mPlayer != null)
                mPlayer.release();

            mPlayer = MediaPlayer.create(getApplicationContext(),
                    getFileinSD(s));

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    Log.e(TAG, "MusicService onCompletion()");
                    isCompleted = true;
                    // if (isLoop)
                    // {
                    try
                    {// ******************************************************dead
                     // code must change someday............................
                     // stub.play(0, mNextPath);
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    // }

                }

            });

            mPlayer.seekTo(progress);
            mPlayer.start();
            isCompleted = false;
        }

        @Override
        public void pause() throws RemoteException
        {
            Log.e(TAG, "MusicService pause()");
            if (mPlayer != null)
                mPlayer.pause();
            isCompleted = false;
        }

        @Override
        public void stop() throws RemoteException
        {
            if (mPlayer != null)
                mPlayer.stop();
        }

        @Override
        public int getDuration() throws RemoteException
        {
            // Log.e(tag, "MusicService getDuration()");
            return mPlayer.getDuration();
        }

        @Override
        public int getCurrentPosition(boolean playing) throws RemoteException
        {
            if (playing)
                return mPlayer.getCurrentPosition();
            return 0;
        }

        @Override
        public void seekTo(int current) throws RemoteException
        {
            Log.e(TAG, "MusicService seekTo()" + current);
            mPlayer.seekTo(current);
            isCompleted = false;
        }

        @Override
        public boolean setLoop(boolean loop, String name)
                throws RemoteException
        {
            // if (mPlayer != null)
            // mPlayer.release();
            // mPlayer = MediaPlayer.create(getApplicationContext(),
            // getFileinSD(name));
            mPlayer.setLooping(loop);
            // isLoop = loop;
            return false;
        }

        @Override
        public boolean isPlaying() throws RemoteException
        {
            return mPlayer.isPlaying();
        }

        @Override
        public boolean isCompleted() throws RemoteException
        {
            return isCompleted;
        }

        @Override
        public void release() throws RemoteException
        {
            if (mPlayer != null)
                mPlayer.release();
        }

        @Override
        public void reset() throws RemoteException
        {
            if (mPlayer != null)
                mPlayer.reset();

        }

        @Override
        public void changeTo(String name) throws RemoteException
        {
            mNextPath = name;
        }

    };

    private Uri getFileinSD(String filename)
    {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {

            Log.e(TAG, "filename ==> " + filename);
            File file = new File(filename);
            Uri uri = Uri.parse("file://" + file.getAbsolutePath());
            return uri;

        }
        else
        {
            return null;
        }

    }

    @Override
    public void onCreate()
    {
        Log.e(TAG, "MusicService onCreate()");

        // get first mp3 in db,set for default mPlayer.
        Cursor c = getApplicationContext().getContentResolver().query(
                MyContentProvider.CONTENT_URI, null, "_id=1", null, null);

        int count = c.getCount();

        if (c != null && count != 0 && c.moveToFirst())
        {
            String ss = c.getString(c.getColumnIndex("name"));
            mPlayer = MediaPlayer.create(getApplicationContext(),
                    getFileinSD(ss));
        }
        else
            Log.e(TAG, "without any data in db.");
        c.close();
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return stub;
    }

}
