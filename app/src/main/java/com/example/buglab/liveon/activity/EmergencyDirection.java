package com.example.buglab.liveon.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Info;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Step;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.example.buglab.liveon.R;
import com.example.buglab.liveon.utility.EmergencySearchForwarding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EmergencyDirection extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback {
    Geocoder geocoder;
    List<Step> step;
    ArrayList<LatLng> sectionList;
    GoogleMap mMap;
    String serverkey="AIzaSyBc-g4Wl0D6RP-rLKaVEvwhsC19L7EkWLw";
    public static LatLng myLatLng;
    private String[] colors = {"#7fff7272", "#7f31c7c5", "#7fff8a00"};
    public static LatLng hospitalLatLng;


    //HospitalInfo
    public static String hospitalName;
    public StringBuilder hospitalInfo=new StringBuilder("");
    public static String hospitalID;
    public static String hospitalPhone;
    public static String hospitalAdrs;
    TextView titleText;
    TextView infoText;
    ImageButton hospitalImage;

    FloatingActionButton busBtn,walkBtn;



    //Zoom the hospital image
    private Animator zoomAnimator;
    private int mShortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_emergency_direction);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!hospitalPhone.equals("N/A"))
                {
                    Uri number = Uri.parse("tel:"+hospitalPhone);
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                    startActivity(callIntent);
                }
                else
                    Toast.makeText(getApplicationContext(),"No phone number available",Toast.LENGTH_LONG).show();

            }
        });



        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton drawerToggle=(FloatingActionButton)findViewById(R.id.sideBarToggleBtn);
        drawerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
            }
        });

        titleText=(TextView)findViewById(R.id.titleText);
        infoText=(TextView)findViewById(R.id.infoText);
        hospitalImage=(ImageButton)findViewById(R.id.hospitalImage);
        walkBtn=(FloatingActionButton)findViewById(R.id.walkBtn);
        busBtn=(FloatingActionButton)findViewById(R.id.busBtn);

        titleText.setText(hospitalName);
        Log.d("HOSPITAL",hospitalAdrs);
        hospitalInfo.append(hospitalAdrs);
        hospitalInfo.append("\nContact NO: "+hospitalPhone);
        infoText.setText(hospitalInfo.toString());

        mShortAnimationDuration=getResources().getInteger(android.R.integer.config_shortAnimTime);

        //https://maps.googleapis.com/maps/api/streetview?size=600x300&location=46.414382,10.013988&heading=151.78&pitch=-0.76&key=YOUR_API_KEY
        StringBuilder url=new StringBuilder("https://maps.googleapis.com/maps/api/streetview?size=600x600&location=");
        url.append(hospitalLatLng.latitude+","+hospitalLatLng.longitude+"&");
        url.append("fov=90&key="+serverkey);
        new DownloadImageTask((ImageView) findViewById(R.id.hospitalImage))
                .execute(url.toString());


        hospitalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(hospitalImage);
            }
        });

        busBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleDirection.withServerKey(serverkey)
                        .from(myLatLng)
                        .to(hospitalLatLng)
                        .transportMode(TransportMode.DRIVING)
                        .alternativeRoute(true)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(final Direction direction, String rawBody) {
                                // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                if (direction.isOK()) {
                                    mMap.clear();
                                    addMarkers();
                                    Log.d("clicked","bus button");
                                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    Route route = direction.getRouteList().get(0);
                                    String color = colors[0 % colors.length];
                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                    mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.parseColor(color)));

                                    Info distanceinfo=direction.getRouteList().get(0).getLegList().get(0).getDistance();
                                    Info durationinfo=direction.getRouteList().get(0).getLegList().get(0).getDuration();

                                    sectionList = direction.getRouteList().get(0).getLegList().get(0).getSectionPoint();
                                    for (LatLng position : sectionList) {

                                        MarkerOptions marker5 = new MarkerOptions().position(
                                                position);

                                        marker5.icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                        mMap.addMarker(marker5);

                                    }
                                    step = direction.getRouteList().get(0).getLegList().get(0).getStepList();

                                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                    for (PolylineOptions polylineOption : polylineOptionList) {
                                        mMap.addPolyline(polylineOption);
                                    }
                                }
                                busBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorTealTransparentPressed));
                                walkBtn.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,255,255)));

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    busBtn.setForegroundTintList(ColorStateList.valueOf(Color.rgb(255,255,255)));
                                    walkBtn.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(0,0,0)));
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something here
                            }
                        });

            }
        });

        walkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoogleDirection.withServerKey(serverkey)
                        .from(myLatLng)
                        .to(hospitalLatLng)
                        .transportMode(TransportMode.WALKING)
                        .alternativeRoute(true)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(final Direction direction, String rawBody) {
                                // Do something here
//                        Snackbar.make(mMap, "Success with status : " + direction.getStatus(), Snackbar.LENGTH_SHORT).show();
                                if (direction.isOK()) {
                                    mMap.clear();
                                    addMarkers();
                                    Log.d("clicked","walk button");
                                    busBtn.setBackgroundColor(Color.rgb(255, 255, 25));
                                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                    Route route = direction.getRouteList().get(0);
                                    String color = colors[0 % colors.length];
                                    ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                                    mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 10, Color.BLUE));

                                    Info distanceinfo=direction.getRouteList().get(0).getLegList().get(0).getDistance();
                                    Info durationinfo=direction.getRouteList().get(0).getLegList().get(0).getDuration();

                                    sectionList = direction.getRouteList().get(0).getLegList().get(0).getSectionPoint();
                                    for (LatLng position : sectionList) {

                                        MarkerOptions marker5 = new MarkerOptions().position(
                                                position);

                                        marker5.icon(BitmapDescriptorFactory
                                                .defaultMarker(BitmapDescriptorFactory.HUE_CYAN));

                                        mMap.addMarker(marker5);

                                    }
                                    step = direction.getRouteList().get(0).getLegList().get(0).getStepList();

                                    ArrayList<PolylineOptions> polylineOptionList = DirectionConverter.createTransitPolyline(getApplicationContext(), step, 5, Color.RED, 5, Color.BLUE);
                                    for (PolylineOptions polylineOption : polylineOptionList) {
                                        mMap.addPolyline(polylineOption);
                                    }
                                    walkBtn.setBackgroundTintList(getResources().getColorStateList(R.color.colorTealTransparentPressed));
                                    busBtn.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,255,255)));

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        walkBtn.setForegroundTintList(ColorStateList.valueOf(Color.rgb(255,255,255)));
                                        busBtn.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(0,0,0)));
                                    }

