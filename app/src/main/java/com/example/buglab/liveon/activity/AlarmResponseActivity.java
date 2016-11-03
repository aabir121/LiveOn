package com.example.buglab.liveon.activity;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.buglab.liveon.R;
import com.example.buglab.liveon.utility.AlarmReceiver;

public class AlarmResponseActivity extends Activity {

    int id;
    public static TextView description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alarm_response);

        if(getIntent().hasExtra(AlarmReceiver.WAKE) && getIntent().getExtras().getBoolean(AlarmReceiver.WAKE)){
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

            id=Integer.parseInt(getIntent().getStringExtra("requestCode"));
            Log.d("ALARMDB",""+id);
            String message=getDBValue(this,id);
            description=(TextView)findViewById(R.id.medicineDescriptionAlarm);
            description.setText(""+message);
        }
    }


    public String getDBValue(Context context,int id)
    {

        SQLiteDatabase db2 = openOrCreateDatabase("DATABASE", MODE_PRIVATE, null);

        String[] params = new String[]{""+id};
        Cursor cursor = db2.rawQuery("SELECT * FROM ALARM WHERE ALARMID = ?",params);

        if(cursor!=null) {
            cursor.moveToFirst();
            Log.d("ALARMDB", cursor.getString(1));
            String p1 = cursor.getString(cursor.getColumnIndex("ALARMMESSAGE"));
            return p1;
        }
        return "NO DATA";
    }

}
