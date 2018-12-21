


package sushant.com.Safe2School;
import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dd.processbutton.iml.ActionProcessButton;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import info.hoang8f.widget.FButton;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    int nullEntryCheck = 0;

    static String[] USERNAME;
    private static final int REQUEST_READ_CONTACTS = 0;
    public static String vcode, vnum, status;
    String imeiNo;
    TelephonyManager telephonyManager;

///*/*/*****************Puttihng the shre preference over here ***************

    RequestQueue requestQueue;
    SharedPreferences sharedpreferences,sh_pref;
    public static final String mypreference = "mypref";
    public static final String Password = "nameKey";
    public static final String Email = "emailKey";
    int OTP;


    //*****************Ends here *****************************


//   DROP AND PICK UP TIME


    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    //private View mProgressView;
    private View mLoginFormView;
    private TextView forgotpswdTV;

    private String email, password;
    public static String[] typeuser;
    NetworkInfo networkInfo;
    private ConnectivityManager connec;
    TextView vNumber;
    SharedPreferences pref;
    String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mLoginFormView = findViewById(R.id.login_form);
        // mProgressView = findViewById(R.id.login_progress);


        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        imeiNo = new String();
        try {
            telephonyManager = (TelephonyManager) this.getSystemService(getApplicationContext().TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling/media/twtech/6D33-6396/login
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            imeiNo = telephonyManager.getDeviceId();
            //Log.e("IMEI ", "Number : " + imeiNo);
        } catch (Exception e) {

        }

        sh_pref=getSharedPreferences("userlogin",Context.MODE_PRIVATE);//**************DONT KONW
        forgotpswdTV = (TextView) findViewById(R.id.forgot_pswd);
        forgotpswdTV.setPaintFlags(forgotpswdTV.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotpswdTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i=new Intent(LoginActivity.this,UserForgetPassword.class);
                startActivity(i);*/
                showForgetPswdDialog();
            }
        });

        pref = getApplicationContext().getSharedPreferences("OTPPref", MODE_PRIVATE); // 0 - for private mode

        mPasswordView = (EditText) findViewById(R.id.password);
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        vNumber = (TextView) findViewById(R.id.vName);


        //****************Checking the internet connection and stopping the flow of exection for the next activity***


        ConnectivityManager connManager2 = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo2 = connManager2.getActiveNetworkInfo();

        if (networkInfo2 != null && networkInfo2.isConnected()) {


            if (sharedpreferences.contains(Email)) {
                // myPd_ring= ProgressDialog.show(LoginActivity.this, "", "Please wait......", true);


                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
//
//                        // TODO Auto-generated method stub
//
                    attemptLogin();


                } else {
                    String info = " Check Your Internet Connection ";
                    Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<font color='#e3f2fd' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.DISPLAY_CLIP_HORIZONTAL, 0, 0);
                    toast.show();

                }


            }


            //*-*****************here is the on click listener for the actoin*********
            //***************HERE IS THE CODE TO GET THE VALUE FROM THE SHARED PRERERENCE **********


            ///*********************Ends here *****************************************


//        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
//                if (id == R.id.login || id == EditorInfo.IME_NULL)
//
//                {
//
//
//                    attemptLogin();
//                    return true;
//                }
//                return false;
//            }
//        });

            //*****************************Ends here**********************************
            connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            networkInfo = connec.getActiveNetworkInfo();

            OTP = new Integer(0);
            //**************************************here is the button on click listener**********

            Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    nullEntryCheck = 0;

                    // Toast.makeText(LoginActivity.this, "YOU CLICKED", Toast.LENGTH_SHORT).show();
                    ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {


                        if (mEmailView.getText().toString().equals("") || mEmailView.getText().toString().equals(null)) {
                            nullEntryCheck++;
                            Log.e("MainActivity", mEmailView.getText().toString());
                        }

                        if (mPasswordView.getText().toString().equals("") || mPasswordView.getText().toString().equals(null)) {
                            nullEntryCheck++;
                            Log.e("MainActivity", mPasswordView.getText().toString());
                        }

                        if (nullEntryCheck > 0) {
                            mEmailView.setError("Please Enter UserName  on button click");
                            mPasswordView.setError("Please Enter Password on button click");
                            Log.e("MainActivity", "error");
                        } else {
                            attemptLogin();
                            Log.e("MainActivity", "inside else");
                        }


                    } else {
                        String info = " Check Your Internet Connection ";
                        Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<font color='#e3f2fd' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.DISPLAY_CLIP_HORIZONTAL, 0, 0);
                        toast.show();

