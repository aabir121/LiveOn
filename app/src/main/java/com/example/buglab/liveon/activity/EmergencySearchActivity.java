package com.example.buglab.liveon.activity;

import com.example.buglab.liveon.R;
import com.example.buglab.liveon.utility.Place;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.akexorcist.googledirection.model.Info;
import com.example.buglab.liveon.utility.PlaceDetailsBackground;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class EmergencySearchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMarkerDragListener, LocationListener {



    public static final int MY_PERMISSIONS_ACCESS_FINE_LOCATION=1;
    public static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION=2;


    public Criteria criteria;
    public String bestProvider;
    LocationManager locationmanager;
    static float windowHeight;
    static float windowWidth;
    Context context = this;
    Button hospitalDetailsBtn;
    String placeListString;
    String hospitalPhone = "";
    String hospitalID = "";
    String hospitalAdrs = "";

    ArrayList<ArrayList<String>> fullPlaceDetails;
    //location
    private GoogleMap mMap;
    MarkerOptions myPositionMarker;
    MarkerOptions markerHere;

    FloatingActionButton navDrawerToggle;
    //PlaceListView
    RelativeLayout listLayout;
    ListView placeListView;
    boolean mapReadyState = false;
    boolean sorted = false;

    //PlaceSearchLayout
    ArrayList<ArrayList<String>> finallist;
    LinearLayout placeTypeLayout;
    View mainView;

    private AutoCompleteTextView mAutocompleteViewSource;
    private static final LatLngBounds BOUNDS_BANGLADESH = new LatLngBounds(
            new LatLng(23.549686, 90.056174), new LatLng(24.018418, 90.514853));

    public static String placePhoneNumber;
    SeekBar seekBar;
    EditText distanceText;
    String alerttext;
    //    NumberPicker numberPicker;
    List<Map<String, String>> items;
    Button testButtonPlace;
    String placeType = null;
    //for async task
    private static final String APP_ID = "AIzaSyBc-g4Wl0D6RP-rLKaVEvwhsC19L7EkWLw";
    int pos = 0;
    ProgressDialog progressDialog;
    Double lat, lon;
    LatLng positionLatlng;
    ArrayList<Place> arrayList = new ArrayList<Place>();
    StringBuilder placeList = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_emergency_search);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait.....");
//        progressDialog.show();


        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        listLayout = (RelativeLayout) findViewById(R.id.mapLayout);
        placeListView = (ListView) findViewById(R.id.mapPlaceList);
        navDrawerToggle = (FloatingActionButton) findViewById(R.id.sideBarToggleBtn);
        hospitalDetailsBtn = (Button) findViewById(R.id.hospitalDetailBtn);

        listLayout.getLayoutParams().height = 0;


        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
        navDrawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton navDrawerToggle;

        Display display = getWindowManager().getDefaultDisplay();
        windowWidth = display.getWidth();
        windowHeight = display.getHeight();
        listLayout.getLayoutParams().width = (int) (windowWidth - windowWidth / 4);

        mainView = getCurrentFocus();
        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria cri = new Criteria();
        String provider = locationmanager.getBestProvider(cri, false);
        if (provider != null & !provider.equals(""))
        {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling

                ActivityCompat.requestPermissions(EmergencySearchActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_ACCESS_FINE_LOCATION
                );
                ActivityCompat.requestPermissions(EmergencySearchActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},MY_PERMISSIONS_ACCESS_COARSE_LOCATION
                );
                return;
            }
            else
            {
                Location location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                Log.d("LOCATION","asd"+location);
                locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,1,this);
                if(location!=null)
                {
                    Log.d("LOCATIONUPDATE",provider);
                    onLocationChanged(location);
                }
                else{
                    Log.d("LOCATIONUPDATE",provider);
                    location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,1,this);
                    progressDialog.setMessage("Getting your location. This may take a few minutes to accurately pinpoint your location only the first time.");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                }
            }
        }

        else

        {
            Toast.makeText(getApplicationContext(),"Provider is null",Toast.LENGTH_LONG).show();
        }

