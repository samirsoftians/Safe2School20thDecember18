package sushant.com.Safe2School;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static sushant.com.Safe2School.R.id.map;

public class NavigationDrawerActivity extends AppCompatActivity
        implements OnMapReadyCallback
{

    Double NEWLAT,NEWLONG;
    String TIME,DATE,LOCATION,newDateString,updatedDate;
    Double srclat=18.651011,srclong=73.769592;//this is the location of school to be displayed in green
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;
    String UPDATEDLATITUDE,UPDATEDLOGITUDE,UPDATEDTIME,UPDATEDLOCATION,UPDATEDDATE,UPDATEDSPEED;
    GoogleMap mMap;
    Marker marker;
    private String SPEED,STATUS,UNAME,VCODE;
    private Intent intent1;
    Timer timer;
    String[] routefinder_response;
    private int mInterval = 60*1000; // 5 seconds by default, can be changed later
    private Handler mHandler;
    TextView textVeh,textDate,textTime,textSpeed,txtLocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        textVeh=(TextView)findViewById(R.id.vName);
        textDate=(TextView)findViewById(R.id.vDate);
        textTime=(TextView)findViewById(R.id.vTime);
        textSpeed=(TextView)findViewById(R.id.vSpeed);
        txtLocation=(TextView)findViewById(R.id.vLocation);

        //   ##################### Getting data of the selected vehicle ####################################

        intent1 = getIntent();
        SPEED=intent1.getStringExtra("vspeed");
        Log.e("speed",SPEED);
        VCODE=intent1.getStringExtra("vcode");
        Log.e("VCODE",VCODE);
        NEWLAT= Double.valueOf(intent1.getStringExtra("vlat"));
        Log.e("lat", String.valueOf(NEWLAT));
        NEWLONG= Double.valueOf(intent1.getStringExtra("vlng"));
        Log.e("log", String.valueOf(NEWLONG));
        LOCATION=getAddress(NavigationDrawerActivity.this,NEWLAT,NEWLONG);
        UNAME=intent1.getStringExtra("vnum");
        Log.e("num",UNAME);
        TIME=intent1.getStringExtra("vtime");
        Log.e("navigation",TIME);
        DATE=intent1.getStringExtra("vdate");
        Log.e("Ndate",DATE);
        SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd ");
        Date newDate = null;
        try {
            newDate = spf.parse(DATE);
            spf = new SimpleDateFormat("dd MMM yyyy");
            newDateString = spf.format(newDate);
        }catch (Exception e){

        }
        Log.e("TM", " ROUTE FINDER RESPONSE " + routefinder_response);

//####################### TIMER TASK FOR UPDATING LOCATION #######################################3
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();//This gets the latest current location of the bus
        timer.schedule(myTimerTask, 60000, 60000);

        //##########################################################################
        if (SPEED.equals("0")) {
            STATUS = " NOT ACTIVE  ";

        } else {
            STATUS = " ACTIVE ";

        }

        //******************************************************************************
        initMap();
        routefinder_response=getArrayList(Preferences.KEY);
        Log.e("route", String.valueOf(routefinder_response));

        textVeh.setText(UNAME);
        textTime.setText(TIME);
        textSpeed.setText(SPEED+"KMPH");
        txtLocation.setText(LOCATION);
        textDate.setText(newDateString);

    }
