package com.example.buglab.liveon.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.example.buglab.liveon.R;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class SelfDiagnosisActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<ArrayList<String>> diseaseList;
    CheckBox[] symptomsCheck=new CheckBox[21];
    int[] dScore;
    Button submitBtn;
    ArrayList total=new ArrayList();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_self_diagnosis);

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
        submitBtn=(Button)findViewById(R.id.symptomsSubmitBtn);
        diseaseList = new ArrayList<>();

        int count=0;
        SQLiteDatabase db2 = openOrCreateDatabase("DATABASE", MODE_PRIVATE, null);
        Cursor cursor = db2.rawQuery("SELECT * FROM SYMPTOMS", null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            String p1 = "" + cursor.getString(cursor.getColumnIndex("Disease"));
            String p2 = "" + cursor.getString(cursor.getColumnIndex("Symptoms"));
            String[] sympDisease=p2.split(",");
            total.add(sympDisease.length);
            Log.d("DISEASESIZE",""+sympDisease.length);
            ArrayList<String> l=new ArrayList<>();
            l.add(p1);
            for (int i=0;i<sympDisease.length;i++)
            {
                l.add(sympDisease[i]);
            }
            diseaseList.add(l);
            count++;
            cursor.moveToNext();
        }
        cursor.close();
        db2.close();

        Log.d("DISEASE",""+count);
        dScore=new int[diseaseList.size()];
        for(int i=0;i<diseaseList.size();i++)
        {
            dScore[i]=0;
        }



        symptomsCheck[0]=(CheckBox)findViewById(R.id.c1);
        symptomsCheck[1]=(CheckBox)findViewById(R.id.c2);
        symptomsCheck[2]=(CheckBox)findViewById(R.id.c3);
        symptomsCheck[3]=(CheckBox)findViewById(R.id.c4);
        symptomsCheck[4]=(CheckBox)findViewById(R.id.c5);
        symptomsCheck[5]=(CheckBox)findViewById(R.id.c6);
        symptomsCheck[6]=(CheckBox)findViewById(R.id.c7);
        symptomsCheck[7]=(CheckBox)findViewById(R.id.c8);
        symptomsCheck[8]=(CheckBox)findViewById(R.id.c9);
        symptomsCheck[9]=(CheckBox)findViewById(R.id.c10);
        symptomsCheck[10]=(CheckBox)findViewById(R.id.c11);
        symptomsCheck[11]=(CheckBox)findViewById(R.id.c12);
        symptomsCheck[12]=(CheckBox)findViewById(R.id.c13);
        symptomsCheck[13]=(CheckBox)findViewById(R.id.c14);
        symptomsCheck[14]=(CheckBox)findViewById(R.id.c15);
        symptomsCheck[15]=(CheckBox)findViewById(R.id.c16);
        symptomsCheck[16]=(CheckBox)findViewById(R.id.c17);
        symptomsCheck[17]=(CheckBox)findViewById(R.id.c18);
        symptomsCheck[18]=(CheckBox)findViewById(R.id.c19);
        symptomsCheck[19]=(CheckBox)findViewById(R.id.c20);
        symptomsCheck[20]=(CheckBox)findViewById(R.id.c21);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             calculateScore();
                Log.d("DISEASECOUNT",""+diseaseList.size());
                StringBuilder result=new StringBuilder("");
                for (int i=0;i<11;i++)
                {
                    Double percent;
                    Log.d("DISEASECOUNT",""+Integer.parseInt(total.get(i).toString()));

                    percent=(Double)(dScore[i]/Double.parseDouble(total.get(i).toString())*100);
                    Log.d("DISEASE",diseaseList.get(i).get(0)+" Score: "+dScore[i]);
                    result.append(""+diseaseList.get(i).get(0)+": \t\t"+percent+"%\n");
                }
                makeAlertBox("Result!",result.toString());
                for (int i=0;i<11;i++)
                {
                    dScore[i]=0;
                }
            }
        });
        makeAlertBox("Symptomps Checker.","Please read carefully and check the symptoms that matches with yours");
        View header = navigationView.getHeaderView(0);
        ImageView headerLogo=(ImageView)header.findViewById(R.id.navHeaderLogoBtn);
        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SelfDiagnosisActivity.this,MainMenuActivity.class);
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
        getMenuInflater().inflate(R.menu.self_diagnosis, menu);
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

    public void calculateScore()
    {

        for(int i=0;i<21;i++)
        {
            if(symptomsCheck[i].isChecked())
            {
                for(int j=0;j<diseaseList.size();j++)
                {

                    for(int k=1;k<diseaseList.get(j).size();k++)
                    {
                        if(symptomsCheck[i].getText().toString().equals(diseaseList.get(j).get(k)))
                        {
                            dScore[j]++;
                        }
                    }
                }
            }
        }
    }

    public void makeAlertBox(String title,String fullInfo)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .create();

        alertDialog.setTitle(title);
        StringBuilder message=new StringBuilder("");
        message.append(fullInfo);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int i=0;i<21;i++)
                {
                    symptomsCheck[i].setChecked(false);
                }
            }
        });
        alertDialog.setMessage(message);
        alertDialog.show();

    }


}