//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, 0 /* clientId */, this)
//                .addApi(Places.GEO_DATA_API)
//                .build();
//        new SelectedPlaceFindBackgroundInner(progressDialog).execute(""+lat,""+lon,txt.getText().toString(),distanceText.getText().toString());
//                txt.setText(arrayList.get(0).toString());




        placeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info=getDetailsData(position);
                makeAlertBox(position,info);
            }
        });

        if(lat!=null && lon!=null)
        {
            startSearch();
        }

        final int[] stateDetails = {0};
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        hospitalDetailsBtn.setAnimation(animation);

        hospitalDetailsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(stateDetails[0] ==0){
                    if(!sorted)
                        sortHospital();
                    makePlaceList(getCurrentFocus());
                    expand(listLayout);
                    listLayout.setVisibility(View.VISIBLE);
                    hospitalDetailsBtn.setAnimation(null);
                    stateDetails[0] =1;
                }
                else if(stateDetails[0] ==1)
                {
                    collapse(listLayout);
//                    listLayout.setVisibility(View.INVISIBLE);
                    hospitalDetailsBtn.setAnimation(animation);
                    stateDetails[0]=0;
                }
            }
        });
        View header = navigationView.getHeaderView(0);
        ImageView headerLogo=(ImageView)header.findViewById(R.id.navHeaderLogoBtn);
        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EmergencySearchActivity.this,MainMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(listLayout.getVisibility()==View.VISIBLE)
        {
            listLayout.getLayoutParams().height=0;

            testButtonPlace.setVisibility(View.VISIBLE);
            listLayout.setVisibility(View.INVISIBLE);
        }

        else if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent=new Intent(EmergencySearchActivity.this,MainMenuActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main_menu_drawer, menu);
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


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.setPadding(0,0,20,240);


//        progressDialog.show();

        locationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria cri = new Criteria();

        String provider = locationmanager.getBestProvider(cri, false);

        if (provider != null & !provider.equals(""))

        {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = locationmanager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,500,1,this);

            if(location!=null)

            {

                onLocationChanged(location);
                LatLng latLng=new LatLng(lat,lon);
//                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
//
//                List<Address> addresses = null;
//                try {
//                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                myPositionMarker = new MarkerOptions().position(
                        latLng).title("You are here");

                myPositionMarker.icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(latLng).zoom(16).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition2));
//
                mMap.addMarker(myPositionMarker);

            }

            else{

//                Toast.makeText(getApplicationContext(),"location not found",Toast.LENGTH_LONG ).show();

            }

        }

        else

        {

            Toast.makeText(getApplicationContext(),"Provider is null",Toast.LENGTH_LONG).show();

        }
        mapReadyState=true;