//*************************************************************************************************************

    private void initMap()
    {

        MapFragment mapFragment= (MapFragment) getFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

    }

    //**********************************************************************************************************
    @Override
    public void onBackPressed()
    {
           Intent i=new Intent(NavigationDrawerActivity.this,MainActivity.class);
           startActivity(i);
           finish();
    }

    //************************************************
    @Override
    public void onMapReady(GoogleMap googleMap)
    {

        // ##################### showing vehicle current position ###################################

            mMap = googleMap;
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.setTrafficEnabled(true);
            mHandler = new Handler();
            startRepeatingTask();
        //  marker=mMap.addMarker(options2);
           /* mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public View getInfoContents(Marker marker) {

                View view = getLayoutInflater()
                        .inflate(R.layout.custom_info_window, null);

                TextView name_tv = (TextView) view.findViewById(R.id.vname);
                TextView details_tv = (TextView) view.findViewById(R.id.vdate);
                TextView food_tv = (TextView) view.findViewById(R.id.vtime);
                TextView transport_tv = (TextView) view.findViewById(R.id.vLocation);
                TextView speed = (TextView) view.findViewById(R.id.vSpeed);

                name_tv.setText(UNAME);
                details_tv.setText(newDateString);
                food_tv.setText(TIME);
                transport_tv.setText(LOCATION);
                speed.setText(SPEED+"KMPH");

                return view;
            }
        });

//              mMap.addMarker(options2);*/
        try{
            int arraylength=0;
            if(routefinder_response!=null){
                arraylength=routefinder_response.length;



        Log.i("TM"," ROUTE RESPONSE "+routefinder_response);

        LatLng thirdlast = new LatLng(Double.parseDouble(routefinder_response[arraylength-12]), Double.parseDouble(routefinder_response[arraylength-11]));
        MarkerOptions options4 = new MarkerOptions();
        options4.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        options4.title(routefinder_response[arraylength-10]);
        options4.position(thirdlast);
        mMap.addMarker(options4);


        LatLng fourthlast = new LatLng(Double.parseDouble(routefinder_response[arraylength-15]), Double.parseDouble(routefinder_response[arraylength-14]));
        MarkerOptions options12 = new MarkerOptions();
        options12.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        options12.title(routefinder_response[arraylength-13]);
        options12.position(fourthlast);
        mMap.addMarker(options12);

        LatLng fifthlast = new LatLng(Double.parseDouble(routefinder_response[arraylength-18]), Double.parseDouble(routefinder_response[arraylength-17]));
        MarkerOptions options20 = new MarkerOptions();
        options20.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        options20.title(routefinder_response[arraylength-16]);
        options20.position(fifthlast);
        mMap.addMarker(options20);


        LatLng secondlast = new LatLng(Double.parseDouble(routefinder_response[arraylength-6]), Double.parseDouble(routefinder_response[arraylength-5]));
        MarkerOptions options10 = new MarkerOptions();
        options10.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        options10.title(routefinder_response[arraylength-4]);
        options10.position(secondlast);
        mMap.addMarker(options10);

        //******************************Location of the school to be displayed in green using lat long*************

        LatLng destination = new LatLng(srclat,srclong);
        MarkerOptions options5 = new MarkerOptions();
        options5.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        options5.title("School Building");
        options5.position(destination);
        mMap.addMarker(options5);
//*****************************************************************************************************************

        String url20 = getDirectionsUrl(fourthlast,fifthlast);
        DowwnloadTask dowwnloadTask20 = new DowwnloadTask();
        dowwnloadTask20.execute(url20);

        String url21 = getDirectionsUrl(fifthlast,thirdlast);
        DowwnloadTask dowwnloadTask21 = new DowwnloadTask();
        dowwnloadTask21.execute(url21);

        String url14 = getDirectionsUrl(thirdlast,secondlast);
        DowwnloadTask dowwnloadTask14 = new DowwnloadTask();
        dowwnloadTask14.execute(url14);

        String url15 = getDirectionsUrl(secondlast, destination);
        DowwnloadTask dowwnloadTask15 = new DowwnloadTask();
        dowwnloadTask15.execute(url15);


        String fstlat=routefinder_response[0];
        Log.i("fstlat",fstlat);
                String fstlong=routefinder_response[1];
                Log.i("fstlong",fstlong);

        Log.i("TM"," arrlength"+arraylength);
        Log.i("TM"," arrlength"+routefinder_response[arraylength-1]);
        Log.i("TM"," arrlength"+routefinder_response[arraylength-2]);
        Log.i("TM"," arrlength"+routefinder_response[arraylength-3]);
        Log.i("TM"," arrlength"+routefinder_response[arraylength-3]);
        Log.i("TM"," arrlength"+routefinder_response[arraylength-3]);

        for (int i=0;i<=arraylength-6;i+=3)
        {

            Log.i("TM"," INDEX"+ i);
            Log.i("TM"," VALUE 1 "+ routefinder_response[i]);
            Log.i("TM"," VALUE 2 "+ routefinder_response[i+1]);
            Log.i("TM"," VALUE 3 "+ routefinder_response[i+2]);
            Log.i("TM"," VALUE 4 "+ routefinder_response[i+3]);
            Log.i("TM"," VALUE 5 "+ routefinder_response[i+4]);
            Log.i("TM"," VALUE 6 "+ routefinder_response[i+5]);


            LatLng ponit1 = new LatLng(Double.parseDouble(routefinder_response[i]), Double.parseDouble(routefinder_response[i + 1]));
            MarkerOptions options3 = new MarkerOptions();
            options3.title(routefinder_response[i + 2]);
            options3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            options3.position(ponit1);
            mMap.addMarker(options3);

                //i += 3;

                LatLng point2 = new LatLng(Double.parseDouble(routefinder_response[i+3]), Double.parseDouble(routefinder_response[i + 4]));
                MarkerOptions options6 = new MarkerOptions();
                options6.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                options6.title(routefinder_response[i + 5]);
                options6.position(point2);
                mMap.addMarker(options6);



            String url13 = getDirectionsUrl(ponit1, point2);
            DowwnloadTask dowwnloadTask13 = new DowwnloadTask();
            dowwnloadTask13.execute(url13);

         }

        LatLng source = new LatLng(Double.parseDouble(fstlat), Double.parseDouble(fstlong));
        MarkerOptions options = new MarkerOptions();
        options.title(" Source Point");
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
        options.position(source);
        mMap.addMarker(options);

//        ############################### ROUTE PLOTT FROM SOURCE TO DESTINATION

        /*LatLng source=new LatLng(Double.parseDouble(orglat),Double.parseDouble(orglng)); //  ETZ
        MarkerOptions options3=new MarkerOptions();
        options3.title("ORIGIN");
        options3.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        options3.position(source);
        mMap.addMarker(options3);

        LatLng destination=new LatLng(Double.parseDouble(destlat),Double.parseDouble(destlng)); // Talegaon
        MarkerOptions options4=new MarkerOptions();
        options4.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        options4.title("DESTINATION");
        options4.position(destination);
        mMap.addMarker(options4);


        String url13=getDirectionsUrl(drop13,destination);
        DowwnloadTask dowwnloadTask13=new DowwnloadTask();
        dowwnloadTask13.execute(url13);
*/
    }
        }catch (Exception e){
            Log.e("exception",e.getMessage());
        }
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                mMap.clear();
                LatLng latLng1 = new LatLng( NEWLAT,NEWLONG);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng1)
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.busart)));
                MarkerOptions options2 = new MarkerOptions();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 19));
                options2.icon(BitmapDescriptorFactory.fromResource(R.drawable.busart));
                options2.position(latLng1);
                marker=mMap.addMarker(options2);
                mMap.getUiSettings().setRotateGesturesEnabled(true);

                // Enable / Disable zooming functionality
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                    @Override
                    public View getInfoWindow(Marker arg0) {
                        return null;
                    }

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public View getInfoContents(Marker marker) {

                        View view = getLayoutInflater()
                                .inflate(R.layout.custom_info_window, null);

                        TextView name_tv = (TextView) view.findViewById(R.id.vname);
                        TextView details_tv = (TextView) view.findViewById(R.id.vdate);
                        TextView food_tv = (TextView) view.findViewById(R.id.vtime);
                        TextView transport_tv = (TextView) view.findViewById(R.id.vLocation);
                        TextView speed = (TextView) view.findViewById(R.id.vSpeed);

                        name_tv.setText(UNAME);
                        Log.e("newDateString",newDateString);
                        details_tv.setText(newDateString);
                        food_tv.setText(TIME);
                        transport_tv.setText(LOCATION);
                        speed.setText(SPEED+"KMPH");

                        return view;
                    }
                });
                Log.e("Inside handler","Execution in 2 minutes...");
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    class  DowwnloadTask extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... params) {

            String data = "";

            try
            {
                data = downloadUrl(params[0]);
            }
            catch (Exception e)
            {
                Log.d("Background Task", e.toString());
            }
            return data;

        }
        @Override
        protected void onPostExecute(String s)
        {

            MyTask myTask=new MyTask();

            myTask.execute(s);

            super.onPostExecute(s);
        }
    }
