package app.android.simpleplayer.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import app.android.simpleplayer.IServicePlayer;
import app.android.simpleplayer.MusicItem;
import app.android.simpleplayer.MusicService;
import app.android.simpleplayer.PlayerActivity;
import app.android.simpleplayer.PlayerActivity.CallBack;
import app.android.simpleplayer.R;
import app.android.simpleplayer.db.AfterWriteDbCallBack;
import app.android.simpleplayer.db.InsertData;
import app.android.simpleplayer.db.MyContentProvider;
import app.android.simpleplayer.log.MyLog;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;

class RankComparator implements Comparator<MusicItem>
{

    public int compare(MusicItem o1, MusicItem o2)
    {
        int i = (o2.getRank() - o1.getRank());
        if (i == 0)
        {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
        return i;
    }
}

class ViewHolder
{
    LinearLayout layout;

    TextView name;

    TextView artist;

    TextView time;

    SeekBar seekbar;

    TextView favorite;

    ImageButton pauseOrplay;

    ImageButton repreat;
}

public class MusicListAdapter extends BaseAdapter
{

    private Context mContext;

    private IServicePlayer iPlayer;

    private volatile int mTempId;

    private String TAG = "MusicListAdapter";

    boolean mUpdatThreadIsRunning = false;

    boolean mAlreayDelete = false;

    boolean mTimeToGetNextMusic = false;

    boolean mIsCompleted = false;

    private final int mSleepTime = 1200;// 800

    private CallBack mCallBack;

    private Set<Integer> mDeletedIdSet = new HashSet<Integer>();

    private ArrayList<MusicItem> mMusicList = new ArrayList<MusicItem>();

    private ArrayList<MusicItem> mTmpMusicList = new ArrayList<MusicItem>();

    private int mDeleteCount = 0;

    // public void preLoadDataFromSdCardBeforActivity(Context c)
    // {
    // Set<String> path = c.getSharedPreferences("songdata_path",
    // Context.MODE_PRIVATE).getStringSet("lasttime_songdata_path",
    // null);
    // if (path == null)
    // {
    // path = new HashSet<String>();
    // path.add(Constants.DEFAULT_MUSIC_PATH);
    // }
    // loadData(path, null);
    // }
    public MusicListAdapter(Context c, final Set<String> path, CallBack callback)
    {
        mCallBack = callback;
        mContext = c;
        Log.e(TAG, "oncreate..");
        List<String> list = loadData(path, null);
        if (!list.isEmpty())
        {
            new InsertData(mContext, list, new AfterWriteDbCallBack() {

                @Override
                public void refresh(List<String> withAnyMp3List)
                {
                    if (loadData(path, withAnyMp3List).isEmpty())
                        notifyDataSetChanged();
                    else
                        Log.e(TAG,
                                "db still haven'nt all data that your choosing path,maybe insert fail.");
                    // Toast.makeText(
                    // mContext,
                    // "can'nt find any file in your last choosing path.",
                    // Toast.LENGTH_LONG).show();
                }

            });

        }
        else
            Log.e(TAG, "good,all your choose paths were in db");

        mContext.bindService(new Intent(mContext, MusicService.class), conn,
                Context.BIND_AUTO_CREATE);
        mContext.startService(new Intent(mContext, MusicService.class));

    }

    public ServiceConnection getServiceConnection()
    {
        return conn;
    }

    public ArrayList<MusicItem> getMusicList()
    {
        return mMusicList;
    }