//                       Toast.makeText(LoginActivity.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

        //**********************Else block ends here ***************************

        else {
            android.support.v7.app.AlertDialog.Builder ad1 = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);
            ad1.setMessage("PLease connect to Internet ");
            ad1.setCancelable(false);


            ad1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            android.support.v7.app.AlertDialog alert = ad1.create();
            alert.show();
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
        getLoaderManager().initLoader(0, null, this);//this method loads the contacts
    }

    //****************************This is the method called *****************
    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                populateAutoComplete();

            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */


    //**************************************Login code starts here *****************************************
    private void attemptLogin() {

         Log.e("attemptlogin","initialized");
        if (sharedpreferences.contains(Email)) {
            Log.e("attemptlogin","email"+Email);

        } else {

            if (mAuthTask != null) {
                return;
            }
            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);
            Log.e("MainActivity", "attemptLogin()");

        }

        if (sharedpreferences.contains(Email)) {
            Log.e("mail","email"+email+",");
           /* try {
                if (email.equals(null)) {
                    SharedPreferences sp = getSharedPreferences("mypref", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed1 = sp.edit();
                    email = sp.getString("emailKey", "");
                    password = sp.getString("confirmpswd", "");
                }
            }catch (Exception e){
                Log.e("ss",e.getMessage());
            }
*/

            email = sharedpreferences.getString(Email, "");
            password = sharedpreferences.getString(Password, "");
            Log.e("attemptlogin","email"+email+","+password);
            Toast.makeText(this, "Email="+email, Toast.LENGTH_SHORT).show();



            //will have both values

        } else {

            email = mEmailView.getText().toString();
            password = mPasswordView.getText().toString();
            Log.e("attemptlogin1","email"+email+","+password);

        }
        //***********************Changes Ends here **********************

        boolean cancel = false;
        View focusView = null;


        if (sharedpreferences.contains(Email)) {
            if (cancel) {

                focusView.requestFocus();
                Log.e("if Cancel", "cancel");
            } else {
                Log.e("inside else", "else");

                showProgress(true);//progress bar to show for some time


                if (!email.equals("") && !password.equals("")) {

                    mAuthTask = new UserLoginTask(email, password);
                    Log.e("checking email", "checking credentials"+email+","+password);
                    mAuthTask.execute((Void) null);
//*************why this block to be executed ********************************
                   /* if(sh_pref.contains("username") && sh_pref.contains("usrpswd")) {
                        email = sh_pref.getString("username", "");
                        password = sh_pref.getString("usrpswd", "");
                        mAuthTask = new UserLoginTask(email, password);
                        mAuthTask.execute((Void) null);
                        Log.e("inside elseif", email + "," + password);
                    }*/


                   //********************DONT KNOW *******************************
                }
               /* else if(sh_pref.contains("username") && sh_pref.contains("usrpswd")){
                    email=sh_pref.getString("username","");
                    password=sh_pref.getString("usrpswd","");
                    mAuthTask = new UserLoginTask(email, password);
                    mAuthTask.execute((Void) null);
                    Log.e("inside elseif",email+","+password);
                }*/

                else {

                    //setError made change here *************

//                    mEmailView.setError(" Enter the valid Username ");
//                    mPasswordView.setError(" Enter the valid Password ");


                    //*-*******************Ends here**************
                    Log.e("email equals to 0", "email null");

                }
            }


            //*******************ENDS HERE 14 APRIL **************************************************

        } else {

            if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
                mPasswordView.setError("Please Enter Valid Password");
                focusView = mPasswordView;
                Log.e("mPasswordView", String.valueOf(mPasswordView));
                cancel = true;
            }


            //****************Changes made here*********


            if (cancel) {

                focusView.requestFocus();
                Log.e("if cancel","cancel");
            } else {

                Log.e("second else", "second else");

                showProgress(true);//progress bar to show for some time


                if (!email.equals("") && !password.equals("")) {

                    mAuthTask = new UserLoginTask(email, password);
                    mAuthTask.execute((Void) null);
                    Log.e("attempt login","authentication");

                } else {
                    showProgress(false);
                    mEmailView.setError(" Enter the valid Username sdofsdfosdiff ");
                    mPasswordView.setError(" Enter the valid Password sdifdsofidf ");
                    Log.e("valid username","valid password");


                }
            }
            // *********Ends here *************************

        }

    }

    //***********************************Ends here *******************************************

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("1");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            /*mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });*/
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            //  mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }
        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION =
                {
                        ContactsContract.CommonDataKinds.Email.ADDRESS,
                        ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
                };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, String, Boolean> {


        public final String mEmail;
        private final String mPassword;

        String response = "";
        String nameOfUser = " ";
        String s = " ";

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String nameOfUser = "";

            try {

//                String url = "http://103.241.181.36:8080/FleetAndrProject/LoginServlet?username="
//                String url = "http://192.168.2.26:8080/FleetAndrProject/LoginServlet?username="
//                String url = "http://192.168.2.26:8080/AndrFleetApp1/LoginServlet?username="
//                String url = "http://103.241.181.36:8080/FleetAndrApp/LoginServlet?username="
                /*String url = "http://103.241.181.36:8080/AndrFleetApp3/LoginServlet?username=" + email + "&password="
                        + password;*/
                String url = "http://103.241.181.36:8080/AndrFleetApp4/LoginServlet?username=" + email + "&password=" + password;


                //Espalier#EndUser

                Log.e("MainActivity", url);

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(url);

                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();

                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(content));
                String s = "";
                while ((s = buffer.readLine()) != null) {

                    nameOfUser += s;
                    Log.e("MainActivity", nameOfUser);

                }

                //  Toast.makeText(LoginActivity.this, nameOfUser.toString(), Toast.LENGTH_SHORT).show();//*****************
                typeuser = nameOfUser.split("#");

                // publishProgress(nameOfUser);
                Log.e("response login servlet" , nameOfUser);
                Log.e("response login servlet", String.valueOf(typeuser));
                Log.e("response login servlet", s);

                Log.e("TM", "RESPONSE FROM Login SERVLET " + nameOfUser + "----- " + typeuser + " " + s);

//                    Thread.sleep(2000);
            } catch (Exception e) {
                Log.e("Exception occured!!", e.getMessage());
                // return false;
            }
