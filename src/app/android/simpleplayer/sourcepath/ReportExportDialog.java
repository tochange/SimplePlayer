package app.android.simpleplayer.sourcepath;


import java.util.HashSet;
import java.util.Set;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import app.android.simpleplayer.R;

public class ReportExportDialog extends Dialog implements
        android.view.View.OnClickListener, IFilePathChooseListener
{

    @Override
    protected void onStop()
    {
        // TODO Auto-generated method stub
        super.onStop();
        Log.e("yang", "ssssssssssssssssssssssssssssss" + mPath);
    }

    /**
     * 搴旂敤绋嬪簭涓婁笅鏂�
     */
    private Context mContext;

    /**
     * 娴忚鎸夐挳
     */
    private Button mBrowserBtn;

    /**
     * 纭畾鎸夐挳
     */
    private Button mConfirmBtn;

    /**
     * 鍙栨秷鎸夐挳
     */
    private Button mCancelBtn;

    /**
     * 璺緞杈撳叆妗�
     */
    private EditText mPathEditText;

    /**
     * 鏍囬鏍�
     */
    private TextView mTitleTextView;

    /**
     * 鎶ュ憡瀵煎嚭鎺ュ彛
     */
    private IReportExportListener mListener;

    /**
     * 閫変腑鐨勮矾寰�
     */
    private Set<String> mPath;

    /**
     * 鏂囦欢閫夋嫨瀵硅瘽妗�
     */
    String TAG = "ReportExportDialog";

    private FileSelectDialog mFileSelectDialog;

    public ReportExportDialog(Context context, IReportExportListener listener)
    {
        // super(context, R.style.exportdialog);
        super(context);
        this.mContext = context;
        this.mListener = listener;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reportexportdialog_layout);
        setCanceledOnTouchOutside(true);
        initObject();

        newDialog();
    }

    public ReportExportDialog(Context context, Set<String> path,
            IReportExportListener listener)
    {
        // super(context, R.style.exportdialog);
        super(context);
        this.mContext = context;
        this.mListener = listener;
        mPath = path;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reportexportdialog_layout);
        setCanceledOnTouchOutside(true);
        initObject();

        newDialog();
    }

    private void initObject()
    {
        mBrowserBtn = (Button) findViewById(R.id.reportexportdialog_browser_button);
        mConfirmBtn = (Button) findViewById(R.id.reportexportdialog_confirm_button);
        mCancelBtn = (Button) findViewById(R.id.reportexportdialog_cancel_button);
        mPathEditText = (EditText) findViewById(R.id.reportexportdialog_editText);
        mBrowserBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
    }

    @Override
    public void setPath(HashSet<String> pathList)
    {
        // mPath = path;
        // mPathEditText.setText(mPath);
        if (mFileSelectDialog != null && mFileSelectDialog.isShowing())
        {
            // mFileSelectDialog.dismiss();
        }

        mListener.reportExportConfirmClick(pathList);// yangxj@20131210
    }

    public void newDialog()
    {
        Log.e(TAG, "mPath=" + mPath);
        mFileSelectDialog = new FileSelectDialog(mContext, mPath, this);
        // mFileSelectDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mFileSelectDialog.show();
    }

    @Override
    public void onClick(View v)
    {
        if (v == mBrowserBtn)
        {
            // newDialog();
        }
        else if (v == mConfirmBtn)
        {
            if ("".equals(mPathEditText.getText().toString().trim()))
            {
                Toast.makeText(mContext, "璇锋寚瀹氬鍑鸿矾寰勶紒", Toast.LENGTH_LONG).show();
                return;
            }
            if (mPathEditText.getText().toString().trim()
                    .equals("/mnt/sdcard/DC4601-DATA"))
            {
                Toast.makeText(mContext, "invalidpath", Toast.LENGTH_LONG)
                        .show();
                return;
            }
            // mListener.reportExportConfirmClick(mPath);
            dismiss();
        }
        else if (v == mCancelBtn)
        {
            dismiss();
        }
    }

}