    public void releasePlayer()
    {
        try
        {
            if (iPlayer != null)
            {
                // iPlayer.stop();
                // iPlayer.reset();
                iPlayer.release();

            }
            // id = 0;
            if (!mMusicList.isEmpty())
            {
                mMusicList.get(mTempId).playing = false;
                mMusicList.get(mTempId).progress = 0;
            }
            mTempId = 0;
            mTmpMusicList.clear();
            mCallBack.updateNotification(PlayerActivity.CLEAR_NOTIFICATION, -1,
                    null);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }

    public List<String> reloadData(HashSet<String> path,
            List<String> withAnyMp3List)
    {
        releasePlayer();
        mMusicList.clear();
        List<String> noInDbId = loadData(path, withAnyMp3List);
        notifyDataSetChanged();
        return noInDbId;

        // mContext.bindService(new Intent(mContext, MusicService.class), conn,
        // Context.BIND_AUTO_CREATE);
        // mContext.startService(new Intent(mContext, MusicService.class));
    }

    public void remove(int position)
    {
        // for (int i = 0; i < mMusicList.size(); i++)
        // {
        // Log.e(TAG, "id=" + mMusicList.get(i).id
        // + mMusicList.get(position).visible
        // + mMusicList.get(i).delete);
        // // Log.e(TAG, "name=" + mMusicList.get(i).name);
        // }
        // mTempId = position;
        Log.e(TAG, "delete position=" + position);

        int id = mMusicList.get(position).id;
        if (id < mTempId){
            mDeleteCount++;
            mTempId--;
        }
        for (int i = 0; i < mTmpMusicList.size(); i++)
        {
            if (mTmpMusicList.get(i).id == id)
            {// if we load data to temper list more next to the end,we don not
             // need to delete in temp list.
                mTmpMusicList.remove(mTmpMusicList.get(i));
                break;
            }
        }

        mAlreayDelete = true;
        mDeletedIdSet.add(id);
        Log.e(TAG, "mDeletedIdSet=" + mDeletedIdSet.toString());

        mMusicList.remove(position);// position != id !!!

        // mMusicList.get(position).visible = false;
        // mMusicList.get(position).delete = true;

        notifyDataSetChanged();
    }

    // temporary donnt used
    // public int getNextItemViewHight(int position)
    // {
    // return mMusicList.get(position + 1).hight;
    //
    // }

    private List<String> loadData(Set<String> path, List<String> withAnyMp3List)
    {
        List<String> noInDb = new ArrayList<String>();
        if (path != null)
            Log.e(TAG, "loadData path=" + path.toString());
        if (withAnyMp3List != null && !withAnyMp3List.isEmpty())
        {
            for (int i = 0; i < withAnyMp3List.size(); i++)
                path.remove(withAnyMp3List.get(i));
        }
        Log.e(TAG, "after loadData path=" + path.toString());
        if (path.isEmpty())
        {
            Log.e(TAG, "path is null,no need to load");
            return noInDb;
        }

        Iterator it = path.iterator();
        String p;
        while (it.hasNext())
        {
            p = (String) it.next();
            String selection = "path" + "='" + p.replace("'", "''") + "'";
            Cursor c = mContext.getContentResolver().query(
                    MyContentProvider.CONTENT_URI,
                    new String[] { "_id", "path", "name" }, selection, null,
                    null);
            if (c.getCount() <= 0)
                noInDb.add(p);
        }
        Log.e(TAG, "noInDb=" + noInDb.toString());
        if (!noInDb.isEmpty())
            return noInDb;

        int k = 0;
        String[] args = new String[path.size()];
        Iterator itt = path.iterator();
        String s = "A";
        while (itt.hasNext())
        {
            p = (String) itt.next();
            args[k++] = p;
            s += " or path=?";

        }

        String selection = s.substring(5, s.length());
        Log.e(TAG, "selection=" + selection);
        Log.e(TAG, "args=" + Arrays.toString(args));

        Cursor c = mContext.getContentResolver().query(
                MyContentProvider.CONTENT_URI,
                new String[] { "_id", "path", "name", "rank" }, selection,
                args, null);
        Log.e(TAG, "loadData path=" + (c != null));
        Log.e(TAG, "loadData path=" + (c.getCount() != 0));

        int count = c.getCount();
        int id = 0;
        if (c != null && count != 0 && c.moveToFirst())
        {
            // int firstId = c.getInt(c.getColumnIndex("_id"));
            do
            {
                // int id = c.getInt(c.getColumnIndex("_id"));//change id
                // calculate method
                String ss = c.getString(c.getColumnIndex("name"));
                int rank = c.getInt(c.getColumnIndex("rank"));
                mMusicList.add(new MusicItem(id++, ss, 0, rank, false, false));
                // Log.e(TAG, "loadData ss=" + ss);
            }
            while (c.moveToNext());
            // mTmpMusicList.addAll(mMusicList);
        }
        c.close();
        return noInDb;

    }

    private ServiceConnection conn = new ServiceConnection() {
        // asynchronous operation,so when be called is unpredict.

        public void onServiceConnected(ComponentName className, IBinder service)
        {
            iPlayer = IServicePlayer.Stub.asInterface(service);
        }

        public void onServiceDisconnected(ComponentName className)
        {
            try
            {
                iPlayer.stop();
                iPlayer.release();
                mMusicList.get(mTempId).playing = false;
            }
            catch (RemoteException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        };
    };

    @Override
    public int getCount()
    {
        // TODO Auto-generated method stub
        return mMusicList.size();
    }

    @Override
    public Object getItem(int position)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        // TODO Auto-generated method stub
        return position;
    }

    private void animateExpanding(View mContentParent)
    {
        mContentParent.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        mContentParent.measure(widthSpec, heightSpec);

        ValueAnimator animator = createHeightAnimator(0,
                mContentParent.getMeasuredHeight(), mContentParent);
        animator.start();
    }

    private void animateCollapsing(final View mContentParent)
    {
        int origHeight = mContentParent.getHeight();

        ValueAnimator animator = createHeightAnimator(origHeight, 0,
                mContentParent);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator)
            {
            }

            @Override
            public void onAnimationEnd(Animator animator)
            {
                mContentParent.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator)
            {
            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {
            }
        });
        animator.start();
    }