//###########################################################################################################################3
            if (!(nameOfUser.equals("Not_OK")) && nameOfUser != null) {

                try {
//**************************SAVING THE VALUE OF THE SHARED PREFERENCE VALUE OVER HERE  ******************************
                    String e = email;
//                    String n = loginPassword.getText().toString();
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Password, password);
                    editor.putString(Email, email);
                    editor.commit();

                    //*****************ENDS HERE ****************************

                    Log.e("TM", "REQ TO CURRENT POSIITON  ");

//                    String url = "http://103.241.181.36:8080/FleetAndrProject/CurrentPosition?typevalue="
//                    String url = "http://192.168.2.26:8080/AndrFleetApp1/CurrentPosition?typevalue="
//                    String url = "http://192.168.2.26:8080/FleetAndrProject/CurrentPosition?typevalue="

                    Log.e("TM", " TYPE OF USER " + typeuser);

                    Log.e("TM", " TYPE OF USER " + typeuser[0] + " TYPE OG USER 2" + typeuser[1]);

                    /*String url = "http://103.241.181.36:8080/AndrFleetApp3/CurrentPosition?typevalue="
                            + typeuser[0] + "&TypeofUser=" + typeuser[1] + "&username=" + email;*/
                    String url = "http://103.241.181.36:8080/AndrFleetApp4/CurrentPosition?typevalue=" + typeuser[0] + "&TypeofUser=" + typeuser[1] + "&username=" + email;

                    //12057 $ Espalier


                    url = url.replaceAll(" ", "%20");
                    Log.e("MainActivity", "url" + url);
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpGet httpGet = new HttpGet(url);
                    HttpResponse execute = client.execute(httpGet);
                    InputStream content = execute.getEntity().getContent();

                    BufferedReader buffer = new BufferedReader
                            (
                                    new InputStreamReader(content));

                    Log.e("TM", " RESOPNSE FROM SERVLET ");

                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }
                    Log.e("TM", "RESPONSE FROM CP SERVLET " + response);
                    Log.e("The response =>", response);
