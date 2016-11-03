package com.example.buglab.liveon.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.model.Line;
import com.example.buglab.liveon.R;
import com.example.buglab.liveon.utility.AlarmReceiver;

import java.util.ArrayList;

public class AlarmMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout rootLayout;
    ArrayList<ArrayList<String>> finallist;
    Button addnewBtn,deleteAllBtn;
    ListView alarmListView;
    int state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_alarm_main);

        FloatingActionButton sideBarToggle=(FloatingActionButton)findViewById(R.id.sideBarToggleBtn);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sideBarToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        rootLayout=(LinearLayout)findViewById(R.id.alarmListRootLayout);
        addnewBtn=(Button)findViewById(R.id.addNewAlarmActivityBtn);
        deleteAllBtn=(Button)findViewById(R.id.deleteAllBtn);

        finallist=new ArrayList<>();
        alarmListView=(ListView)findViewById(R.id.alarmListView);
//        LinearLayout root=new LinearLayout(AlarmMainActivity.this);
//        root.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
//        root.setOrientation(LinearLayout.VERTICAL);
//        rootLayout.addView(root);
        SharedPreferences prefs = getSharedPreferences("Live_ON", MODE_PRIVATE);
        state = prefs.getInt("No Alarm", 0); //0 is the default value.

        if(state==0)
        {
            Intent intent=new Intent(AlarmMainActivity.this,AlarmSetActivity.class);
            startActivity(intent);
        }
        else
        {
            SQLiteDatabase db2 = openOrCreateDatabase("DATABASE", MODE_PRIVATE, null);
            Cursor cursor = db2.rawQuery("SELECT * FROM ALARM",null);

            cursor.moveToFirst();
            while(cursor.isAfterLast()==false)
            {
                String p1 = cursor.getString(cursor.getColumnIndex("ALARMID"));
                String p2 = cursor.getString(cursor.getColumnIndex("ALARMMESSAGE"));
                String p3 = cursor.getString(cursor.getColumnIndex("ALARMTIME"));
                ArrayList<String> l=new ArrayList<String>();
                l.add(p1);
                l.add(p2);
                l.add(p3);
                finallist.add(l);


                cursor.moveToNext();
            }
            cursor.close();
            db2.close();
            final ArrayList<String> listarr = new ArrayList<String>();
            for (int i = 0; i <finallist.size(); i++) {
                listarr.add("Alarm ID: "+finallist.get(i).get(0)+"\nMedicine: "+finallist.get(i).get(1)+"\nTime: "+finallist.get(i).get(2));
            }

            final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                    R.layout.list_view_style_text, listarr);
            alarmListView.setAdapter(adapter);
        }


        addnewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AlarmMainActivity.this,AlarmSetActivity.class);
                startActivity(intent);
            }
        });
        View header = navigationView.getHeaderView(0);
        ImageView headerLogo=(ImageView)header.findViewById(R.id.navHeaderLogoBtn);
        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AlarmMainActivity.this,MainMenuActivity.class);
                startActivity(intent);
            }
        });

        alarmListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int alarmid;
                    alarmid=Integer.parseInt(finallist.get(position).get(0));


                makeAlertBox(alarmid,0);
            }
        });

        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAlertBox(1,1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            Intent intent=new Intent(AlarmMainActivity.this,MainMenuActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.alarm_main, menu);
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

    public void cancel(int _id) {
        Intent alarmIntent = new Intent(AlarmMainActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("requestCode",""+_id);

        SQLiteDatabase db2 = openOrCreateDatabase("DATABASE", MODE_PRIVATE, null);
        String[] params = new String[]{""+_id};
        db2.execSQL("DELETE FROM ALARM WHERE ALARMID = ?",params);
        db2.close();


        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(PendingIntent.getService(this ,_id ,alarmIntent , 0));
//        manager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }
    public void makeAlertBox(final int i,final int state)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .create();
        StringBuilder message=new StringBuilder("");

        if(state==0){
            alertDialog.setTitle("Cancel Alarm");
            message.append("Do you want to cancel this alarm?");
        }
        else if(state==1)
        {
            alertDialog.setTitle("Cancel All Alarms!");
            message.append("Do you want to cancel all existing alarms?");
        }
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(state==0)
                {
                    cancel(i);
                    Intent intent=new Intent(AlarmMainActivity.this,AlarmMainActivity.class);
                    startActivity(intent);


                }
                else if(state==1)
                {
                    deleteDB();
                    Intent intent=new Intent(AlarmMainActivity.this,AlarmMainActivity.class);
                    startActivity(intent);

                }
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setMessage(message);
        alertDialog.show();
    }
    public void deleteDB()
    {
        SQLiteDatabase db2 = openOrCreateDatabase("DATABASE", MODE_PRIVATE, null);
        db2.delete("ALARM", "1", null);
        db2.close();

    }
}
