package com.example.buglab.liveon.utility;

/**
 * Created by aabir on 10/17/2016.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.akexorcist.googledirection.model.Info;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PlaceDetailsBackground extends AsyncTask<String, Void, String> {
    //        private TextView textView;
    private LatLng latLng;
    Context context;
    Double myLat,myLon;
    String placePhoneNumber;
    Double placeLat,placeLon;
    private static final String APP_ID = "AIzaSyBc-g4Wl0D6RP-rLKaVEvwhsC19L7EkWLw";
    Info durationinfo, distanceinfo;
    StringBuilder placeDetails=new StringBuilder();
    ProgressDialog progressDialog;
    String jsonData = null;




    public PlaceDetailsBackground(ProgressDialog progressDialog,Double myLat,Double myLon,String placePhoneNumber) {
//            this.textView = textView;
//        this.latLng=latLng;
        this.myLat=myLat;
        this.myLon=myLon;
        this.progressDialog=progressDialog;
        this.placePhoneNumber=placePhoneNumber;
    }

    @Override
    protected String doInBackground(String... strings) {
//        jsonData=getUrlContents(strings[0]);
        String place_id = strings[0];
        return findPlaces(place_id);

    }

    protected String getJSON(String url) {
        return getUrlContents(url);
    }

    public String findPlaces(String place_id) {
        StringBuilder content = new StringBuilder();
        String urlString = makeUrl(place_id);

        String json = getJSON(urlString);
        String phonenumber=null;
        String adrs=null;
        String name=null;
        Log.d("PlaceInfo",urlString);
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            JSONObject result=(JSONObject)object.get("result");
            adrs=result.getString("formatted_address");
            placeDetails.append(""+adrs);
            JSONObject geometry=(JSONObject)result.get("geometry");
            JSONObject location=(JSONObject)geometry.get("location");
            name=result.getString("name");
            placeLat=location.getDouble("lat");
            placeLon=location.getDouble("lng");
            Location myLoc=new Location("");
            myLoc.setLatitude(myLat);
            myLoc.setLongitude(myLon);

            Location placeLoc=new Location("");
            placeLoc.setLatitude(placeLat);
            placeLoc.setLongitude(placeLon);

            float distance=myLoc.distanceTo(placeLoc);
            placeDetails.append(":"+distance);
            phonenumber=result.getString("international_phone_number");

        } catch (JSONException e) {
            Log.d("PlaceInfo",e.toString());
        }
        if(phonenumber==null)
        {
            phonenumber="No Number Found";
//            EmergencySearchActivity.placePhoneNumber=phonenumber;
            placeDetails.append(":"+"N/A");
        }
        else
        {
            placeDetails.append(":"+phonenumber);
//            EmergencySearchActivity.placePhoneNumber=phonenumber;
        }
        Log.d("PlaceInfo",placeDetails.toString());


        return placeDetails.toString();
    }






    private String getUrlContents(String theUrl)
    {
        String urldata = theUrl;
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
            urldata=content.toString();

            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urldata;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
            progressDialog.show();
    }

    @Override
    protected void onPostExecute(String temp) {
            placePhoneNumber=temp;
        Log.d("onPost",temp);

//        latLng=new LatLng(arrayList.get(pos).getLatitude(),arrayList.get(pos).getLongitude());
//            StringBuilder placeList=new StringBuilder();
//              EmergencySearchActivity.placePhoneNumber=temp;
        progressDialog.dismiss();
                super.onPostExecute(temp);

    }



    private String makeUrl(String place_id) {
        StringBuilder urlString = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
//        StringBuilder urlString = new StringBuilder("https://www.google.com");
        urlString.append("placeid=");
        urlString.append(place_id);
        urlString.append("&key=" + APP_ID);

        return urlString.toString();
    }


    public String getInfo()
    {
        return placeDetails.toString();
    }
}
