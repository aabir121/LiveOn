package com.example.buglab.liveon.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.buglab.liveon.R;

import java.util.ArrayList;

public class MedicalStudiesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private WebView mWebview ;
    LinearLayout webView;
    Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_studies);

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



        spinner=(Spinner)findViewById(R.id.medicalStudiesSpinner);
        webView=(LinearLayout)findViewById(R.id.medicalStudiesWEbView);

        mWebview  = new WebView(this);

        mWebview.getSettings().setJavaScriptEnabled(true); // enable javascript

        final Activity activity = this;

        mWebview.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
            }
        });

        mWebview .loadUrl("http://www.google.com");
        webView.addView(mWebview);



        String[] arraySpinner;

        ArrayList<String> arrayList=new ArrayList<>();
        ArrayList<ArrayList<String>> finalLinks=new ArrayList<>();

        arrayList.add("Denue Fever");
        arrayList.add("http://www.webmd.com/a-to-z-guides/dengue-fever-reference#1");
        finalLinks.add(arrayList);

        arrayList.add("Thalassemia");
        arrayList.add("http://www.healthline.com/health/thalassemia");
        finalLinks.add(arrayList);

        arrayList.add("Typhoid Fever");
        arrayList.add("http://www.webmd.com/a-to-z-guides/typhoid-fever");
        finalLinks.add(arrayList);


        arraySpinner = new String[] {
                "Dengue Fever",
                "Thalassemia",
                "Typhoid Fever"
        };

        ArrayAdapter adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, arraySpinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String url="";
                if(parent.getItemAtPosition(position).toString().equals("Thalassemia"))
                    url= "http://www.healthline.com/health/thalassemia";
                else if(parent.getItemAtPosition(position).toString().equals("Dengue Fever"))
                    url= "http://www.webmd.com/a-to-z-guides/dengue-fever-reference#1";
                else if(parent.getItemAtPosition(position).toString().equals("Typhoid Fever"))
                    url="http://www.webmd.com/a-to-z-guides/typhoid-fever";

                mWebview.loadUrl(url);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        getMenuInflater().inflate(R.menu.medical_studies, menu);
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
}
