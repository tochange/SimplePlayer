package app.android.simpleplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import app.android.simpleplayer.log.MyLog;
import app.android.simpleplayer.utils.ExitAllActivityApplication;

public class CoverActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cover);

        ExitAllActivityApplication.getInstance().addActivity(this);

//      MyLog.captureLogToFile(this, getApplication()
//                .getPackageName());

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run()
            {
                Intent mainIntent = new Intent(CoverActivity.this,
                        PlayerActivity.class);
                startActivity(mainIntent);
                CoverActivity.this.finish();
            }

        }, 200);

    }
}