//                                    walkBtn.setBackgroundColor(Color.rgb(211, 211, 211));
                                }
                            }

                            @Override
                            public void onDirectionFailure(Throwable t) {
                                // Do something here
                                Toast.makeText(getApplicationContext(),"Please check your internet connection.",Toast.LENGTH_LONG);
                            }
                        });

            }
        });
        busBtn.performClick();

        View header = navigationView.getHeaderView(0);
        ImageView headerLogo=(ImageView)header.findViewById(R.id.navHeaderLogoBtn);
        headerLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(EmergencyDirection.this,MainMenuActivity.class);
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
        getMenuInflater().inflate(R.menu.emergency_direction, menu);
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
            frwardToEmergencySearch(EmergencyDirection.this);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//             TODO: Consider calling
            return;
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
//        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.setTrafficEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);

        mMap.setPadding(0,0,50,0);



    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void addMarkers()
    {
        LatLng latLngSource = myLatLng;

        MarkerOptions marker = new MarkerOptions().position(
                latLngSource).snippet("Source");

        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_person_pin));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLngSource).zoom(16).build();

        mMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        mMap.addMarker(marker);




        LatLng latLngDest = hospitalLatLng;

        MarkerOptions marker2 = new MarkerOptions().position(
                latLngDest).snippet("Destination");

        marker2.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_hospital));


        mMap.addMarker(marker2);

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


    private void zoomImageFromThumb(final View thumbView) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (zoomAnimator != null) {
            zoomAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expandedImage);
        StringBuilder url=new StringBuilder("https://maps.googleapis.com/maps/api/streetview?size=600x600&location=");
        url.append(hospitalLatLng.latitude+","+hospitalLatLng.longitude+"&");
        url.append("fov=90&key="+serverkey);
        new DownloadImageTask((ImageView) findViewById(R.id.expandedImage))
                .execute(url.toString());
//        expandedImageView.setImageResource(imageResId);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                zoomAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                zoomAnimator = null;
            }
        });
        set.start();
        zoomAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (zoomAnimator != null) {
                    zoomAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        zoomAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        zoomAnimator = null;
                    }
                });
                set.start();
                zoomAnimator = set;
            }
        });
    }


}
