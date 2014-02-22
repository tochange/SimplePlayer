package app.android.simpleplayer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import app.android.simpleplayer.adapter.ContextualUndoAdapter;
import app.android.simpleplayer.adapter.ContextualUndoAdapter.DeleteItemCallback;
import app.android.simpleplayer.adapter.MusicListAdapter;
import app.android.simpleplayer.db.AfterWriteDbCallBack;
import app.android.simpleplayer.db.InsertData;
import app.android.simpleplayer.sourcepath.IReportExportListener;
import app.android.simpleplayer.sourcepath.ReportExportDialog;
import app.android.simpleplayer.utils.Constants;
import app.android.simpleplayer.utils.ExitAllActivityApplication;

@TargetApi(11)
public class PlayerActivity extends Activity implements IReportExportListener
{
    String TAG = "PlayerActivity";

    public static final int PLAY = 1;

    public static final int PAUSE = 2;

    private NotificationManager mNotificationManager;

    private PendingIntent mPendingIntent;

    public static final int SEND_NOTIFICATION = 47;

    public static final int CLEAR_NOTIFICATION = 48;

    ImageButton imageButtonFavorite;

    ImageButton imageButtonNext;

    ImageButton imageButtonPlay;

    ImageButton imageButtonPre;

    ImageButton imageButtonRepeat;

    SeekBar musicSeekBar;

    MusicListAdapter mMusiclistAdapter;

    ContextualUndoAdapter mContextualUndoAdapter;

    Set<String> mDataPath;

    SharedPreferences mSharedPreferences;

    public interface CallBack
    {
        void updateNotification(int type, int id, String message);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add("choose path");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        switch (item.getItemId())
        {
            case 0:
                // donn't remenber last time choosing path
                // mDataPath = mSharedPreferences.getStringSet(
                // "lasttime_songdata_path", null);
                //
                // if (mDataPath == null)
                // {
                // mDataPath = new HashSet<String>();
                // mDataPath.add("/mnt/sdcard/My Documents/music");
                // }

                ReportExportDialog r = new ReportExportDialog(this, null, this);
                // r.show();
        }

        return super.onOptionsItemSelected(item);

    }

    @TargetApi(11)
    @Override
    public void reportExportConfirmClick(final HashSet<String> path)
    {
        Log.e(TAG, "reportExportConfirmClick path=" + path);
        mSharedPreferences.edit().putStringSet("lasttime_songdata_path", path)
                .commit();
        reloadData(path);
    }

