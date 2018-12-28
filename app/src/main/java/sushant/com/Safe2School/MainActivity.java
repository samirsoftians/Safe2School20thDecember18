package sushant.com.Safe2School;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    String vehNumber,vcode,email;
    TextView vloc, vdate, vtime, vNumber, vspeed;
    private static Timer timer;
    String[] RouteFinder_Response;
    public String orglat, orglng;
    Context context;
    List<String> vName;
    List<String> date;
    List<String> vehtime;
    List<String> vlat;
    List<String> vlng;
    List<String> vlocation;
    List<String> vSpeed;
    List<String> vCode;
    private RecyclerView mRecyclerView;
    CustomLeaderAdapter customAdapter;
    static int count=0;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ########################## Initializing ###############################

        vName=new ArrayList<>();
        date=new ArrayList<>();
        vehtime=new ArrayList<>();
        vlat=new ArrayList<>();
        vlng=new ArrayList<>();
        vSpeed=new ArrayList<>();
        vCode=new ArrayList<>();
        vlocation=new ArrayList<>();
        vloc = (TextView) findViewById(R.id.vLocation);
        vspeed = (TextView) findViewById(R.id.vSpeed);
        vdate = (TextView) findViewById(R.id.vDate);
        vtime = (TextView) findViewById(R.id.vTime);
        vNumber = (TextView) findViewById(R.id.vName);

        // ########################## Adding progress bar for loading data ###############################

         progressDialog = new ProgressDialog(MainActivity.this);
         progressDialog.setMessage("Fetching Data....");
         progressDialog.show();
         progressDialog.setCancelable(true);

        // ########################## Getting data from LoginActivity ###############################

        final SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
        final SharedPreferences pref = getSharedPreferences("mypref", MODE_PRIVATE);
        //password=pref.getString("nameKey",null);
        try {

            email= Preferences.getInstance(getApplicationContext()).getdata(Preferences.Email);


           // email=pref.getString("emailKey",null);
        Log.e("email",email);

            vcode= Preferences.getInstance(getApplicationContext()).getdata(Preferences.VCODE);

            //vcode=prefs.getString("vcode", null);
        Log.e("vcode",vcode);

            vehNumber= Preferences.getInstance(getApplicationContext()).getdata(Preferences.VNUM);

           // vehNumber=prefs.getString("vnum", null);
        Log.e("num",vehNumber);
        new RouteFinderReq().execute();
        }catch (Exception e){
            Log.e("exception",e.getMessage());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ########################## Recyclerview for setting data from adapter ###############################

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        customAdapter = new CustomLeaderAdapter(context,vName,date,vehtime,vlat,vlng,vCode,vSpeed);
        mRecyclerView.setAdapter(customAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            String msg = "User Profile:)" + email;
            Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<font color='#e3f2fd' ><b>" + msg + "</b></font>"), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();
        } else if (id == R.id.nav_gallery) {
            Intent i=new Intent(MainActivity.this,MainActivity.class);
            startActivity(i);
            finish();
        }else if(id== R.id.change_pswd){
            Intent i=new Intent(MainActivity.this,ChangePassword.class);
            //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            MainActivity.this.finish();
            finish();
        }
        else if (id == R.id.nav_manage) {
// Logggd Off




                /*SharedPreferences preferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();*/
            Preferences.getInstance(getApplicationContext()).logout();



            finish();
                startActivity(new Intent(this, LoginActivity.class));
                String info = " Successfully Logged Off";
                Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<font color='#e3f2fd' ><b>" + info + "</b></font>"), Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.DISPLAY_CLIP_HORIZONTAL, 0, 0);
                toast.show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setIcon(R.drawable.appicon);
        builder1.setMessage("Do you want to exit");
        builder1.setTitle("Safe2School Application");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        try {
                            timer=new Timer();
                            if (timer != null) {
                                timer.cancel();
                                timer = null;
                                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory( Intent.CATEGORY_HOME );
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                System.exit(0);
                                Log.e("inside if","if");
                            }
                            else
                                Log.e("cancel","cancel");
                           // Log.e("cancelling timer",timer.toString());
                        }catch (Exception e){
                            Log.e("timer exception",e.getMessage());
                        }
                        finish();

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    class RouteFinderReq extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            int i = 0;

            String response = "";
            try {

//                    String url = "http://103.241.181.36:8080/FleetAndrProject/OnlineData?vehiclecode="
//                String url = "http://192.168.2.26:8080/AndrFleetApp1/RouteFinder?user="
//                    String url = "http://192.168.2.26:8080/FleetAndrProject/OnlineData?vehiclecode="

                String url = "http://103.241.181.36:8080/AndrFleetApp4/RouteFinder?user="
                        + email+"&vehiclecode="+vcode;
                Log.e("MainActivity", url);
                i++;
                url = url.replaceAll(" ", "%20");
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));

                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
                Log.e("TM", "RESPONSE FROM ROUTE FINDER SERVLET " + response);

                Log.e("TM", " ROUTE FINDER  " + response);

            } catch (Exception e) {
                Log.e("Exception occured!!", e.getMessage());
            }
            if (!(response.equals("No_Data"))) {

                try {

                    String[] rows = response.split("\\$");
                    publishProgress(rows);
                    Log.e(" TM ", " RESPONSE  " + rows);


                    RouteFinder_Response = rows;
                    Log.e("response", String.valueOf(RouteFinder_Response));
                    saveArrayList(RouteFinder_Response, Preferences.KEY);//HERE IS ANOTHER SHARED PREFERENCE

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {


            }
            return response;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            // ########################## Timer for updating data in every 30 seconds ###############################

            new CurrentPositonRequest().execute();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    count=0;
                    new CurrentPositonRequest().execute();
                    Log.e("VehicleInfoActivity", "Timer executed");
                    if (timer == null) {
                        cancel();
                    }else
                        Log.e("else","else");
                }

            }, 0, 30*1000);

        }
    }


    // ########################## Class for getting multiple vehicles data ###############################


    class CurrentPositonRequest extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            int i = 0;

            String response = "";
            try {

                Log.e("TM", "ORG LAT122312" + orglat + " " + orglng);


//                    String url = "http://103.241.181.36:8080/FleetAndrProject/OnlineData?vehiclecode="
//                    String url = "http://192.168.2.26:8080/AndrFleetApp1/OnlineData?vehiclecode="
//                    String url = "http://192.168.2.26:8080/FleetAndrProject/OnlineData?vehiclecode="

              //  String url = "http://103.241.181.36:8080/AndrFleetApp3/OnlineData?vehiclecode=" + VCODE;
                 String url = "http://103.241.181.36:8080/AndrFleetApp4/OnlineData?vehiclecode="+vcode;
                Log.e("MainActivity", "vcode url" + url);
                i++;
                url = url.replaceAll(" ", "%20");
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));

                String s = "";
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }

            } catch (Exception e) {
                System.out.println("Exception occured!!" + e);
            }
            if (!(response.equals("No_Data"))) {
                try {

                    date.clear();
                    vehtime.clear();
                    vlat.clear();
                    vlng.clear();
                    vlocation.clear();
                    vSpeed.clear();
                    vCode.clear();
                    vName.clear();

                    String[] str = response.split("##");
                    int length=str.length;
                    Log.e("length", String.valueOf(length));
                    //      Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();///************
                    for (int j = 0; j < str.length; j++) {
                            //int count = 0;
                            String line = str[j];
                            Log.e("line", line);
                            if(!line.equals(" ")) {
                                String[] rowsnew = line.split("\\$");
                                count++;
                                //publishProgress(rowsnew);
                                date.add(rowsnew[0]);
                                vehtime.add(rowsnew[1]);
                                vlat.add(rowsnew[2]);
                                vlng.add(rowsnew[3]);
                                vlocation.add(rowsnew[4]);
                                vSpeed.add(rowsnew[5]);
                                vCode.add(rowsnew[6]);
                                vName.add(rowsnew[7]);

                                Log.e("TM", " RESPONSE " + rowsnew);
                            }else{
                                Log.e("Inside else","else");
                            }

                    }
                    customAdapter.notifyDataSetChanged();
                    mRecyclerView.smoothScrollToPosition(str.length-1);
                    Log.e("TM", "RESPONSE FROM ONLINEDATA SERVLET " + response);
                    progressDialog.dismiss();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {


            }
            return response;
        }

        @Override
        protected void onProgressUpdate(final String... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(String s) {
//                myPd_ring.dismiss();
            super.onPostExecute(s);

        }

        @Override
        protected void onCancelled() {

            super.onCancelled();

        }
    }

    // ########################## Method for saving list of routes in shared preferences ###############################

        public void saveArrayList(String[] list, String key) {
            /*SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(list);
            editor.putString(key, json);
            editor.apply();*/


            // This line is IMPORTANT !!!
            Gson gson = new Gson();

            String json = gson.toJson(list);

            Preferences.getInstance(getApplicationContext()).update(Preferences.KEY,json);





        }

}
