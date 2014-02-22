package app.android.simpleplayer.sourcepath;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import app.android.simpleplayer.R;

public class FilePathAdapter extends BaseAdapter
{
    private final String TAG = "FilePathAdapter";

    private List<String> m_Data;

    private HashSet<String> mReturnDataArrayList;

    private String m_Parent;

    private Context m_Context;

    private IFilePathChooseListener mListener;

    private String UPPER = "...";

    public static final String MNT = "/mnt";

    public static final String SDCARD = android.os.Environment
            .getExternalStorageDirectory().getAbsolutePath();

    public static final String EXTERNAL_SDCARD = SDCARD;// ImportdataConstant

    // .getExternalStoragesDirectory();

    public static final String USB_STORAGE = SDCARD;// ImportdataConstant

    // .getUsbStoragesDirectory();

    private Set<String> mPath;

    public HashSet<String> getDataList()
    {
        return mReturnDataArrayList;
    }

    public FilePathAdapter(Context context, TextView tv, Set<String> path)
    {
        this.mPath = path;
        m_Data = new ArrayList<String>();
        mReturnDataArrayList = new HashSet<String>();
        m_Parent = MNT;
        tv.setText(m_Parent);
        m_Context = context;
        UPPER = "..";
        initData(m_Parent);
    }

    private boolean checkExternalSdcard()
    {
        File f = new File(EXTERNAL_SDCARD + "/1.txt");
        try
        {
            f.createNewFile();
        }
        catch (IOException e)
        {
            Log.e(TAG, "checkExternalSdcard, external sdcard not exists" + f);
            return false;
        }
        if (f.exists())
        {
            Log.e(TAG,
                    "checkExternalSdcard, ****************external sdcard file exists"
                            + f);
            f.deleteOnExit();
            Log.e(TAG,
                    "checkExternalSdcard, ****************external sdcard file deleteOnExit"
                            + f);
            return true;
        }
        else
        {
            Log.e(TAG,
                    "checkExternalSdcard, ****************external sdcard file not exists"
                            + f);
            return false;
        }
    }

    private boolean checkUsbstorage()
    {
        File f = new File(USB_STORAGE + "/1.txt");
        try
        {
            f.createNewFile();
        }
        catch (IOException e)
        {
            Log.e(TAG, "checkUsbstorage, usb_storage not exists" + f);
            return false;
        }
        if (f.exists())
        {
            Log.e(TAG,
                    "checkUsbstorage, ****************usb_storage file exists"
                            + f);
            f.deleteOnExit();
            Log.e(TAG,
                    "checkUsbstorage, ****************usb_storage file deleteOnExit"
                            + f);
            return true;
        }
        else
        {
            Log.e(TAG,
                    "checkUsbstorage, ****************usb_storage file not exists"
                            + f);
            return false;
        }
    }

    public String getParentPath()
    {
        return m_Parent;
    }

    public void initData(String path)
    {
        if (m_Data != null && m_Data.size() > 0)
        {
            m_Data.clear();
        }
        if (path != null && path.equals(UPPER))
        {
            File f2 = new File(m_Parent);
            path = f2.getParent();
        }
        if (path != null && !path.equals(MNT))
        {
            m_Data.add(UPPER);
            File file = new File(path);
            if (file.exists())
            {
                File[] files = file.listFiles();
                if (files != null && files.length > 0)
                {
                    Arrays.sort(files);
                    for (File f : files)
                    {
                        // if (f.isDirectory()) yangxj@20131216
                        if (f.isDirectory()
                                || f.getAbsolutePath().endsWith(".mp3"))
                        {
                            m_Data.add(f.getPath());
                        }
                    }
                }
            }
            else
            {
                Toast.makeText(m_Context, "null path", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (path != null && path.equals(MNT))
        {
            // if (checkUsbstorage())
            // {
            // m_Data.add(USB_STORAGE);
            // }
            // if (checkExternalSdcard())
            // {
            // m_Data.add(EXTERNAL_SDCARD);
            // }
            m_Data.add(SDCARD);
        }
        m_Parent = path;
    }

    private final class ViewHolder
    {
        private TextView textview;

        private CheckBox radiobutton;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder vh = null;
        if (null == convertView)
        {
            vh = new ViewHolder();
            convertView = View.inflate(m_Context,
                    R.layout.fileselectlistview_layout, null);
            vh.textview = (TextView) convertView
                    .findViewById(R.id.fileselectdialog_listview_path_textView);
            vh.radiobutton = (CheckBox) convertView
                    .findViewById(R.id.fileselectldialog_listview_radioButton);
            convertView.setTag(vh);
        }
        else
        {
            vh = (ViewHolder) convertView.getTag();
        }
        String path = m_Data.get(position);
        if (path != null && path.contains("/"))
        {
            int index = path.lastIndexOf("/");
            path = path.substring(index + 1);
        }
        vh.textview.setText(path);
        if (m_Data.get(position).equals(UPPER))
        {
            vh.radiobutton.setVisibility(View.INVISIBLE);
        }
        else
        {
            vh.radiobutton.setVisibility(View.VISIBLE);

            if (mReturnDataArrayList.contains(m_Data.get(position)))
            {
                vh.radiobutton.setChecked(true);
            }
            else
            {
                vh.radiobutton.setChecked(false);
            }
            vh.radiobutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    // mListener.setPath(m_Data.get(position));
                    if (!mReturnDataArrayList.contains(m_Data.get(position)))
                        mReturnDataArrayList.add(m_Data.get(position));
                    else
                        mReturnDataArrayList.remove(m_Data.get(position));
                    Log.e(TAG, "**" + mReturnDataArrayList.toString());
                }
            });
        }
        return convertView;
    }

    @Override
    public int getCount()
    {
        return m_Data.size();
    }

    @Override
    public Object getItem(int position)
    {
        return m_Data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }
}