    private void reloadData(final HashSet<String> path)
    {
        if (path != null && !path.equals(""))
        {
            List<String> list = mMusiclistAdapter.reloadData(path, null);
            Log.e(TAG, "reloadData list path=" + list.toString());
            if (!list.isEmpty())
            {
                Log.e(TAG, " " + list.toString());
                new InsertData(this, list, new AfterWriteDbCallBack() {

                    @Override
                    public void refresh(List<String> withAnyMp3List)
                    {
                        if (mMusiclistAdapter.reloadData(path, withAnyMp3List)
                                .isEmpty())
                            mContextualUndoAdapter.notifyDataSetChanged();
                        else
                            Log.e(TAG,
                                    "db still haven'nt all data that your choosing path,maybe insert fail.");
                    }

                });
            }
            else
                Log.e(TAG, "good,all your choose paths were in db");

        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Log.e(TAG, "onDestory...");
        // unwork(cannt release resource),but have to call to unblind service
        this.unbindService(mMusiclistAdapter.getServiceConnection());

        this.stopService(getIntent());
        sendNotification(CLEAR_NOTIFICATION, -1, null);
        mMusiclistAdapter.releasePlayer();

        ExitAllActivityApplication.getInstance().exit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        // WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.player);

        ExitAllActivityApplication.getInstance().addActivity(this);
        imageButtonFavorite = (ImageButton) findViewById(R.id.imageButtonFavorite);
        imageButtonNext = (ImageButton) findViewById(R.id.imageButtonNext);
        imageButtonPlay = (ImageButton) findViewById(R.id.imageButtonPlay);
        imageButtonPre = (ImageButton) findViewById(R.id.imageButtonPre);
        imageButtonRepeat = (ImageButton) findViewById(R.id.imageButtonRepeat);
        musicSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);

        mSharedPreferences = getSharedPreferences("songdata_path",
                Context.MODE_PRIVATE);

        mDataPath = mSharedPreferences.getStringSet("lasttime_songdata_path",
                null);

        if (mDataPath == null)
        {
            mDataPath = new HashSet<String>();
            mDataPath.add(Constants.DEFAULT_MUSIC_PATH);
        }
        mMusiclistAdapter = new MusicListAdapter(this, mDataPath,
                new CallBack() {

                    @Override
                    public void updateNotification(int type, int id,
                            String message)
                    {
                        // TODO Auto-generated method stub
                        sendNotification(type, id, message);
                    }

                });

        ListView lv = (ListView) findViewById(R.id.listview);
        mContextualUndoAdapter = new ContextualUndoAdapter(mMusiclistAdapter,
                R.layout.undo_row, R.id.undo_row_undobutton);
        mContextualUndoAdapter.setAbsListView(lv);
        lv.setAdapter(mContextualUndoAdapter);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3)
            {
                // TODO Auto-generated method stub
                Log.e(TAG, "item click="
                        + mMusiclistAdapter.getMusicList().get(arg2).visible);
                // so easy missoperation between click and slide.
                if (mMusiclistAdapter.getMusicList().get(arg2).visible)
                {
                    mMusiclistAdapter.getMusicList().get(arg2).visible = !mMusiclistAdapter
                            .getMusicList().get(arg2).visible;
                    mMusiclistAdapter.notifyDataSetChanged();
                }

            }
        });
        mContextualUndoAdapter
                .setDeleteItemCallback(new MyDeleteItemCallback());

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null)
        {
            Log.e(TAG, " type:" + type);
            Uri uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            HashSet<String> urlSet = new HashSet<String>();
            urlSet.add(uri.getPath());
            Log.e(TAG, " uri path:" + uri.getPath());
            reloadData(urlSet);

            if ("image/".equals(type))
            {
                Uri imageUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null)
                {
                    Log.e(TAG, " imageUri:" + imageUri.getPath());
                }
            }
        }

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent2 = new Intent(this, PlayerActivity.class);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent2, 0);
        // click notification go to setting module
        // mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(
        // "android.settings.SETTINGS"), 0);
    }

    private void sendNotification(int type, int id, String s)
    {
        String message;
        if (s != null)
            message = id + ". "
                    + s.substring(s.lastIndexOf('/') + 1, s.lastIndexOf('.'));
        else
            message = "default message!";
        int Notification_ID_BASE = 110;

        int Notification_ID_MEDIA = 119;

        Notification baseNF = new Notification();

        Notification mediaNF;

        int i = 0;

        switch (type)
        {
            case 1:
                baseNF.icon = R.drawable.icon;
                // ֪ͨʱ��״̬����ʾ������
                baseNF.tickerText = "You clicked BaseNF!";
                baseNF.defaults |= Notification.DEFAULT_VIBRATE;
                // baseNF.defaults |= Notification.DEFAULT_LIGHTS;
                baseNF.defaults |= Notification.DEFAULT_SOUND;

                // baseNF.sound =
                // Uri.parse("file:///sdcard/notification/ringer.mp3");

                // ��������������ѭ����ֱ���û���Ӧ
                baseNF.flags |= Notification.FLAG_INSISTENT;

                // ֪ͨ��������Զ���ʧ
                baseNF.flags |= Notification.FLAG_AUTO_CANCEL;

                // ���'Clear'ʱ���������֪ͨ(QQ��֪ͨ�޷�����������õ����)
                baseNF.flags |= Notification.FLAG_NO_CLEAR;

                baseNF.setLatestEventInfo(PlayerActivity.this, "Title01",
                        "Content01", mPendingIntent);

                // ����״̬��֪ͨ
                // The first parameter is the unique ID for the Notification
                // and the second is the Notification object.
                mNotificationManager.notify(Notification_ID_BASE, baseNF);

                break;

            case 2:
                // ����֪ͨ
                // ����״̬����ʾ��һ���¶��ţ���û���ü��鿴������һ���¶��ŵ���ʾ��
                // ��ʱ���ø���ԭ��֪ͨ�ķ�ʽ�Ƚϡ�
                // (�����·�һ��֪ͨҲ���ԣ��������������֪ͨ�Ļ��ң�������ʾ���֪ͨ���û������û�Ҳ���Ѻ�)
                baseNF.tickerText = "You clicked undate BaseNF!";
                baseNF.setLatestEventInfo(PlayerActivity.this, "Title02",
                        "Content02", mPendingIntent);
                mNotificationManager.notify(Notification_ID_BASE, baseNF);
                break;

            case 3:
                mNotificationManager.cancel(Notification_ID_BASE);
                break;

            case 4:
                mediaNF = new Notification();
                mediaNF.icon = R.drawable.icon;
                mediaNF.tickerText = "You clicked MediaNF!";
                // �Զ�������
                mediaNF.sound = Uri.withAppendedPath(
                        Audio.Media.INTERNAL_CONTENT_URI, "6");

                // ֪ͨʱ��������
                // ��һ������: ��ǰ�ȴ���ʱ��
                // �ڶ��������� ��һ���񶯵�ʱ�����Դ�����
                long[] vir = { 0, 100, 200, 300 };
                mediaNF.vibrate = vir;
                mediaNF.setLatestEventInfo(PlayerActivity.this, "Title03",
                        "Content03", mPendingIntent);
                mNotificationManager.notify(Notification_ID_MEDIA, mediaNF);
                break;

            case 5:
                mNotificationManager.cancel(Notification_ID_MEDIA);
                break;

            case CLEAR_NOTIFICATION:
                mNotificationManager.cancelAll();
                break;

            case SEND_NOTIFICATION:
                Notification notification = new Notification();
                RemoteViews contentView = new RemoteViews(getPackageName(),
                        R.layout.custom_notification);

                // Builder builder = new
                // Notification.Builder(Lesson_10.this);
                // builder.setSmallIcon(R.drawable.icon).setTicker("update")
                // .setWhen(System.currentTimeMillis())
                // .setContentTitle("more progress").setOngoing(true)
                // .setOnlyAlertOnce(true).setContent(contentView);
                // notification = builder.getNotification();

                notification.icon = R.drawable.micon;
                notification.tickerText = message;
                contentView.setImageViewResource(R.id.image, R.drawable.icon);
                contentView.setTextViewText(R.id.text, message
                        + " is playing..");

                // notification.ledARGB = 0xff0000ff;
                // notification.ledOnMS = 1000;
                // notification.ledOffMS = 1000;
                // notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
                // notification.flags |= Notification.FLAG_SHOW_LIGHTS;

                notification.contentView = contentView;

                // ʹ���Զ���������ͼʱ������Ҫ�ٵ���setLatestEventInfo()����
                // ���Ǳ��붨�� contentIntent
                // notification.contentIntent = mPendingIntent;
                notification.contentIntent = null;

                mNotificationManager.notify(3, notification);
                break;
        }

    }

    // public void onPauseButtonClicked(View view)
    // {
    // Log.e(TAG, "................");
    // }

    private class MyDeleteItemCallback implements DeleteItemCallback
    {

        @Override
        public void deleteItem(int position)
        {
            mMusiclistAdapter.remove(position);
            mMusiclistAdapter.notifyDataSetChanged();
        }

        // @Override
        // public int nextItemViewHight(int position)
        // {
        // // TODO Auto-generated method stub
        // return mMusiclistAdapter.getNextItemViewHight(position);
        // }

    }

}