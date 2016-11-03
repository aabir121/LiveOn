package com.example.buglab.liveon.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.buglab.liveon.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class DoctorInfoActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,AdapterView.OnItemClickListener {

    static float windowHeight;
    static float windowWidth;
    ArrayList<ArrayList<String>> finallist;
    ListView doctorDBList;
    LinearLayout searchBar;

    EditText searchDoctorFilter;

    ArrayAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_doctor_info);

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
        doctorDBList =(ListView)findViewById(R.id.doctorDBListView);
        searchDoctorFilter=(EditText)findViewById(R.id.searchDoctorFilter);
        searchBar=(LinearLayout) findViewById(R.id.searchBarEditText);
        Display display = getWindowManager().getDefaultDisplay();
        windowWidth = display.getWidth();
        windowHeight=display.getHeight();




        finallist=new ArrayList<>();
        SQLiteDatabase db2 = openOrCreateDatabase("DATABASE", MODE_PRIVATE, null);
        Cursor cursor = db2.rawQuery("SELECT * FROM DOCTOR",null);

        cursor.moveToFirst();
        while(cursor.isAfterLast()==false)
        {
            String p1 = ""+cursor.getString(cursor.getColumnIndex("DoctorName"));
            String p2 = ""+cursor.getString(cursor.getColumnIndex("Degree"));
            String p3 = ""+cursor.getString(cursor.getColumnIndex("Department"));
            String p4 = ""+cursor.getString(cursor.getColumnIndex("Chamber"));
            String p5 = ""+cursor.getString(cursor.getColumnIndex("ChamberPhone"));
            String p6 = ""+cursor.getString(cursor.getColumnIndex("Hospital"));
            String p7 = ""+cursor.getString(cursor.getColumnIndex("AppointmentTime"));
            String p8 = ""+cursor.getString(cursor.getColumnIndex("HospitalPhone"));
            ArrayList<String> l=new ArrayList<String>();
            l.add(p1);
            l.add(p2);
            l.add(p3);
            l.add(p4);
            l.add(p5);
            l.add(p6);
            l.add(p7);
            l.add(p8);
            finallist.add(l);

            cursor.moveToNext();
        }
        cursor.close();
        db2.close();
        final ArrayList<String> listarr = new ArrayList<String>();
        for (int i = 0; i <finallist.size(); i++) {
            listarr.add(""+finallist.get(i).get(0)+"\nDepartment: "+finallist.get(i).get(2)+"\nDegree: "+finallist.get(i).get(1));
        }

        adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.list_view_style_text, listarr);
        doctorDBList.setAdapter(adapter);

        doctorDBList.setOnItemClickListener(this);

        FloatingActionButton searchBarToggle=(FloatingActionButton)findViewById(R.id.searchBtn);
        searchBarToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(searchBar.getVisibility()==View.VISIBLE)
                    collapse(searchBar);
                else
                    expand(searchBar);
                Log.d("SearchBar","PRESSED");
            }
        });


        searchDoctorFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                DoctorInfoActivity.this.adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        View header = navigationView.getHeaderView(0);
        ImageView headerLogo=(ImageView)header.findViewById(R.id.navHeaderLogoBtn);
        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DoctorInfoActivity.this,MainMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doctor_info, menu);
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

    public void makeAlertBox(final int i,String fullInfo)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .create();

        alertDialog.setTitle(finallist.get(i).get(0));
        StringBuilder message=new StringBuilder("");
        message.append(fullInfo);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setMessage(message);
        alertDialog.show();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StringBuilder fullinfo=new StringBuilder("");
        fullinfo.append("\nSpeciality: "+finallist.get(position).get(2));
        fullinfo.append("\n"+finallist.get(position).get(1));
        fullinfo.append("\nChamber: "+finallist.get(position).get(3));
        fullinfo.append("\nChamber Phone: "+finallist.get(position).get(4));
        fullinfo.append("\nHospital: "+finallist.get(position).get(5));
        fullinfo.append("\nHospital Phone: "+finallist.get(position).get(7));
        fullinfo.append("\nAppointment Time: "+finallist.get(position).get(6));
        makeAlertBox(position,fullinfo.toString());
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetWidth = (int)(windowWidth-windowWidth/12);

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().width = 1;

        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().width = interpolatedTime == 1
                        ? targetWidth
                        : (int)(targetWidth * interpolatedTime);
//                v.getLayoutParams().width=(int)(windowWidth-windowWidth/4);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetWidth / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialWidth = v.getMeasuredWidth();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().width = initialWidth - (int)(initialWidth * interpolatedTime);
//                    v.getLayoutParams().width=(int)(windowWidth-windowWidth/4);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialWidth / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }
}
