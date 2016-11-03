package com.example.buglab.liveon.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.buglab.liveon.R;
import com.example.buglab.liveon.utility.ShakeDetector;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.nearby.messages.internal.HandleClientLifecycleEventRequest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    int DBState;
    Button medicalServicesBtn,medicalStudiesBtn,selfDiagnosBtn;


    // The following are used for the shake detection
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    Context context=this;
    ProgressDialog progressDialog;
    FloatingActionButton emergencyTextBtn;
    Button emergencyButton;
    ImageButton sideBarToggleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main_menu);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        medicalServicesBtn=(Button)findViewById(R.id.medicalServicesBtn);
        medicalStudiesBtn=(Button)findViewById(R.id.medicalStudiesBtn);
        selfDiagnosBtn=(Button)findViewById(R.id.selfDiagnosBtn);

        medicalServicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainMenuActivity.this,MedicalServicesInitialActivity.class);
                startActivity(intent);
            }
        });


        SharedPreferences prefs = getSharedPreferences("Live_ON", MODE_PRIVATE);
        DBState = prefs.getInt("FirstUseDB", 0);

//        if(DBState==0)
//        {
        InitDatabase();
        SharedPreferences settings = getSharedPreferences("Live_ON", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("FirstUseDB",1);
//        }



        sideBarToggleButton=(ImageButton)findViewById(R.id.sideBarToggleBtn);
        sideBarToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        emergencyButton=(Button)findViewById(R.id.emergencySearchBtn);
        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("button pressed","press");
                if(haveNetworkConnection() && haveGPS()){
                    Intent intent=new Intent(MainMenuActivity.this,EmergencySearchActivity.class);
                    startActivity(intent);

                }
                else if(!haveGPS())
                {
                    showSettingsAlert();

                }
                else
                {
                    makeAlert("Network Connection Needed","This service needs an active internet connection to work.\n Do you want to activate your internet connection?",1);
                }
            }
        });

        progressDialog=new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Sending Messages. Please wait....");

        emergencyTextBtn=(FloatingActionButton)findViewById(R.id.emergencyTextBtn);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        emergencyTextBtn.startAnimation(shake);
        emergencyTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
//                sendMessageToContacts();
                new SMSSyncTask(progressDialog).execute();

            }
        });

        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            @Override
            public void onShake(int count) {
				/*
				 * The following method, "handleShakeEvent(count):" is a stub //
				 * method you would use to setup whatever you want done once the
				 * device has been shook.
				 */
                handleShakeEvent(count);
            }
        });

        medicalStudiesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainMenuActivity.this,MedicalStudiesActivity.class);
                startActivity(intent);
            }
        });
        selfDiagnosBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainMenuActivity.this,SelfDiagnosisActivity.class);
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
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            MainMenuActivity.this.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
            emergencyButton.performClick();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    public String getPosition() {
        StringBuilder positionMessage=new StringBuilder("");
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // get the last know location from your location manager.
        boolean permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        try {
            if (permissionGranted) {
                // {Some Code}
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                // now get the lat/lon from the location and do something with it.
                positionMessage.append("I need immediate help.\n");
                positionMessage.append("https://maps.google.com/?q=");
                positionMessage.append(location.getLatitude());
                positionMessage.append(",");
                positionMessage.append(location.getLongitude());

            } else {
                ActivityCompat.requestPermissions(getParent(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 200);
            }
        }catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }
        return positionMessage.toString();
    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }


//    public void makeAlert(String title, String message)
//    {
//        final AlertDialog alertDialog = new AlertDialog.Builder(this)
//                .create();
//
////                alertDialog.setTitle("Location Selected");
////
////                alertDialog.setMessage("Add this Location to your");
////                AlertDialog alertDialog=new AlertDialog.Builder(getApplicationContext()).create();
//        alertDialog.setTitle(title);
//        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Wi-fi", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//                wifi.setWifiEnabled(true);
//                Intent intent=new Intent(MainMenuActivity.this,EmergencySearchActivity.class);
//                startActivity(intent);
//            }
//        });
//        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
////        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Mobile Data", new DialogInterface.OnClickListener() {
////            @Override
////            public void onClick(DialogInterface dialog, int which) {
////                try {
////                    setMobileDataEnabled(MainMenuActivity.this,true);
////                } catch (ClassNotFoundException e) {
////                    e.printStackTrace();
////                } catch (NoSuchFieldException e) {
////                    e.printStackTrace();
////                } catch (IllegalAccessException e) {
////                    e.printStackTrace();
////                } catch (NoSuchMethodException e) {
////                    e.printStackTrace();
////                } catch (InvocationTargetException e) {
////                    e.printStackTrace();
////                }
////            }
////        });
//        alertDialog.setMessage(message);
//        alertDialog.show();
//    }

    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }


    public void handleShakeEvent(int count)
    {
        if(count>2){
            final int[] progress = new int[1];
            progressDialog.setMessage("Sending messags in....."+ 5);

            progressDialog.show();
            final int[] pgr = {5};
            CountDownTimer countDownTimer=new CountDownTimer(5000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    pgr[0]--;
                    progressDialog.setMessage("Counting..."+ pgr[0]);
                }

                @Override
                public void onFinish() {
                    progressDialog.dismiss();
//                    sendMessageToContacts();
                    new SMSSyncTask(progressDialog).execute();

                }
            }.start();
        }
    }



    class SMSSyncTask extends AsyncTask<String,Void,Void>{

        ProgressDialog progressDialog;

        public SMSSyncTask(ProgressDialog progressDialog)
        {
            this.progressDialog=progressDialog;
        }
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sending Message....");
            progressDialog.show();
//            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            progressDialog.dismiss();
            makeAlert("Messages Sent!","Emergency messages have been sent to all of your contacts.",0);
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(String... params) {
            sendMessageToContacts();
//            progressDialog.setMessage("Sending message to: "+contact);
//            Log.d("AsyncSMS",contact);
            return null;
        }
        private void sendSMS(String phoneNumber, String message)
        {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPI = PendingIntent.getBroadcast(MainMenuActivity.this, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(MainMenuActivity.this, 0,
                    new Intent(DELIVERED), 0);

            //---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS sent",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), "Generic failure",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), "No service",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Null PDU",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), "Radio off",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(SENT));

            //---when the SMS has been delivered---
            registerReceiver(new BroadcastReceiver(){
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode())
                    {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(getBaseContext(), "SMS not delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }
        public void sendMessageToContacts()
        {
            SharedPreferences prefs2 = getSharedPreferences("Live_ON", MODE_PRIVATE);
            if (prefs2.getInt("FirstUseContact",0)!=0)
            {
//                    progressDialog.show();
                final String c1="+880"+prefs2.getString("C1","");
                final String c2="+880"+prefs2.getString("C2","");
                final String c3="+880"+prefs2.getString("C3","");
                final String c4="+880"+prefs2.getString("C4","");
                final String c5="+880"+prefs2.getString("C5","");

//                final int[] temp = {0};
//                final String[] contactTosend = new String[1];
//                CountDownTimer countDownTimer=new CountDownTimer(14000,2000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        if(temp[0] ==0)
//                            contactTosend[0] =c1;
//                        else if(temp[0] ==1)
//                            contactTosend[0] =c2;
//                        else if(temp[0] ==2)
//                            contactTosend[0] =c3;
//                        else if(temp[0] ==3)
//                            contactTosend[0] =c4;
//                        else if(temp[0] ==4)
//                            contactTosend[0] =c5;
//                        Log.d("AsyncSMS",""+temp[0]);
//                        progressDialog.setMessage("Sending Message to "+contactTosend[0]);
//                        sendSMS(contactTosend[0],getPosition());
//                        temp[0]++;
//                        if(temp[0]>5)
//                            onFinish();
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        progressDialog.dismiss();
//                        makeAlert("Messages Sent!","Emergency messages have been sent to all of your contacts.",0);
//                    }
//                }.start();
////            new SMSSyncTask(progressDialog).execute(c2,getPosition());
////            new SMSSyncTask(progressDialog).execute(c3,getPosition());
////            new SMSSyncTask(progressDialog).execute(c4,getPosition());
////            new SMSSyncTask(progressDialog).execute(c5,getPosition());

            sendSMS(c1,getPosition());
            sendSMS(c2,getPosition());
            sendSMS(c3,getPosition());
            sendSMS(c4,getPosition());
            sendSMS(c5,getPosition());
                progressDialog.dismiss();
            }
            else {
                Intent intent=new Intent(MainMenuActivity.this,EmergencyContactActivity.class);
                startActivity(intent);
            }
        }

    }

    public void makeAlert(String title, String message, final int state)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .create();
        alertDialog.setTitle(title);
        String positiveBtnText = "Yes";
        if(state==0)
            positiveBtnText="Okay";
        else if(state==1)
            positiveBtnText="Wi-fi";
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, positiveBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(state==0)
                {
                    alertDialog.dismiss();
                }
                else if(state==1)
                {
                    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    wifi.setWifiEnabled(true);
                    Intent intent=new Intent(MainMenuActivity.this,EmergencySearchActivity.class);
                    startActivity(intent);
                }
            }
        });
        if(state==1)
        {
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

        }
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    public void showSettingsAlert(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                if(haveNetworkConnection())
                {

                }
                else {
                    makeAlert("Network Connection Needed","This service needs an active internet connection to work.\n Do you want to activate your internet connection?",1);

                }
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public boolean haveGPS()
    {
        final LocationManager manager = (LocationManager)context.getSystemService    (Context.LOCATION_SERVICE );

        return manager.isProviderEnabled( LocationManager.GPS_PROVIDER );
    }


    public void InitDatabase(){

        SQLiteDatabase db = openOrCreateDatabase("DATABASE",MODE_PRIVATE,null);
        db.execSQL("DROP TABLE IF EXISTS HOSPITAL");

        db.execSQL("CREATE TABLE IF NOT EXISTS HOSPITAL (HospitalId INT, HospitalName VARCHAR, Area VARCHAR, Address VARCHAR, Hotline VARCHAR, Email VARCHAR);");


        db.execSQL("INSERT INTO HOSPITAL VALUES ('1', 'Popular Medical College Hospital & Diagnostic Centre LTD.', 'Dhanmondi', 'House #7, Road #2, Dhanmondi, Dhaka-1205', '01961041318-21, 9663301, 9662286, 9660886, 9661532, 9669752, 9675810', 'popularhospital@popularbd.com')");
        db.execSQL("INSERT INTO HOSPITAL VALUES ('2', 'LabAid Specialized Hospital & Diagnostic Centre LTD.', 'Dhanmondi', 'House #, Road #, Dhanmondi, Dhaka-1205', 'N/A', 'N/A')");

//        db2.close();

        Log.d("DB","INITIALIZE");

//        SQLiteDatabase db =openOrCreateDatabase("LiveOnDB",MODE_PRIVATE,null);
        db.execSQL("DROP TABLE IF EXISTS DOCTOR");

        db.execSQL("CREATE TABLE IF NOT EXISTS DOCTOR (DoctorId INT, DmdcRegNo VARCHAR, DoctorName VARCHAR,KICHUEKTA VARCHAR, Degree VARCHAR, Department VARCHAR, Chamber VARCHAR, ChamberPhone VARCHAR, Hospital VARCHAR, AppointmentTime VARCHAR, HospitalPhone VARCHAR);");


        db.execSQL("INSERT INTO DOCTOR VALUES ('1', '', 'Professor Dr. M. Nazrul Islam', '', 'MBBS, FCPS, FRCP, FACC, FESC', 'Cardiology', '', '', 'Popular', '3 pm –  5 pm & 8 pm –  10 pm (except Thursday & Friday)', '01553341060-1, 01553341063, 9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('2', '', 'Professor Dr. Abdullah-Al-Safi', '', 'MBBS, D. Card, MD(Card), FACC, FSGC, FRCP Research Fellow, NCVC, (Japan) WHO Fellow in Cardiology, USA', 'Cardiology', '', '', 'Popular', '11 am – 1 pm & 5 pm – 7 pm (except Friday)', '01553341060-1, 01553341063, 9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('3', '', 'Professor Dr. Md. Abu Siddique', '', 'MBBS, FPGCS ( Medicine ) , PhD ( Cardiology ) ', 'Cardiology', '', '', 'Popular', '5 pm – 9 pm (except Thursday & Friday)', '9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('4', '', 'Dr. Khandaker Qamrul Islam', '', 'MBBS, D.Card (DU), Md (Cardioloty), FACC (USA)', 'Cardiology', '', '', 'Popular', '7 pm – 10 pm (except Tuesday, Thursday, Friday)', '01553341060-1, 01553341063,9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('5', '', 'Professor Dr. M A Azhar', '', 'MBBS, FCPS, FACP, FRCP (Edin)', 'Medicine', '', '', 'Popular', '4 pm – 8 pm (except Friday)', '01553341060-1, 01553341063, 9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('6', '', 'Professor Dr. Md. Enamul Karim', '', 'MBBS, FCPS (Medicine), FACP (USA) WHO Fellow (Diabetics)', 'Medicine', '', '', 'Popular', '5 pm – 9 pm (except Thursday & Friday)', '01553341060-1, 01553341063, 9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('7', '', 'Dr. Q. Tarikul Islam', '', 'MBBS, FCPS, FACP (USA), FRCP', 'Medicine', '', '', 'Popular', '5 pm -9 pm (except Friday)', '9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('8', '', 'Dr. Md. Titu Miah', '', 'MBBS, FCPS (Medicine)', 'Medicine', '', '9661491-3', 'Popular', '4 pm - 10 pm (Except Friday)', '9669480')");
//        db.execSQL("INSERT INTO DOCTOR VALUES ('9' '', 'Prof (Dr.) HAM Nazmul Ahsan', '', 'MBBS, FCPS, FRCP (Glasgow), FRCP (Edin), FACP (USA)', 'Medicine', '', '', 'Popular', '5 pm – 8 pm (except Friday)', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('10', '', 'Dr. Md. Rafiqul Alam', '', 'MBBS, FCPS (Medicine), MD(Chest)', 'Chest Medicine', '', '', 'Popular', '4 pm – 7 pm (except Friday)', '9669480, 9661491-3 ')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('11', '', 'Dr. K.C Ganguly, MD', '', 'MBBS, DTCD, MD, FCPS, MCPS ( USA )', 'Chest Medicine', '', '', 'Popular', '5 pm – 9 pm (except Thursday, Friday, Saturday)', '9662741')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('12', '', 'Prof. (Dr.) Md. Golam Kibria Khan', '', 'MBBS (Dhaka), FCPS (Medicine), MACP (USA), FACP (USA)', 'Rheumatology Medicine', '', '', 'Popular', '5:30 pm – 9 pm (except Friday)', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('13', '', 'Dr. A K M Motiur Rahman Bhuiyan', '', 'MBBS, MPH, MD (Medicine)', 'Rheumatology Medicine', '', '', 'Popular', '7 pm – 9 pm (except Friday)', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('14', '', 'Prof. (Dr.) Mohd. Taslim Uddin', '', 'MBBS, FCPS, Musculoskeletal System Physician', 'Physical Medicine & Rehabilitation', '', '', 'Popular', '6 pm – 8 pm (except Thursday, Friday & Govt. Holidays)', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('15', '', 'Dr. Monirul Islam', '', 'MBBS, FCPS', 'Physical Medicine & Rehabilitation', '', '', 'Popular', '6 pm – 9 pm (except Thursday, Friday)', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('16', '', 'Prof. (Dr.) Kazi Manzur Kader', '', 'MBBS, DMRT, MSc, FACP', 'Oncology', '', '', 'Popular', '5 pm – 9 pm (except Friday)', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('17', '', 'Prof. Zafor Md. Masud', '', 'MBBS, MPhil, FCPS', 'Oncology', '', '', 'Popular', '6:30 pm – 8:30 pm (except Tuesday, Wednesday & Friday)', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('18', '', 'Professor Dr. Syed Wahidur Rahman', '', 'MBBS, FCPS (Medicine)', 'Neurology', '', '', 'Popular', '6 pm - 9 pm (except Thursday & Friday)', '9669480, 9661491-3 ')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('19', '', 'Dr. Nirmalendu Bikash Bhowmik', '', 'MBBS, MD ( Neorology )', 'Neurology', '', '', 'Popular', '6 pm - 9 pm (except Friday)', '9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('20', '', 'Dr. Sehelly Jahan', '', 'MBBS, MD ( Neorology )', 'Neurology', '', '', 'Popular', '6 pm – 9 pm (except Thursday & Friday)', '9669480, 9661491-3 ')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('21', '', 'Dr. Narayan Chandra Kundu', '', 'MBBS, FCPS (Medicine), MD (Neuromedicine) MACP (USA)', 'Neurology', '', '', 'Popular', '6 pm – 9 pm (except Friday)', '9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('22', '', 'Dr. Biplob Kumar Roy ', '', 'MBBS, MPH, MCPS ( Medicine ), MD ( Neurology )', 'Neurology', '', '', 'Popular', '5 pm – 9 pm (Only Saturday)', '01553341060-1, 01553341063, 9669480, 9661491-3')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('23', '', 'Professor Dr. Md. Anisur Rahman', '', 'MBBS, FCPS, Trained in Therapeutic Endoscopy (Japan)', 'Gastroenterology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('24', '', 'Professor Dr. Faruque Ahmed', '', 'MBBS, FCPS ( Med. ), MD ( Gastro. )', 'Gastroenterology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('25', '', 'Prof. (Dr.) M. T. Rahman', '', 'Prof. (Dr.) M. T. Rahman ', 'Gastroenterology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('26', '', 'Prof. (Dr.) Md. Habibur Rahman', '', 'MBBS, FCPS, MSc (Eng), FRCP (Edin)', 'Nephrology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('27', '', 'Prof. (Dr.) Shamim Ahmed', '', 'MBBS (DMC), FCPS (Med), FRCP (Edin), FRCP (Glassgow)', 'Nephrology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('28', '', 'Prof. (Dr.) Md. Farid Uddin', '', 'MBBS, DEM, MD', 'Endocrine Medicine', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('29', '', 'Dr. M. A. Sayem', '', 'MBBS, DLP ( Diabetology ) - BIRDEM, C.C', 'Diabetologist', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('30', '', 'Dr. Iqbal Ahmed', '', 'MBBS, CCD ( Birdem )', 'Diabetologist', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('31', '', 'Prof. (Dr.) T I M A Faruq', '', 'MBBS, FCPS ', 'General Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('32', '', 'Prof. (Dr). Syed Serajul Karim', '', 'MBBS, FCPS, FICS Thyroid, Breast, Endocrine, Laparoscopic Surgeon', 'General Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('33', '', 'Professor Dr. Muzibar Rahman', '', 'MBBS, FCPS, FICS, FACS, Fellow WHO (Uro) Australia, Advanced Training in Uro (UK)', 'General Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('34', '', 'Professor Dr. Omar Ali', '', 'MBBS, FCPS, FICS ( USA ), WHO Fellow in Laparoscopic & Endoscopic Surgery ( Thailand )', 'General Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('35', '', 'Professor Dr. Kanak Kanti Barua', '', 'MBBS, FCPS (Surgery), MS (Neuro Surgery) Ph.D, FICS', 'Neurosurgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('36', '', 'Dr. Saumitra Sarker', '', 'MBBS, MS (Neuro Surgery)', 'Neurosurgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('37', '', 'Prof. Dr. Shafquat Hussain Khundakar', '', 'MBBS, FCPS (Plastic Surgery)', 'Plastic Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('38', '', 'Dr. Hashim Rabby', '', 'MBBS, FCPS (Surgery), MRCS (Edin), MRCPS (Glassgow)', 'Liver, Biliary & Pancreatic Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('39', '', 'Prof. Dr. Shafquat Hussain Khundakar', '', 'MBBS, FCPS (Plastic Surgery)', 'Colorectal Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('40', '', 'Professor Dr. AKM Anwarul Islam', '', 'MBBS, FCPS, FRCS, FICS Clinical Fellow in Urology (WHO)', 'Urology Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('41', '', 'Dr. S. A. Khan', '', 'MBBS, FCPS, MS (Urology) Urologist & Transplant Surgeon', 'Urology Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('42', '', 'Professor Dr. Mirza M H Faisal', '', 'MBBS, FCPS, FRCS (Ed), FICS', 'Urology Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('43', '', 'Prof. (Dr.) SK. Nurul Alam', '', 'MBBS, MS (Ortho), D Ortho Fellow Orthopaedic Surgery (Singapore)', 'Orthopaedic Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('44', '', 'Prof. (Dr.) Moinuddin Ahmed Chowdhury', '', 'MBBS, MS (Orthopaedics), RCO (USA)', 'Orthopaedic Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('45', '', 'Dr. G M Reza', '', 'MBBS, MCPS (Surgery), D(Ortho), MS (Ortho), AAOS (USA)', 'Orthopaedic Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('46', '', 'Dr. Hafizur Rahman', '', 'FRCOG', 'Gynaecology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('47', '', 'Professor Dr. Farhat Hossain', '', 'MBBS, FCPS (Gynae) Fellow-Gynae Oncology (TATA Memorial Hospital, India)', 'Gynaecology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('48', '', 'Dr. Ferdousi Begum', '', 'BBS, DGO, FCPS', 'Gynaecology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('49', '', 'Prof. (Dr.) Sayeba Akhter', '', 'MBBS, FCPS (BD), FCPS (PAK), FICMCH (IN), DRH (UK)', 'Gynaecology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('50', '', 'Prof. (Dr.) Kohinoor Begum', '', 'MBBS, FCPS', 'Gynaecology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('51', '', 'Prof. (Dr.) Moinul Hossain', '', 'MBBS, FCPS', 'Pain Management', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('52', '', 'Professor Dr. M. A. Razzak', '', 'MBBS, DA ( DU ), MASA ( USA )', 'Pain Management', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('53', '', 'Dr. Rehana Begum', '', 'MBBS (Dhaka), LM, DGO (Ireland)', 'Breast Cancer Specialist', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('54', '', 'Professor Dr. Mohammad Hanif ', '', 'MBBS, FCPS, FRCP (Edin)', 'Child/Paediatrics', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('55', '', 'Professor Dr. Md. Monimul Hoque', '', 'MBBS, FCPS', 'Child/Paediatrics', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('56', '', 'Professor Dr. Md. Ruhul Amin', '', 'MBBS, FCPS (Paediatrics), Pediatric Pulmonology (Fellow - UK)', 'Child/Paediatrics', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('57', '', 'Professor Dr. M. A. Jaigirdar', '', 'MBBS, DCH, MRCP (UK)', 'Child/Paediatrics', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('58', '', 'Dr. Md. Faizul Islam', '', 'MBBS, ARAB Board ( FCPS ), DCH, RCPS ( Ire.)', 'Child/Paediatrics', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('59', '', 'Prof. (Dr.) Md. Selimuzzaman', '', 'MBBS, DCH, MD (Paediatrics) (DU)', 'Child/Paediatrics', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('60', '', 'Dr. Md. Abu Jafor', '', 'MBBS (Dhaka), MCPS (Surgery) FCPS (Surgery), MS (Ped. Surgery)', 'Paediatric Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('61', '', 'Professor Dr. M Mujibul Hoque', '', 'MBBS, FCPS, FRCP, DDV ( DU ), DDV ( Austria ) ', 'Skin/Dermatology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('62', '', 'Professor Dr. Kazi A. Karim', '', 'MBBS (Dhaka), DDV (Vien), MSSVD (London)', 'Skin/Dermatology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('63', '', 'Dr. Abida Sultana', '', 'MBBS, DDV, FCPS (Dermatology & Venareology)', 'Skin/Dermatology', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('64', '', 'Professor Dr.  (Major Retd.) Md. Ashraful Islam', '', 'MBBS, FCPS, FICS (USA) Fellow in Otology Harvard Medical School, USA', 'ENT, Head & Neck Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('65', '', 'Professor Dr. Mohammad Abdullah', '', 'MBBS, FCPS, FICS', 'ENT, Head & Neck Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('66', '', 'Dr. Md. Monjurul Alam', '', 'MBBS, FCPS, MS (ENT), FICS (USA), Microear Surgery (Bangkok, Mumbai, Malaysia) FESS & Plastic Surgery (Delhi, Chennai, Singapore)', 'ENT, Head & Neck Surgery', '', '', 'Popular', '', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('67', '', 'Prof. (Dr.) Jalal Ahmed', '', 'MBBS, FCPS, FICS', 'Eye/Ophthalmology', '', '', 'Popular', '5 pm – 8 pm (except Thursday & Friday)', '')");
        db.execSQL("INSERT INTO DOCTOR VALUES ('68', '', 'Prof. (Dr.) Md. Shamsul Haque', '', 'MBBS, FCPS', 'Eye/Ophthalmology', '', '', 'Popular', '6:30 pm – 9 pm (except Thursday & Friday)', '')");


        db.execSQL("DROP TABLE IF EXISTS AMBULANCE");

        db.execSQL("CREATE TABLE IF NOT EXISTS AMBULANCE (AmbulanceId INT, OrganizationName VARCHAR, PhoneNo VARCHAR, AmbulancePlateNo VARCHAR, Availability VARCHAR);");


        db.execSQL("INSERT INTO AMBULANCE VALUES ('1', 'Popular Medical College & Hospital', '', 'Dhaka Metro-Cha  53-2650', 'True')");
        db.execSQL("INSERT INTO AMBULANCE VALUES ('2', 'Popular Medical College & Hospital', '', 'Dhaka Metro-Cha  53-2651', 'True')");
        db.execSQL("INSERT INTO AMBULANCE VALUES ('3', 'Popular Medical College & Hospital', '', 'Dhaka Metro-Chha  71-0954', 'True')");
        db.execSQL("INSERT INTO AMBULANCE VALUES ('4', 'Popular Medical College & Hospital', '', 'Dhaka Metro-Chha  71-0955', 'True')");
        db.execSQL("INSERT INTO AMBULANCE VALUES ('5', 'Popular Diagnostic Centre LTD.', '', 'Dhaka Metro-Chha  71-0797', 'True')");


        db.execSQL("CREATE TABLE IF NOT EXISTS SYMPTOMS (DiseaseID INT,Disease VARCHAR,Symptoms VARCHAR);");
        db.execSQL("INSERT INTO SYMPTOMS VALUES ('1', 'Common Cold', 'Cough,Sneeze,Nausea,Dizzyness,catarrh')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('2', 'Fever', 'High temperature,Dizzyness,Temporary weakness,Loss of appetite')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('3', 'Viral Fever', 'Cough,Sneeze,Nausea,High temperature,Dizzyness,Temporary weakness,Loss of appetite,Quavering')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('4', 'Dysentary', 'Temporary weakness,Dehydration,Dizzyness,Loose motion,Frequent bowel movement')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('5', 'Sinusitis Problem', 'Headache,Nausea,Dizzyness,Problem in breathing,Sneeze')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('6', 'Fungal/Bacterial Infection', 'High temperature,Dizzyness,Temporary weakness,Rash,Allergic reactions')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('7', 'Cardiac Problems', 'Heartburn,Chest pain,Anxiety,Problem in breathing,Dizzyness')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('8', 'Gastric Problems', 'Lower abdominal pain,Rankle,Abdomen inflation,Wincing,Uneasy feeling')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('9', 'Respiratory Problems', 'Problem in breathing,Irregular breathing,Cough,Pain in sides of the chest,Dizzyness')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('10', 'Internal Injury', 'Severe pain,Coughing blood,Losing sense,Rash')");

        db.execSQL("INSERT INTO SYMPTOMS VALUES ('11', 'Kidney & Renal Problems', 'Blood with urine,Urinal infection,Red or dark brown urine')");


        db.close();

    }
}