    private ValueAnimator createHeightAnimator(int start, int end,
            final View mContentParent)
    {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator)
            {
                int value = (Integer) valueAnimator.getAnimatedValue();

                ViewGroup.LayoutParams layoutParams = mContentParent
                        .getLayoutParams();
                layoutParams.height = value;
                mContentParent.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    int getIdWithName(String name)
    {
        String selection = "name" + "='" + name.replace("'", "''") + "'";
        Cursor c = mContext.getContentResolver().query(
                MyContentProvider.CONTENT_URI, new String[] { "_id", "name" },
                selection, null, null);

        int count = c.getCount();

        if (c != null && count != 0 && c.moveToFirst())
        {
            return c.getInt(c.getColumnIndex("_id"));
        }
        c.close();
        return -1;

    }

    int clickPosition;

    @Override
    public View getView(final int position, View view, ViewGroup parent)
    {
        ViewHolder viewHolder = null;

        if (view == null)
        {
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.musiclistitem, null);
            viewHolder.layout = (LinearLayout) view.findViewById(R.id.all);
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.artist = (TextView) view.findViewById(R.id.artist);
            viewHolder.time = (TextView) view.findViewById(R.id.time);
            viewHolder.seekbar = (SeekBar) view.findViewById(R.id.musicSeekBar);

            viewHolder.favorite = (TextView) view
                    .findViewById(R.id.imageButtonFavorite);
            viewHolder.pauseOrplay = (ImageButton) view
                    .findViewById(R.id.imageButtonPause);
            viewHolder.repreat = (ImageButton) view
                    .findViewById(R.id.imageButtonRepeat);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }

        final MusicItem item = mMusicList.get(position);

        String s = item.name;
        if (!item.delete)
            viewHolder.name.setText(item.id + ". "
                    + s.substring(s.lastIndexOf('/') + 1, s.lastIndexOf('.')));
        else
            viewHolder.name.setVisibility(View.GONE);

        final LinearLayout lo = viewHolder.layout;
        final TextView tv = viewHolder.favorite;

        if (item.rank != 0)
            tv.setText(String.valueOf(item.rank));

        viewHolder.favorite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                item.rank++;
                tv.setText(String.valueOf(item.rank));
                new UpdateDbFavorateRankTask().execute(item.name);
            }
        });
        // final
        viewHolder.name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                Log.e(TAG, "name click=" + item.visible + position);
                clickPosition = position;
                item.visible = !item.visible;
                // if (item.visible)
                // {
                // animateExpanding(lo);
                // // lo.setVisibility(View.VISIBLE);
                // }
                // else
                // {
                // animateCollapsing(lo);
                // // lo.setVisibility(View.GONE);
                // }

                notifyDataSetChanged();
            }
        });

        if (item.visible)
        {
            // if (!mMusicList.get(mTempId).playing && clickPosition ==
            // position)
            // animateExpanding(lo);
            lo.setVisibility(View.VISIBLE);
        }
        else
        {
            // if (!mMusicList.get(mTempId).playing && clickPosition ==
            // position)
            // animateCollapsing(lo);
            lo.setVisibility(View.GONE);
        }
        if (item.playing)
            viewHolder.pauseOrplay.setBackgroundResource(R.drawable.pause);
        else
            viewHolder.pauseOrplay.setBackgroundResource(R.drawable.play);

        if (item.repeated)
            viewHolder.repreat.setBackgroundResource(R.drawable.repeat_green);
        else
            viewHolder.repreat.setBackgroundResource(R.drawable.repeat_grey);

        viewHolder.pauseOrplay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                if (!item.playing)
                {
                    try
                    {
                        Log.e(TAG, "****item.progress=" + item.progress);

                        mMusicList.get(mTempId).playing = false;// last time
                                                                // playing item

                        Iterator it = mDeletedIdSet.iterator();
                        int j, k = 0;
                        while (it.hasNext())
                        {
                            j = (Integer) it.next();
                            if (j < item.id)
                                k++;
                        }
                        if (mAlreayDelete)
                        {
                            // if (!mHasUsedDeletedIdList.contains(item.id))
                            // {
                            // mHasUsedDeletedIdList.add(item.id);
                            mTempId = item.id - k;
                            // }
                            // else
                            // mTempId = item.id;
                        }
                        else
                            mTempId = item.id;

                        // Log.e(TAG, "item.id=" + item.id);
                        // Log.e(TAG, "mTempId=" + mTempId);
                        // Log.e(TAG, "item.name=" + item.name);
                        // Log.e(TAG, "item.name=" + (item.name == null));
                        // Log.e(TAG, "item.name=" + item.name.equals(""));
                        File f = new File(item.name);
                        if (!f.exists())
                        {
                            Toast.makeText(
                                    mContext,
                                    "no such file in your choosed path,please choose another path.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        iPlayer.play(item.progress, item.name);
                        if (!mUpdatThreadIsRunning)
                        {
                            mUpdateProgressThread.start();
                            mGetNameNextToPlayFromRankTheand.start();
                            mUpdatThreadIsRunning = true;
                        }

                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                    mCallBack.updateNotification(
                            PlayerActivity.SEND_NOTIFICATION, item.id,
                            item.name);
                }
                else
                {
                    try
                    {
                        iPlayer.pause();
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                }

                Log.e(TAG, "in adapter mTempId=" + mTempId + "; item.playing="
                        + item.playing);
                item.playing = !item.playing;
                notifyDataSetChanged();
            }
        });

        if (item.duration != 0)
        {
            // Log.e(TAG, mMusicList.indexOf(item) + " progress=" +
            // item.progress);
            viewHolder.seekbar.setProgress(Math
                    .round(100 * ((float) item.progress / item.duration)));
        }
        else
            viewHolder.seekbar.setProgress(0);

        viewHolder.seekbar
                .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar,
                            int progress, boolean fromUser)
                    {
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar)
                    {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar)
                    {
                        File f = new File(item.name);
                        if (!f.exists())
                        {
                            Toast.makeText(
                                    mContext,
                                    "no such file in your choosed path,please choose another path.",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        try
                        {// +0.5 to reduce definition cause by changing 100
                         // percents to milliseconds

                            item.progress = Math.round((float) (seekBar
                                    .getProgress() + 0.5)
                                    / 100
                                    * iPlayer.getDuration());
                            item.duration = iPlayer.getDuration();

                            Log.e(TAG, "seek to =" + item.progress);
                            notifyDataSetChanged();

                            if (item.playing)
                                iPlayer.play(item.progress, item.name);
                            // iPlayer.seekTo(item.progress);
                        }
                        catch (RemoteException e1)
                        {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }

                    }
                });

        viewHolder.repreat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)
            {// if not playing,can'nt set this iPlayer.
             // if (item.playing)
             // {
             // try
             // {
             // iPlayer.setLoop(!item.repeated, item.name);
             // }
             // catch (RemoteException e)
             // {
             // e.printStackTrace();
             // }
             // Log.e(TAG, "**item.repeated=" + item.repeated);
             // item.repeated = !item.repeated;
             // notifyDataSetChanged();
             // }
             // mIsSingleMusicLoop = true;

                Log.e(TAG, " item.repeated=" + item.repeated);
                item.repeated = !item.repeated;
                notifyDataSetChanged();
            }
        });
        item.hight = (short) view.getHeight();
        return view;
    }

    class UpdateDbFavorateRankTask extends AsyncTask<String, Integer, String>
    {

        int mRank;

        String mName;

        @Override
        protected String doInBackground(String... params)
        {
            alreadyDealGetNameNextToPlay = false;
            String selection = "name='" + params[0].replace("'", "''") + "'";

            Cursor c = mContext.getContentResolver().query(
                    MyContentProvider.CONTENT_URI,
                    new String[] { "name", "rank" }, selection, null, null);
            Log.e(TAG, "loadData path=" + (c.getCount() != 0));

            int count = c.getCount();

            if (c != null && count != 0 && c.moveToFirst())
            {

                do
                {
                    mRank = c.getInt(c.getColumnIndex("rank"));
                    mRank++;
                    Log.e(TAG, "doInBackground mRank=" + mRank);
                    ContentValues values = new ContentValues();
                    values.put("rank", mRank);

                    mContext.getContentResolver().update(
                            MyContentProvider.CONTENT_URI, values, selection,
                            null);
                }
                while (c.moveToNext());
            }
            c.close();
            Log.e(TAG, "onPostExecute" + mRank + mName);

            return null;
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            notifyDataSetChanged();

            int p, d;
            p = msg.arg1;
            d = msg.arg2;

             Log.e(TAG, p + "=p,mTempId=" + mTempId);
             Log.e(TAG, d + "=d");

            if (mMusicList.get(mTempId).playing)
            {
                mMusicList.get(mTempId).progress = p;
                mMusicList.get(mTempId).duration = d;
            }
            if (msg.what == -1)
            {
                Log.e(TAG, "finish id=" + mTempId);
                mMusicList.get(mTempId).playing = false;

            }

        };
    };

    Thread mUpdateProgressThread = new Thread(new Runnable() {
        boolean alreadyDeal = false;

        boolean alreadyDeal2 = false;

        @Override
        public void run()
        {
            while (iPlayer != null)
            {
                MyLog.e(mTmpMusicList.size() + " " + mTempId + mIsCompleted
                        + mMusicList.get(mTempId).repeated + alreadyDeal
                        + alreadyDeal2);

                //
                // Log.e(TAG, " in thread playing="
                // + mMusicList.get(mTempId).playing);

                if (mIsCompleted)
                {
                    mCallBack.updateNotification(
                            PlayerActivity.CLEAR_NOTIFICATION, -1, null);
                    if (!mMusicList.get(mTempId).repeated)
                    {

                        if (!alreadyDeal)
                        {
                            alreadyDeal = true;
                            if (mTmpMusicList.isEmpty())
                                return;
                            mTempId = mTmpMusicList.get(0).id;
                            Log.e(TAG, "new music id=" + mTempId);

                            // mTmpMusicList.get(0).playing = !mTmpMusicList
                            // .get(0).playing;
                            
                            
                            mMusicList.get(mTempId).playing = !mMusicList
                                    .get(mTempId).playing;
//                            mDeleteCount = 0;
                            try
                            {
                                iPlayer.play(mTmpMusicList.get(0).progress,
                                        mTmpMusicList.get(0).name);
                            }
                            catch (RemoteException e)
                            {
                                e.printStackTrace();
                            }
                            mCallBack.updateNotification(
                                    PlayerActivity.SEND_NOTIFICATION, mTempId,
                                    mTmpMusicList.get(0).name);

                            if (mTmpMusicList.size() > 0)
                                mTmpMusicList.remove(0);
                            else
                                Log.e(TAG, "music list is overing");

                        }

                    }
                    else
                    {
                        if (!alreadyDeal2)
                        {
                            alreadyDeal2 = true;
                            mMusicList.get(mTempId).playing = true;
                            Log.e(TAG, "again music id=" + mTempId);
                            try
                            {
                                iPlayer.play(0, mMusicList.get(mTempId).name);
                            }
                            catch (RemoteException e)
                            {
                                e.printStackTrace();
                            }
                            mCallBack.updateNotification(
                                    PlayerActivity.SEND_NOTIFICATION, mTempId,
                                    mMusicList.get(mTempId).name);
                        }

                    }
                }
                MyLog.e(mMusicList.size() + " ....mTempId=" + mTempId
                        + "; mTempId.playing="
                        + mMusicList.get(mTempId).playing);
                // if (!mTmpMusicList.isEmpty())
                MyLog.e("playing= " + mMusicList.get(mTempId).playing);

                if (mMusicList.get(mTempId).playing)// ///////////////
                // if ((mTmpMusicList.isEmpty() &&
                // mMusicList.get(mTempId).playing)
                // || (!mTmpMusicList.isEmpty() && mTmpMusicList
                // .get(mTempId).playing))// ///////////////
                {
                    try
                    {

                        // Log.e(TAG, "mTempId=" + mTempId +
                        // "; mTempId.playing="
                        // + mMusicList.get(mTempId).playing);
                        mIsCompleted = iPlayer.isCompleted();
                        Message m = new Message();
                        if (!mIsCompleted)
                        {
                            alreadyDeal = false;

                            int progress = iPlayer
                                    .getCurrentPosition(mMusicList.get(mTempId).playing);
                            int duration = iPlayer.getDuration();
                            mTimeToGetNextMusic = (float) progress / duration > 0.95f;

                            // Log.e(TAG, "=isCompleted=" + isCompleted);

                            m.arg1 = progress;
                            m.arg2 = duration;

                        }
                        else
                            alreadyDeal2 = false;
                        m.what = (mIsCompleted) ? -1 : 0;
                        handler.sendMessage(m);
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }
                }

                try
                {
                    Thread.sleep(mSleepTime);
                }
                catch (InterruptedException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }
        }
    });

    boolean alreadyDealGetNameNextToPlay = false;

    Thread mGetNameNextToPlayFromRankTheand = new Thread(new Runnable() {

        // "select name.* from musicitem name where 1 > (select count(*) from musicitem where rank > name.rank)"
        @Override
        public void run()
        {
            while (true)
            {
                MyLog.e("id=" + mTempId + " " + mTimeToGetNextMusic + ","
                        + alreadyDealGetNameNextToPlay);

                if (mTimeToGetNextMusic && !alreadyDealGetNameNextToPlay)
                {
                    mTmpMusicList.clear();
                    mTmpMusicList.addAll(mMusicList);
                    String playingName = mMusicList.get(mTempId).name;
                    for (int i = 0; i < mTmpMusicList.size(); i++)
                    {
                        mTmpMusicList.get(i).playing = playingName
                                .equals(mTmpMusicList.get(i).name) ? true
                                : false;
                    }
                    Collections.sort(mTmpMusicList, new RankComparator());

                    for (MusicItem m : mTmpMusicList)
                        MyLog.e("rank=" + m.rank + " " + m.id + " " + m.name);
                    alreadyDealGetNameNextToPlay = true;

                    try
                    {
                        iPlayer.changeTo(mTmpMusicList.get(0).name);
                    }
                    catch (RemoteException e)
                    {
                        e.printStackTrace();
                    }

                }

                try
                {
                    Thread.sleep(mSleepTime);
                }
                catch (InterruptedException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

        }

    });

}