package com.example.buglab.liveon.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.buglab.liveon.R;
import com.example.buglab.liveon.utility.AlarmReceiver;

import java.util.Calendar;

public class AlarmSetActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    int state;
    private PendingIntent pendingIntent;
    EditText medicineDesciptionText;
    TimePicker timePicker;
    EditText intervalText;
    Button setAlarmButton;
    String message;

    static float windowHeight;
    static float windowWidth;
    static float initialHeight;


    CheckBox checkBoxMinutes,checkBoxHours,checkBoxDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alarm_set);

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        FloatingActionButton sideBarToggle=(FloatingActionButton)findViewById(R.id.sideBarToggleBtn);
        sideBarToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        medicineDesciptionText=(EditText)findViewById(R.id.medicineDesciptionText);
        timePicker=(TimePicker)findViewById(R.id.alarmPicker);
        intervalText=(EditText)findViewById(R.id.medicineIntervalText);
        setAlarmButton=(Button)findViewById(R.id.setAlarmBtn);
        checkBoxMinutes=(CheckBox)findViewById(R.id.checkBoxMinutes);
        checkBoxHours=(CheckBox)findViewById(R.id.checkBoxHours);
        checkBoxDays=(CheckBox)findViewById(R.id.checkBoxDays);



        Display display = getWindowManager().getDefaultDisplay();
        windowWidth = display.getWidth();
        windowHeight = display.getHeight();
        initialHeight=medicineDesciptionText.getLayoutParams().height;

        SharedPreferences prefs = getSharedPreferences("Live_ON", MODE_PRIVATE);
        state = prefs.getInt("No Alarm", 0); //0 is the default value.


        medicineDesciptionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                expand(v);
            }
        });


        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                start();

                if(!intervalText.getText().toString().isEmpty()){
                    String message=medicineDesciptionText.getText().toString();

                    AlarmReceiver.message=message;
                    Intent alarmIntent = new Intent(AlarmSetActivity.this, AlarmReceiver.class);
                    final int _id = (int) System.currentTimeMillis();
                    alarmIntent.putExtra("requestCode",""+_id);

//                Bundle bundle = new Bundle();
//                bundle.putString("reuqestCode", extra);
//                intent.putExtras(bundle);
                    String alarmTime=timePicker.getCurrentHour()+":"+timePicker.getCurrentMinute();
                    pendingIntent = PendingIntent.getBroadcast(AlarmSetActivity.this, _id, alarmIntent, PendingIntent.FLAG_ONE_SHOT);
                    SharedPreferences prefs = getSharedPreferences("Live_ON", MODE_PRIVATE);
                    state = prefs.getInt("No Alarm", 0); //0 is the default value.

                    if(state==0){
                        SharedPreferences settings = getSharedPreferences("Live_ON", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("No Alarm",1);
                        editor.commit();

                        SQLiteDatabase db =openOrCreateDatabase("DATABASE",MODE_PRIVATE,null);
                        db.execSQL("DROP TABLE IF EXISTS ALARM");

                        db.execSQL("CREATE TABLE IF NOT EXISTS ALARM (ALARMID INT,ALARMMESSAGE VARCHAR,ALARMTIME VARCHAR);");

                        db.execSQL("INSERT INTO ALARM VALUES ('"+_id+"','"+message+"','"+alarmTime+"')");

                        db.close();
                        Log.d("ALARMDB","New Databse");
                    }
                    else
                    {
                        SQLiteDatabase db =openOrCreateDatabase("DATABASE",MODE_PRIVATE,null);
                        db.execSQL("INSERT INTO ALARM VALUES ('"+_id+"','"+message+"','"+alarmTime+"')");
                        db.close();
                        Log.d("ALARMDB","Update Database");
                    }


                    int hour=timePicker.getCurrentHour();
                    int minutes= timePicker.getCurrentMinute();
                    int interval= Integer.parseInt(intervalText.getText().toString());
                    if(checkBoxMinutes.isChecked())
                    {
                        Toast.makeText(AlarmSetActivity.this, "Alarm set at "+hour+":"+minutes+"\nat a "+interval+" minutes interval.", Toast.LENGTH_SHORT).show();
                    }
                    else if(checkBoxHours.isChecked())
                    {
                        interval=interval*60;
                        Toast.makeText(AlarmSetActivity.this, "Alarm set at "+hour+":"+minutes+"\nat a "+intervalText.getText().toString()+" Hours interval.", Toast.LENGTH_SHORT).show();
                    }
                    else if(checkBoxDays.isChecked())
                    {
                        interval=interval*60*24;
                        Toast.makeText(AlarmSetActivity.this, "Alarm set at "+hour+":"+minutes+"\nat a "+intervalText.getText().toString()+" Days interval.", Toast.LENGTH_SHORT).show();
                    }

                    startAtSpecific(hour,minutes,interval);
                    Intent intent=new Intent(AlarmSetActivity.this,AlarmMainActivity.class);
                    startActivity(intent);
                }
                else {
                    intervalText.setError("Please set an alarm interval");
                }

            }
        });
        View header = navigationView.getHeaderView(0);
        ImageView headerLogo=(ImageView)header.findViewById(R.id.navHeaderLogoBtn);
        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AlarmSetActivity.this,MainMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
//        else if(medicineDesciptionText.getLayoutParams().height>initialHeight)
//        {
//            collapse(medicineDesciptionText);
//        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_set, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_emergency) {
            Intent intent=new Intent(this,EmergencyContactActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_medical_service) {
            Intent intent=new Intent(this,MedicalServicesInitialActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_medical_studies) {

            Intent intent=new Intent(this,MedicalStudiesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_self_diagnose) {
            Intent intent=new Intent(this,SelfDiagnosisActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_rate) {

        } else if (id == R.id.nav_medicine_alarm) {
            Intent intent=new Intent(this,AlarmMainActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.nav_emergency_search)
        {
            Intent intent=new Intent(this,EmergencySearchActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void start() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }


    public void startAtSpecific(int hour,int minute,int interval) {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        int interval = 1000 * 60 * 20;

        /* Set the alarm to start at 10:30 AM */
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        Log.d("Alarm","In Milli "+calendar.getTimeInMillis());
        Log.d("Alarm","Time: "+calendar.getTime());

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * interval, pendingIntent);
    }

    public void onCheckboxClicked(View view) {

        switch(view.getId()) {

            case R.id.checkBoxMinutes:

                checkBoxHours.setChecked(false);
                checkBoxDays.setChecked(false);

                break;

            case R.id.checkBoxHours:

                checkBoxDays.setChecked(false);
                checkBoxMinutes.setChecked(false);

                break;

            case R.id.checkBoxDays:

                checkBoxMinutes.setChecked(false);
                checkBoxHours.setChecked(false);

                break;
        }
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = (int)windowHeight/4;

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? targetHeight
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.getLayoutParams().height=initialHeight;
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