//*************************************************************************
    class MyTask extends AsyncTask<Object, Object, List<List<HashMap<String, String>>>>
        {


        @Override
        protected List<List<HashMap<String, String>>> doInBackground(Object... params)
        {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try
            {
                jObject = new JSONObject((String) params[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();//***************
                routes=parser.parse(jObject);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return routes;

        }

        //*******************************************************************************************************
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists)
        {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            Log.i("TM","lists"+lists);
            Log.i("TM","lists"+lists);

            for (int i = 0; i < lists.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);

               // Log.i("TM","PATH"+path);

                for (int j = 0; j < path.size(); j++)
                {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route


            if (lineOptions!=null)
            {

                mMap.addPolyline(lineOptions);
                super.onPostExecute(lists);
            }
        }
    }
//**************************************************************************************************************************
    private String getDirectionsUrl(LatLng origin, LatLng dest)
    {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }
//*****************************************************************************

    private String downloadUrl(String strUrl) throws IOException
    {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try
        {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        }
        catch (Exception e)
        {
                Log.d("Exception", e.toString());
        }
        finally
        {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    //###################### Updating Map data in every 60 seconds ################################
    class UpdateLocation extends AsyncTask<String,String,String>
    {


        @Override
        protected String doInBackground(String... params)
        {
            int i = 0;
            String response = "";
            try
            {

//                Log.i("TM"," VCODE "+LoginActivity.vcode);
//                String url = "http://103.241.181.36:8080/FleetAndrProject/OnlineData?vehiclecode="
//                String url = "http://192.168.2.26:8080/FleetAndrProject/OnlineData?vehiclecode="
//                String url = "http://192.168.2.26:8080/AndrFleetApp/OnlineData?vehiclecode="
                String url = "http://103.241.181.36:8080/AndrFleetApp4/OnlineData?vehiclecode="+VCODE;
                i++;
                url = url.replaceAll(" ", "%20");
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null)
                {

                    response += s;
                    Log.e("response+=",response);

                }
                // System.out.println("The response =>" + response);
//                Thread.sleep(2000);
            }
            catch (Exception e)
            {
                System.out.println("Exception occured!!" + e);
            }
            if (!(response.equals("No_Data"))) {

                try
                {
                    String[] rows = response.split("\\$");
                    publishProgress(rows);

                    Log.i(" TM "," RESPONSE  "+rows);

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

            }
            else
            {
            }


            return response;
        }


        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);
            String timeStamp = new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime());
            Log.i("TM"," VALUES  "+values.length);

            if (values.length==8)
            {
                UPDATEDDATE = values[0];
                Log.e("updatedDate",UPDATEDDATE);
                UPDATEDTIME = values[1];
                UPDATEDLATITUDE = values[2];
                UPDATEDLOGITUDE = values[3];
               // UPDATEDLOCATION = values[4];
                UPDATEDSPEED=values[5];

                try {
                int arraylength = 0;
                if (routefinder_response != null) {
                    arraylength = routefinder_response.length;


                    //  int arraylength=routefinder_response.length;

                    String fstlat = routefinder_response[0];
                    String fstlong = routefinder_response[1];
                }
            }catch (Exception e){

            }

            //###################### Setting Updated data in Google Map ################################
                SimpleDateFormat spf=new SimpleDateFormat("yyyy-MM-dd ");
                Date newDate = null;
                try {
                    newDate = spf.parse(UPDATEDDATE);
                    spf = new SimpleDateFormat("dd MMM yyyy");
                    updatedDate = spf.format(newDate);
                    Log.e("conversion",updatedDate);
                }catch (Exception e){

                }
                if (marker!=null)
                {


                    marker.remove();
                    mMap.clear();

                    LatLng latLng1 = new LatLng(Double.parseDouble(UPDATEDLATITUDE), Double.parseDouble(UPDATEDLOGITUDE));
//                LatLng latLng1 = new LatLng(18.457600, 73.867057);
                    UPDATEDLOCATION=getAddress(NavigationDrawerActivity.this,Double.parseDouble(UPDATEDLATITUDE), Double.parseDouble(UPDATEDLOGITUDE));
                    MarkerOptions options2 = new MarkerOptions();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 19));
                    options2.icon(BitmapDescriptorFactory.fromResource(R.drawable.busart));
                    options2.position(latLng1);
                    mMap.addMarker(options2);
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                        @Override
                        public View getInfoWindow(Marker arg0) {
                            return null;
                        }

                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public View getInfoContents(Marker marker) {

                            View view = getLayoutInflater()
                                    .inflate(R.layout.custom_info_window, null);

                            TextView name_tv = (TextView) view.findViewById(R.id.vname);
                            TextView details_tv = (TextView) view.findViewById(R.id.vdate);
                            TextView food_tv = (TextView) view.findViewById(R.id.vtime);
                            TextView transport_tv = (TextView) view.findViewById(R.id.vLocation);
                            TextView speed = (TextView) view.findViewById(R.id.vSpeed);

                            name_tv.setText(UNAME);
                            details_tv.setText(newDateString);
                            food_tv.setText(UPDATEDTIME);
                            transport_tv.setText(UPDATEDLOCATION);
                            speed.setText(UPDATEDSPEED+"KMPH");

                            return view;
                        }
                    });

                }


            }
            else
            {
                String info=" You are ONNline Check your internet Connection.";
                Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<font color='#e3f2fd' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.DISPLAY_CLIP_HORIZONTAL, 0, 0);
                toast.show();


            }

            textVeh.setText(UNAME);
            textTime.setText(UPDATEDTIME);
            textSpeed.setText(UPDATEDSPEED+"KMPH");
            txtLocation.setText(UPDATEDLOCATION);
            textDate.setText(newDateString);

        }

        @Override
        protected void onPostExecute(String s)
        {
            super.onPostExecute(s);


        }
    }
