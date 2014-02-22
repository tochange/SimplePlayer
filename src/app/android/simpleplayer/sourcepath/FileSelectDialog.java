package app.android.simpleplayer.sourcepath;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import app.android.simpleplayer.R;

public class FileSelectDialog extends Dialog
{

    private final String TAG = "FileSelectDialog";

    private Context m_Context;

    private ListView m_PathLsit;

    private Button m_CancelBtn;

    private FilePathAdapter m_PathAdapter;

    private TextView m_CurrentTextView;

    private Button m_FreshPathBtn;

    public FileSelectDialog(Context context, Set<String> path,
            final IFilePathChooseListener listener)
    {
        // super(context, R.style.fileselectdialog);
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.fileselectdialog_layout);
        setCanceledOnTouchOutside(true);
        m_Context = context;
        m_CurrentTextView = (TextView) findViewById(R.id.fileselectdialog_current_textView);
        m_FreshPathBtn = (Button) findViewById(R.id.button1_freshpath);
        m_PathLsit = (ListView) findViewById(R.id.fileselectdialog_listView);
        m_CancelBtn = (Button) findViewById(R.id.fileselectdialog_cancel_button);

        m_PathAdapter = new FilePathAdapter(m_Context, m_CurrentTextView, path);
        m_PathLsit.setOnItemClickListener(m_OnItemClickListener);
        m_PathLsit.setAdapter(m_PathAdapter);

        m_CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                dismiss();
            }
        });

        m_FreshPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // if(m_PathAdapter != null){
                // m_PathAdapter.initData(m_CurrentTextView.getText().toString());
                // m_PathAdapter.notifyDataSetChanged();
                // }
                HashSet<String> list = m_PathAdapter.getDataList();
                if (list != null && !list.isEmpty())
                {
                    listener.setPath(list);
                    dismiss();
                }
                else
                    Toast.makeText(m_Context, "path is null or empth.",
                            Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 鍒楄〃鐩戝惉
     */
    private OnItemClickListener m_OnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> listview, View item,
                int position, long id)
        {
            // 搴旇鍏堝緱鍒颁竴涓叿浣撶殑璺緞锛岀劧鍚庡緱鍒扮殑鏄璺緞涓嬬殑鎵�湁鏂囦欢澶�
            String path = m_PathAdapter.getItem(position).toString().trim();
            File f = new File(path);
            if (f.isFile())
                return;
            m_PathAdapter.initData(path);
            m_CurrentTextView.setText(m_PathAdapter.getParentPath());
            m_PathAdapter.notifyDataSetChanged();
        }
    };
}