//                            Thread.sleep(2000);

                } catch (Exception e) {
                    Log.e("Exception occured!!", e.getMessage());
                    return false;
                }

                if (!(nameOfUser.equals("No_Data"))) {
                    Log.e("TM", "RESPONSE IS EQUAL TO DATA");


                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Password, password);
                    editor.putString(Email, email);
                    editor.commit();


                    try {
                        Log.e("response", response);
                        String[] str = response.split("\\$");
                        publishProgress(str);
                        Log.e("str", String.valueOf(str));
                        Log.e("TM", "RESULT" + response);
                        USERNAME = nameOfUser.split("#");
                        Log.e("TM", "NAME" + USERNAME);


                    } catch (Exception e) {

                        e.printStackTrace();


                    }
                    return true;
                } else {

//                    mEmailView.setError(" Enter the valid Username ");
//                    mPasswordView.setError(" Enter the valid Password ");


//                    finish();
//
//                    Intent intent=new Intent(LoginActivity.this, LoginActivity.class);
//                    startActivity(intent);

                    showProgress(false);

//                    SharedPreferences preferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = preferences.edit();
//                    editor.clear();
//                    editor.commit();
//
//                    mEmailView.setError(" Enter the valid Username ");
//                   mPasswordView.setError(" Enter the valid Password ");


                    Toast.makeText(LoginActivity.this, "Invalid user name or password", Toast.LENGTH_LONG).show();


                    return false;
                }

            } else {

//                SharedPreferences preferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.clear();
//                editor.commit();
                showProgress(false);

                Toast.makeText(LoginActivity.this, "Invalid user name or password", Toast.LENGTH_LONG).show();
//
//
//                mEmailView.setError(" Enter the valid Username ");
//                   mPasswordView.setError(" Enter the valid Password ");

//                finish();
//
//                Intent intent=new Intent(LoginActivity.this, LoginActivity.class);
//                startActivity(intent);
//
//                mEmailView.setError(" Enter the valid Username ");
//                mPasswordView.setError(" Enter the valid Password ");
                return false;

            }

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            Log.i("TM", " VALUES " + values);


            if (values.length == 2) {

                vcode = values[0];
                vnum = values[1];
                //status = values[2];

                Log.e("MainActivity", "vcode" + vcode);
               /* Log.e("MainActivity", "vnum" + vnum);
                Log.e("MainActivity", "status" + status);
*/
                SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("vcode", vcode); // Storing string
                editor.putString("vnum", vnum);
                // editor.putString("status",status);
                editor.commit();

                // new RouteFinderReq().execute();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
                finish();


//                new CurrentPositonRequest().execute();

            } else {
                showProgress(false);

//                mEmailView.setError("Please Enter Valid Username samir");
//                mPasswordView.setError("Please Enter Valid Password samir");


                SharedPreferences preferences = getSharedPreferences("mypref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();

                String info = " Check Your Credential";
                Toast toast = Toast.makeText(getApplicationContext(), Html.fromHtml("<font color='#e3f2fd' ><b>" + info + "</b></font>"), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.DISPLAY_CLIP_HORIZONTAL, 0, 0);
                toast.show();
                finish();

                Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.this);
        builder1.setIcon(R.drawable.appicon);
        builder1.setMessage("Do you want to exit");
        builder1.setTitle("Safe2School Application");
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                        homeIntent.addCategory(Intent.CATEGORY_HOME);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(homeIntent);
                        Log.e("inside if", "if");
                        System.exit(0);
                        finish();

                   /*    ActivityCompat.finishAffinity(LoginActivity.this);// Close all activites
                       System.exit(0);*/

                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public int generateRandomNumber() {
        int randomNumber;
        int range = 9;  // to generate a single number with this range, by default its 0..9
        int length = 4; // by default length is 4
        SecureRandom secureRandom = new SecureRandom();
        String s = "";
        for (int i = 0; i < length; i++) {
            int number = secureRandom.nextInt(range);
            if (number == 0 && i == 0) { // to prevent the Zero to be the first number as then it will reduce the length of generated pin to three or even more if the second or third number came as zeros
                i = -1;
                continue;
            }
            s = s + number;
        }
        randomNumber = Integer.parseInt(s);
        return randomNumber;
    }

    public void showForgetPswdDialog() {
        // flag=false;
       // AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final AlertDialog optionDialog = new AlertDialog.Builder(this).create();
        final Dialog dialog = new Dialog(LoginActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.forget_pswd_dialog, null);
        optionDialog.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.reg_number);
        Button btnVarify;
        btnVarify = (Button) dialogView.findViewById(R.id.submit_button);
        btnVarify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobNo=edt.getText().toString();
                Log.e("MobNo",mobNo);
                SharedPreferences sh_pref = getApplicationContext().getSharedPreferences("mobNo", MODE_PRIVATE);
                SharedPreferences.Editor editor = sh_pref.edit();
                editor.putString("mobileNo",mobNo); // Storing integer
                editor.commit();
                if (edt.getText().toString().equals("")){
                    edt.setError("Enter Registered Mobile No.");
                }else
/*

                Intent i=new Intent(LoginActivity.this,UserForgetPassword.class);
                startActivity(i);
*/

               /* OTP = generateRandomNumber();
                Log.e("OTP", String.valueOf(OTP));
                SharedPreferences.Editor editor = pref.edit();
                editor.putInt("otp", OTP); // Storing integer
                editor.commit();*/
                //showOTPDialog();
                try {
                   /* JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.GET,
                            "http://192.168.1.115:6060/FWebservice/rest/ForgetPassword?MobNo=7083002657&OTP="+OTP+"&format=json",
                            //"http://103.241.181.36:8080/VehSummary/rest/UserRegistration?Username=" + userName + "&EmailId=" + emailID + "&MobileNo=" + mobileNo + "&Address=" + address + "&CompanyCode=" + compCode + "&imeiNo=" + imeiNo + "&OTP=" + OTP + "&format=json",
                            // "http://192.168.2.124:6060/FWebservice/rest/UserRegistration?Username=" + userName + "&EmailId=" + emailID + "&MobileNo=" + mobileNo + "&Address=" + address + "&CompanyCode=" + compCode + "&imeiNo=" + imeiNo + "&OTP=" + OTP + "&format=json",
                            new JSONObject(),
                            new Response.Listener<JSONObject>() {
                                public void onResponse(JSONObject response) {
                                    String s=response.toString();
                                    Toast.makeText(getApplicationContext(), "Data posted", Toast.LENGTH_SHORT).show();
                                    Log.e("Exception while 1", "Sending data to server : "+s);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //  Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
                            Log.e("Exception while 2", "Sending data to server : "+error);
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                    Log.e("jsonObjectRequest", String.valueOf(jsonObjectRequest));
                   // if (response.equals("ok"))
                    showOTPDialog();*/
                    OTP = generateRandomNumber();
                    final JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(
                            Request.Method.GET,
                            "http://103.241.181.36:8080/FleetForgetPassword/rest/ForgetPassword?MobNo="+edt.getText().toString()+"&OTP="+OTP+"&format=json",
                            new JSONArray(),
                            new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                   // OTP = generateRandomNumber();
                                    String url="http://103.241.181.36:8080/FleetForgetPassword/rest/ForgetPassword?MobNo="+edt.getText().toString()+"&OTP="+OTP+"&format=json";
                                    String s=response.toString();
                                    Log.e("s",s);
                                    Log.e("url",url);
                                    try {
                                        for (int i = 0; i < response.length(); i++) {
                                            JSONObject objectjsn = response.getJSONObject(i);
                                            String result = objectjsn.getString("Output");
                                            Log.e("Unit ID & code Response", "" + result);
                                            if(result.equals("Yes")){
                                                //OTP = generateRandomNumber();
                                                Log.e("OTP", String.valueOf(OTP));
                                                SharedPreferences.Editor editor = pref.edit();
                                                editor.putInt("otp", OTP); // Storing integer
                                                editor.commit();
                                                optionDialog.dismiss();
                                                showOTPDialog();

                                            }else if(result.equals("No")){
                                                edt.setError("Enter Registered mobile No.");
                                            }
                                        }
                                    } catch (JSONException e) {
                                       // new MyLogger().storeMassage("Register Activity : Exception while updating userDetails ", e.getMessage());
                                        Log.e("Exception 3453 ", " : " + e.getMessage());
                                        Toast.makeText(LoginActivity.this, "check internet Connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(LoginActivity.this, "Server not reachable....Try Later", Toast.LENGTH_SHORT).show();
                                    Log.e("Exception ", "while url hitting" + error);//NoConnectionError
                                }
                            }
                    );
                    requestQueue.add(jsonarrayRequest);
                } catch (Exception e) {
                    Log.e("Exception while ", "Sending data to server : " + e.getMessage());
                    Toast.makeText(LoginActivity.this, "Oops....Registration Failed Try Later.", Toast.LENGTH_LONG).show();
                }
                //showOTPDialog();

            }
        });


      //  AlertDialog b = dialogBuilder.create();
        optionDialog.show();
        // finish();
    }
    public void showOTPDialog() {
        // flag=false;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final Dialog dialog = new Dialog(LoginActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custum_otp_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.edit2);
        Button btnVarify;
        btnVarify = (Button) dialogView.findViewById(R.id.submit);
        btnVarify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int getOTPFromUser = Integer.parseInt(edt.getText().toString());
                int getOTPFromDB = pref.getInt("otp", 0); // getting String
                Log.e("getOTPFromDB", String.valueOf(getOTPFromDB));

              //  Log.e("OTP from you "+getOTPFromUser,"OTP from DB "+getOTPFromDB);
                //if(getOTPFromUser=){
                if (getOTPFromUser == getOTPFromDB) {
                    Toast.makeText(LoginActivity.this, "Successfully Verified!!!", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(LoginActivity.this,UserForgetPassword.class);
                    startActivity(i);
                    dialog.dismiss();
                 //   GetJSONData3();

                } else {
                    Toast.makeText(LoginActivity.this, "Please enter Valid OTP........", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
        // finish();
    }

    private BroadcastReceiver reciever =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                //your_edittext.setText(message);
                Log.e("SMS ",": "+","+message+",");
                Log.e("OTP ",","+OTP+",");
                String otp1 =String.valueOf(OTP);
                Log.e("OTP1",otp1);

                if(message.equals(otp1)){
                   Intent i=new Intent(LoginActivity.this,UserForgetPassword.class);
                   startActivity(i);
                    Log.e("Successfully ","Varified");
                    Toast.makeText(LoginActivity.this, "Successfully Verified!!!", Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(reciever);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(reciever, new IntentFilter("otp"));
    }


}