//******************************************************************************
    class MyTimerTask extends TimerTask
    {
        @Override
        public void run()
        {
            if (networkInfo!=null&&networkInfo.isConnected())
            {

                new UpdateLocation().execute();
                Log.i("TM"," TIMER TASK EXECUTED....");
            }
            else
            {
                String info=" You are Offline Check your internet Connection.";
                Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<font color='#e3f2fd' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.DISPLAY_CLIP_HORIZONTAL, 0, 0);
                toast.show();
            }

        }
    }

//###################### Getting route key from MainActivity class ################################

    public String[] getArrayList(String key) {
        Gson gson = new Gson();


        String json=Preferences.getInstance(getApplicationContext()).getdata(Preferences.KEY);
        /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NavigationDrawerActivity.this);
        Gson gson = new Gson();
        String json = prefs.getString(key,null);*/
        Type type = new TypeToken<String[]>() {
        }.getType();
        return gson.fromJson(json, type);
    }

//###################### Getting complete address for Location ################################

    public String getAddress(Context context, double LATITUDE, double LONGITUDE) {
        String address=" ";
        //Set Address
        try {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null && addresses.size() > 0) {



                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

                Log.e("ss", "getAddress:  address" + address);
                Log.e("ss", "getAddress:  city" + city);
                Log.e("ss", "getAddress:  state" + state);
                Log.e("ss", "getAddress:  postalCode" + postalCode);
                Log.e("ss", "getAddress:  knownName" + knownName);

            }
        } catch (IOException e) {
            Log.e("ss",e.getMessage());
            e.printStackTrace();
        }
        return address;
    }
}
