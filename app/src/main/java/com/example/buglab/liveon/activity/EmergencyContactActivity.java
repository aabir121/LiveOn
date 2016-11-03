package com.example.buglab.liveon.activity;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.buglab.liveon.R;
import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EmergencyContactActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FloatingActionButton sideBarToggle;
    EditText contact1,contact2,contact3,contact4,contact5;
    int state;
    ImageButton[] contactBtn=new ImageButton[5];
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_emergency_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sideBarToggle=(FloatingActionButton)findViewById(R.id.sideBarToggleBtn);
        sideBarToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        submitBtn=(Button)findViewById(R.id.emergencyContactSubmitBtn);


        contact1=(EditText)findViewById(R.id.contactText1);
        contact2=(EditText)findViewById(R.id.contactText2);
        contact3=(EditText)findViewById(R.id.contactText3);
        contact4=(EditText)findViewById(R.id.contactText4);
        contact5=(EditText)findViewById(R.id.contactText5);


        contactBtn[0]=(ImageButton)findViewById(R.id.contactButton1);
        contactBtn[1]=(ImageButton)findViewById(R.id.contactButton2);
        contactBtn[2]=(ImageButton)findViewById(R.id.contactButton3);
        contactBtn[3]=(ImageButton)findViewById(R.id.contactButton4);
        contactBtn[4]=(ImageButton)findViewById(R.id.contactButton5);




        SharedPreferences prefs = getSharedPreferences("Live_ON", MODE_PRIVATE);
        state = prefs.getInt("FirstUseContact", 0); //0 is the default value.
        Log.d("DATASAVED","state"+state);
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String c1="+880"+contact1.getText().toString();
                    String c2="+880"+contact2.getText().toString();
                    String c3="+880"+contact3.getText().toString();
                    String c4="+880"+contact4.getText().toString();
                    String c5="+880"+contact5.getText().toString();
                    if(!c1.isEmpty() && !c2.isEmpty() && !c3.isEmpty() && !c4.isEmpty() && !c5.isEmpty() && c1.length()==14 && c2.length()==14 && c3.length()==14 && c4.length()==14 && c5.length()==14)
                    {
                        SharedPreferences settings = getSharedPreferences("Live_ON", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putInt("FirstUseContact",1);

                        editor.putString("C1",contact1.getText().toString());
                        editor.putString("C2",contact2.getText().toString());
                        editor.putString("C3",contact3.getText().toString());
                        editor.putString("C4",contact4.getText().toString());
                        editor.putString("C5",contact5.getText().toString());
                        editor.commit();
                        Log.d("DATASAVE:","saved"+settings.getInt("FirstUseContact",0));
                        makeAlert("Confirmation","Are you sure to save these as your emergency contact?",0);
                    }
                    else
                    {
                        Log.d("DATASAVED","ERROR");

                        Toast.makeText(getApplicationContext(),"Input is not valid",Toast.LENGTH_LONG);
                        if(c1.isEmpty())
                        {
                            contact1.setError("This field cannot be left blank.");
                        }
                        else if(c1.length()<14  || c1.length()>14)
                        {
                            contact1.setError("The mobile number must be 11 characters long.");
                        }
                        if(c2.isEmpty())
                        {
                            contact2.setError("This field cannot be left blank.");
                        }
                        else if(c2.length()<14  || c2.length()>14)
                        {
                            contact2.setError("The mobile number must be 11 characters long.");
                        }
                        if(c3.isEmpty())
                        {
                            contact1.setError("This field cannot be left blank.");
                        }
                        else if(c3.length()<14  || c3.length()>14)
                        {
                            contact3.setError("The mobile number must be 11 characters long.");
                        }
                        if(c4.isEmpty())
                        {
                            contact4.setError("This field cannot be left blank.");
                        }
                        else if(c4.length()<14  || c4.length()>14)
                        {
                            contact4.setError("The mobile number must be 11 characters long.");
                        }
                        if(c5.isEmpty())
                        {
                            contact5.setError("This field cannot be left blank.");
                        }
                        else if(c5.length()<14  || c5.length()>14)
                        {
                            contact5.setError("The mobile number must be 11 characters long.");
                        }
                    }

                }
            });

        if(state!=0)
        {
            SharedPreferences prefs2 = getSharedPreferences("Live_ON", MODE_PRIVATE);
            contact1.setText(prefs2.getString("C1",""));
            contact2.setText(prefs2.getString("C2",""));
            contact3.setText(prefs2.getString("C3",""));
            contact4.setText(prefs2.getString("C4",""));
            contact5.setText(prefs2.getString("C5",""));
        }


        for(int i=0;i<contactBtn.length;i++)
        {
            final int finalI = i;
            contactBtn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(pickContact, finalI +1);
                }
            });
        }

        View header = navigationView.getHeaderView(0);
        ImageView headerLogo=(ImageView)header.findViewById(R.id.navHeaderLogoBtn);
        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EmergencyContactActivity.this,MainMenuActivity.class);
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
            Intent intent=new Intent(this,MainMenuActivity.class);
            startActivity(intent);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.emergency_contact, menu);
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
        else if (id == R.id.nav_emergency_search) {
            frwardToEmergencySearch(EmergencyContactActivity.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case (1):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();
                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    number=formatNumber(number);
                    contact1.setText(number);
                    break;
                }
            case (2):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();
                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    number=formatNumber(number);
                    contact2.setText(number);
                    break;
                }
            case (3):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();
                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    number=formatNumber(number);
                    contact3.setText(number);
                    break;
                }
            case (4):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();
                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    number=formatNumber(number);
                    contact4.setText(number);
                    break;
                }
            case (5):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactUri = data.getData();
                    String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                    Cursor cursor = getContentResolver()
                            .query(contactUri, projection, null, null, null);
                    cursor.moveToFirst();
                    // Retrieve the phone number from the NUMBER column
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(column);
                    number=formatNumber(number);
                    contact5.setText(number);
                    break;
                }
        }
    }

    public String formatNumber(String number)
    {
        if(number.contains("-"))
        {
            number=number.replace("-","");
            Log.d("PHONENUMBER: ","-"+number);
        }
        if(number.contains(" "))
        {
            number=number.replace(" ","");
            Log.d("PHONENUMBER: ","-"+number);
        }

        if(number.contains("+880"))
        {
            number=number.replace("+880","");
            Log.d("PHONENUMBER: ","+880"+number);
        }
        if(number.startsWith("0"))
        {
            number=number.substring(1,number.length());
            Log.d("PHONENUMBER: ","0start"+number);
        }

        return number;
    }

    public void makeAlert(String title, String message, final int state)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .create();