//        if(progressDialog.isShowing())
//            progressDialog.dismiss();
        mMap.setOnMarkerClickListener(this);

    }

    @Override
    public void onMapLongClick(LatLng latLng) {



    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng markerLatLng=marker.getPosition();
        Double markerLat=markerLatLng.latitude;
        Double markerLng=markerLatLng.longitude;

        for(int i=0;i<finallist.size();i++)
        {
            if(markerLat==Double.parseDouble(finallist.get(i).get(1)) && markerLng==Double.parseDouble(finallist.get(i).get(2)))
            {
                String info=getDetailsData(i);
                makeAlertBox(i,info);
//                Log.d("markerclicked",fullPlaceDetails.get(i).get(0));
//                Log.d("markerclicked",fullPlaceDetails.get(i).get(6));
            }

        }

        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lat=location.getLatitude();
        lon=location.getLongitude();
        if(progressDialog.isShowing())
        {
            mapUpdateNew();

            progressDialog.dismiss();
            final Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                progressDialog.show();
                    if(mapReadyState)
                    {
                        try{
                            onMapUpdate(mMap);
                        }catch(Exception e)
                        {
                            Log.d("MAPEXCEPTION: ",e.toString());
                        }
                    }
                }
            },2500);
        }
        Log.d("LOCATION",""+lat+","+lon);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    class SelectedPlaceFindBackgroundInner extends AsyncTask<String, Void, String> {
        //        private TextView textView;
        private LatLng latLng;
        int pos=0;
        Geocoder geocoder;

        ProgressDialog progressDialog;
        private static final String APP_ID = "AIzaSyBc-g4Wl0D6RP-rLKaVEvwhsC19L7EkWLw";
        Info durationinfo,distanceinfo;

        String jsonData=null;
        public SelectedPlaceFindBackgroundInner(ProgressDialog progressDialog) {
//            this.textView = textView;
//        this.latLng=latLng;
//            this.progressDialog=progressDialog;
        }

        @Override
        protected String doInBackground(String... strings) {
//        jsonData=getUrlContents(strings[0]);
            Double lat=Double.parseDouble(strings[0]);
            Double lon=Double.parseDouble(strings[1]);
            Double distance=Double.parseDouble(strings[3]);
            return findPlaces(lat,lon,strings[2],distance);

        }
        protected String getJSON(String url) {
            return getUrlContents(url);
        }

        public String findPlaces(double latitude, double longitude, String placeSpacification,Double distance2)
        {
            StringBuilder content = new StringBuilder();
            String urlString = makeUrl(latitude, longitude,placeSpacification,distance2);

            String json = getJSON(urlString);


            JSONObject object = null;
            try {
                object = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray array = null;
            try {
                array = object.getJSONArray("results");


                LatLng origin=new LatLng(latitude,longitude);
                arrayList.clear();
                for (int i = 0; i < array.length(); i++) {
                    try {

                        JSONObject main=(JSONObject)array.get(i);


                        JSONObject geometry=(JSONObject)main.get("geometry");


                        JSONObject location=(JSONObject)geometry.get("location");
                        Double latitude2=location.getDouble("lat");
                        Double longitude2=location.getDouble("lng");

                        String placeName=main.getString("name");
                        String icon=main.getString("icon");
                        String place_id=main.getString("place_id");

                        Log.d("insidefindplaces", "name: "+placeName+"\nID: "+place_id);

                        Log.d("insidefindplaces",""+latitude2+longitude2);
                        alerttext=String.valueOf(location);


                        Place place = new Place();
                        place.setLatitude(latitude2);
                        place.setLongitude(longitude2);
                        place.setName(placeName);
                        place.setId(place_id);

//                        String placeNumber=t.getInfo();
                        Log.v("Places Services ", ""+place);
//                        Log.d("insideInfo",""+placeNumber);

                        content.append(""+placeName+"\n");
                        arrayList.add(place);
                    } catch (Exception e) {
                        Log.d("insideerror: ",e.toString());
                    }
                }

                double min=1000;

                for(int i=0;i<arrayList.size();i++)
                {
                    LatLng destination=new LatLng(arrayList.get(i).getLatitude(),arrayList.get(i).getLongitude());

                    Location frst=new Location("");
                    frst.setLatitude(latitude);
                    frst.setLongitude(longitude);

                    Location last=new Location("");
                    last.setLatitude(arrayList.get(i).getLatitude());
                    last.setLongitude(arrayList.get(i).getLongitude());

                    double distance=frst.distanceTo(last);
                    if(distance<min)
                    {min=distance;
                        pos=i;}
                }

                placeList.delete(0,placeList.length());
//            textView.setText("");
                for(int i=0;i<arrayList.size();i++)
                {
                    placeList.append(arrayList.get(i).getName()+",");
                    placeList.append(arrayList.get(i).getLatitude()+",");
                    placeList.append(arrayList.get(i).getLongitude()+",");
                    placeList.append(arrayList.get(i).getId()+";");
//                textView.setText(txt.getText().toString().concat(arrayList.get(i).getName()+","+arrayList.get(i).getLatitude()+","+arrayList.get(i).getLongitude()+"\n"));
                }



//        content.append("Distance: "+min+"\n\n");
//            String resultCoordinates=arrayList.get(pos).getLatitude()+","+arrayList.get(pos).getLongitude();
                return placeList.toString();

            } catch (JSONException e) {
                return null;
            }

        }





        private String getUrlContents(String theUrl)
        {
            String weather = theUrl;
            try {
                URL url = new URL(theUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();
                StringBuilder content = new StringBuilder();

                String inputString;
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    content.append(line + "\n");
                }


//            while ((inputString = bufferedReader.readLine()) != null) {
//                builder.append(inputString);
//            }
                weather=content.toString();

                urlConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = ProgressDialog.show(context, "Test mail sturen", "wachten a.u.b...");
            progressDialog = ProgressDialog.show(context,
                    "Please Wait...",
                    "Getting data",
                    true, false);
            progressDialog.setCancelable(false);
            Log.d("ProgressDialogcheck",progressDialog.toString());
//            progressDialog.setMessage("Getting data.....");
//            progressDialog.show();
        }

        @Override
        protected void onPostExecute(String temp) {

//        latLng=new LatLng(arrayList.get(pos).getLatitude(),arrayList.get(pos).getLongitude());
//            StringBuilder placeList=new StringBuilder();
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            placeListString=temp;
            Log.d("ProgressDialogCheck",placeListString);

            if(placeListString.equals(""))
            {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(EmergencySearchActivity.this, R.style.AppTheme));
                alertDialog.setTitle("No Place Found!");
                alertDialog.setMessage(alerttext);
                alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // continue with delete
                    }
                });
                alertDialog.show();

                Toast.makeText(getApplicationContext(),"Sorry! No such place found in the given region.",Toast.LENGTH_LONG);
            }

            else{
                Log.d("ProgressDialogCheck",placeListString);
                finallist=new ArrayList<>();

                String[] places=placeListString.split(";");
//        txt.setText(places[4]);
                String[] separatePlaces=null;
                for(int i=0;i<places.length;i++)
                {
                    ArrayList<String> l=new ArrayList<String>();

                    separatePlaces=places[i].split(",");
                    l.add(separatePlaces[0]);
                    l.add(separatePlaces[1]);
                    l.add(separatePlaces[2]);
                    l.add(separatePlaces[3]);
//            txt.setText(separatePlaces[2]);
                    finallist.add(l);
                }


                String typeIcon="pin_hospital";

                Double disVal=2000.00;
                mMap.clear();
                mMap.addCircle(new CircleOptions()
                        .center(new LatLng(lat, lon))
                        .radius(disVal)
                        .strokeColor(0x80D2D6FF)
                        .fillColor(0x60D2D6FF));
//                mMap.addMarker(markerHere);
                mMap.addMarker(myPositionMarker);

                Log.d("Error return","asdasd");
                Resources res = getResources();
                final int resID = res.getIdentifier(typeIcon , "drawable", getPackageName());
                Drawable resDraw = res.getDrawable(resID );

                for(int i=0;i<finallist.size();i++)
                {
                    Double lat=Double.parseDouble(finallist.get(i).get(1));
                    Double lon=Double.parseDouble(finallist.get(i).get(2));
                    LatLng latLng=new LatLng(lat,lon);
                    MarkerOptions markerBus2 = new MarkerOptions().position(
                            latLng).title(finallist.get(i).get(0));

                    markerBus2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));

                    mMap.addMarker(markerBus2);
                }
                CameraPosition cameraPosition2 = new CameraPosition.Builder()
                        .target(new LatLng(lat,lon)).zoom(14).build();

                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition2));




            }


        }



        private String makeUrl(double latitude, double longitude,String place,Double distance) {
            StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        StringBuilder urlString = new StringBuilder("https://www.google.com");

            if (place.equals("")) {
                urlString.append("location=");
                urlString.append(Double.toString(latitude));
                urlString.append(",");
                urlString.append(Double.toString(longitude));
                urlString.append("&radius="+distance);
                //   urlString.append("&types="+place);
                urlString.append("&key=" + APP_ID+"&sensor=true");
            } else {
                urlString.append("location=");
                urlString.append(Double.toString(latitude));
                urlString.append(",");
                urlString.append(Double.toString(longitude));
                urlString.append("&radius="+distance);
                urlString.append("&types="+place);
                urlString.append("&key=" + APP_ID+"&sensor=true");
            }

            return urlString.toString();
        }
    }


    public void onMapUpdate(final GoogleMap mMap) {

        String placeNumber=null;
        fullPlaceDetails=new ArrayList<>();
        Log.d("ProgressDialogcheck",progressDialog.toString());

        SelectedPlaceFindBackgroundInner task=new SelectedPlaceFindBackgroundInner(progressDialog);
        task.execute(""+lat,""+lon,"hospital","2000");
        Log.d("codehere",lat+""+lon);
//        String placeListString=null;
//        try {
//            placeListString=task.get().toString();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

        Toast.makeText(getApplicationContext(),placeListString,Toast.LENGTH_LONG);



    }

    public void makePlaceList(View v)
    {

        final ArrayList<String> listarr = new ArrayList<String>();
        for (int i = 0; i <finallist.size(); i++) {
            listarr.add(finallist.get(i).get(0));
        }

        Log.d("fullPlace",""+fullPlaceDetails.size());
        final ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.list_view_style_text, listarr);
        placeListView.setAdapter(adapter);
        listLayout.setGravity(Gravity.LEFT|Gravity.BOTTOM);

        Display display = getWindowManager().getDefaultDisplay();
        float width = display.getWidth();
        float height=display.getHeight();
        listLayout.getLayoutParams().width=(int)width;
        listLayout.getLayoutParams().height=(int)height/2;
    }

    public void makeAlertBox(final int i,String fullInfo)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .create();

        alertDialog.setTitle(finallist.get(i).get(0));
        StringBuilder message=new StringBuilder("");
        message.append(fullInfo);