//                alertDialog.setTitle("Location Selected");
//
//                alertDialog.setMessage("Add this Location to your");
//                AlertDialog alertDialog=new AlertDialog.Builder(getApplicationContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(state==0)
                {
                    makeAlert("Notify Contacts","Do you want to notify your emergency contacts?",1);
                }
                else if(state==1)
                {
                    String c1="+880"+contact1.getText();
                    String c2="+880"+contact2.getText();
                    String c3="+880"+contact3.getText();
                    String c4="+880"+contact4.getText();
                    String c5="+880"+contact5.getText();

                    sendSMS(c1,"You have been chosen as one of my Emergency Contacts.");
                    sendSMS(c2,"You have been chosen as one of my Emergency Contacts.");
                    sendSMS(c3,"You have been chosen as one of my Emergency Contacts.");
                    sendSMS(c4,"You have been chosen as one of my Emergency Contacts.");
                    sendSMS(c5,"You have been chosen as one of my Emergency Contacts.");

                    Intent intent=new Intent(EmergencyContactActivity.this,MainMenuActivity.class);
                    startActivity(intent);
                }

            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(state==1)
                {
                    Intent intent=new Intent(EmergencyContactActivity.this,MainMenuActivity.class);
                    startActivity(intent);
                }

            }
        });
        alertDialog.setMessage(message);
        alertDialog.show();
    }

    private void sendSMS(String phoneNumber, String message)
    {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
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
    public void frwardToEmergencySearch(Context context)
    {
        if(haveNetworkConnection()){
            Intent intent=new Intent(context,EmergencySearchActivity.class);
            startActivity(intent);
        }
        else
        {
            makeAlert("Network Connection Needed","This service needs an active internet connection to work.\n Do you want to activate your internet connection?");
        }
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
    public void makeAlert(String title, String message)
    {
        final AlertDialog alertDialog = new AlertDialog.Builder(this)
                .create();

//                alertDialog.setTitle("Location Selected");
//
//                alertDialog.setMessage("Add this Location to your");
//                AlertDialog alertDialog=new AlertDialog.Builder(getApplicationContext()).create();
        alertDialog.setTitle(title);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Wi-fi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                wifi.setWifiEnabled(true);
                Toast.makeText(getApplicationContext(),"Wi-fi turned On",Toast.LENGTH_LONG).show();
                frwardToEmergencySearch(getApplicationContext());
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
//        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Mobile Data", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                try {
//                    setMobileDataEnabled(MainMenuActivity.this,true);
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                } catch (NoSuchMethodException e) {
//                    e.printStackTrace();
//                } catch (InvocationTargetException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        alertDialog.setMessage(message);
        alertDialog.show();
    }

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

}