//        message.append("\nContact Number: "+fullPlaceDetails.get(i).get(6));
//        message.append("\nDistance from my position: "+fullPlaceDetails.get(i).get(5)+"meters");
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "Get Directions", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                LatLng myLatLng=new LatLng(lat,lon);

                Double hospitalLat=Double.parseDouble(finallist.get(i).get(1));
                Double hospitalLng=Double.parseDouble(finallist.get(i).get(2));
                LatLng hospitalLatlng=new LatLng(hospitalLat,hospitalLng);

                EmergencyDirection.myLatLng=myLatLng;
                EmergencyDirection.hospitalLatLng=hospitalLatlng;
                EmergencyDirection.hospitalName=finallist.get(i).get(0);
                EmergencyDirection.hospitalPhone=hospitalPhone;
                EmergencyDirection.hospitalAdrs=hospitalAdrs;
                EmergencyDirection.hospitalID=finallist.get(i).get(3);



                Intent intent=new Intent(EmergencySearchActivity.this,EmergencyDirection.class);
                startActivity(intent);
            }
        });
        alertDialog.setMessage(message);
        alertDialog.show();

    }

    class PlaceDetailsCallBackground extends AsyncTask
    {

        @Override
        protected Object doInBackground(Object[] params) {
            return null;
        }
    }

    public String getDetailsData(int i)
    {
        String placeNumber;
        StringBuilder fullInfo=new StringBuilder("");
        String placeID = finallist.get(i).get(3);
        PlaceDetailsBackground t = new PlaceDetailsBackground(progressDialog, lat, lon, placePhoneNumber);
        try {
            placeNumber = t.execute(placeID).get();
//                    Log.d("FULLINF",placeNumber);
            String[] demoInfo = placeNumber.split(":");
            Log.d("FULLINF", demoInfo[0]);
            Log.d("FULLINF", demoInfo[1]);
            Log.d("FULLINF", demoInfo[2]);


            ArrayList<String> l2 = new ArrayList<String>();
            l2.add(finallist.get(i).get(0));
            l2.add(finallist.get(i).get(1));
            l2.add(finallist.get(i).get(2));
            l2.add(finallist.get(i).get(3));
            l2.add(demoInfo[0]);
            l2.add(demoInfo[1]);
            l2.add(demoInfo[2]);
//            txt.setText(separatePlaces[2]);
            fullPlaceDetails.add(l2);

            hospitalAdrs=demoInfo[0];
            hospitalPhone=demoInfo[2];
            fullInfo.append(demoInfo[0]);
            fullInfo.append("\nContact No: "+demoInfo[2]);
            fullInfo.append("\nDistance: "+Float.parseFloat(demoInfo[1])/1000+" KM");

        } catch (InterruptedException e) {
            Log.d("Error return", e.toString());
        } catch (ExecutionException e) {
            Log.d("Error return", e.toString());
        } catch (Exception e) {
            Log.d("ERRORR", e.toString());

        }

        return fullInfo.toString();
    }

    public static void expand(final View v) {
        v.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final int targetHeight = (int)windowHeight/2;

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
                v.getLayoutParams().width=(int)(windowWidth-windowWidth/4);
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
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.getLayoutParams().width=(int)(windowWidth-windowWidth/4);
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

    public void sortHospital()
    {
        Collections.sort(finallist, new Comparator<ArrayList<String>>() {
            @Override
            public int compare(ArrayList<String> hospital1, ArrayList<String> hospital2) {
                float dis1=getDistanceFromMyPlace(Double.parseDouble(hospital1.get(1)),Double.parseDouble(hospital1.get(2)));
                float dis2=getDistanceFromMyPlace(Double.parseDouble(hospital2.get(1)),Double.parseDouble(hospital2.get(2)));
                return (int) (dis1-dis2);
            }
        });

        for(int i=0;i<finallist.size();i++)
        {
            Log.d("SORTED",""+finallist.get(i).get(0)+"\t");
            Log.d("SORTED",""+getDistanceFromMyPlace(Double.parseDouble(finallist.get(i).get(1)),Double.parseDouble(finallist.get(i).get(2)))+"\n");
        }
    }

    public float getDistanceFromMyPlace(Double destLat,Double destLng)
    {
        Location myLoc=new Location("");
        myLoc.setLatitude(lat);
        myLoc.setLongitude(lon);

        Location placeLoc=new Location("");
        placeLoc.setLatitude(destLat);
        placeLoc.setLongitude(destLng);

        return myLoc.distanceTo(placeLoc);
    }

    public void mapUpdateNew()
    {
        LatLng latLng=new LatLng(lat,lon);
        myPositionMarker = new MarkerOptions().position(
                latLng).title("You are here");

        myPositionMarker.icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//
        CameraPosition cameraPosition2 = new CameraPosition.Builder()
                .target(latLng).zoom(16).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition2));
//
        mMap.addMarker(myPositionMarker);
    }

    public void startSearch()
    {
        final Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                progressDialog.show();
                if(mapReadyState)
                {
                    try{
                        onMapUpdate(mMap);
                    }catch(Exception e)
                    {
                        Log.d("MAPEXCEPTION: ",e.toString());
                    }
                }
            }
        },2500);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    super.onBackPressed();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
