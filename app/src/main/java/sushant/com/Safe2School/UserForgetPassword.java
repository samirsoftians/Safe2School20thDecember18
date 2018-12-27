


package sushant.com.Safe2School;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class UserForgetPassword extends AppCompatActivity {
    TextView newPassword,confirmPassword,error;
    Button resetPassword;
    String strNewPswd,strConfirmPswd;
    RequestQueue requestQueue;
    String mobNo;
    SharedPreferences sharedpreferences,sp;
    String vcode,vehNumber,email;


    //*****************************SHARRED PREFERENCES TO STORE DATA****
    public static final String mypreference = "mypref";
    public static final String Password = "nameKey";
    public static final String Email = "emailKey";
    //****************************END HERE*************************





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_forget_password);
        sp = getApplicationContext().getSharedPreferences("Username", MODE_PRIVATE); // 0 - for private mode

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        newPassword=(TextView)findViewById(R.id.newpswd);
        confirmPassword=(TextView)findViewById(R.id.confirmpswd);
        resetPassword=(Button)findViewById(R.id.btn_pswd_reset);
        error = (TextView)findViewById(R.id.TextView_PwdProblem);



        sharedpreferences = getApplication().getSharedPreferences(mypreference, Context.MODE_PRIVATE);

        /*sharedpreferences2 = getSharedPreferences("MyPref", Context.MODE_PRIVATE);

        sharedpreferences = getSharedPreferences("MyPref", Context.MODE_PRIVATE);*/
       // sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sharedpreferences.edit();
        //SharedPreferences.Editor ed1 =sp.edit();
        email=sharedpreferences.getString(Email,"");
        Log.e("uname",email);
        try {
            vcode = sharedpreferences.getString("vcode", null);
            Log.e("vcode", vcode);
            vehNumber = sharedpreferences.getString("vnum", null);
            Log.e("num", vehNumber);
        }catch (Exception e){
            Log.e("ss",e.getMessage());
        }
        ed.putString("vcode", vcode); // Storing string
        ed.putString("vnum", vehNumber);
        ed.commit();
        SharedPreferences sh_pref = getApplicationContext().getSharedPreferences("mobNo", MODE_PRIVATE);
        SharedPreferences.Editor editor = sh_pref.edit();
        mobNo=sh_pref.getString("mobileNo","");
        Log.e("mob",mobNo);
        confirmPassword.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                strNewPswd = newPassword.getText().toString();
                strConfirmPswd = confirmPassword.getText().toString();
                if (strNewPswd.equals(strConfirmPswd)) {
                    error.setText("Password Matched");
                } else {
                    error.setText("Password Not Matched");
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });


//*************CHANGES MADE HERE ***************



       /* SharedPreferences.Editor editor2 = sharedpreferences.edit();
        editor2.putString(Password, strConfirmPswd);
        editor2.putString(Email, email);
        editor2.commit();*/
//*******************ENDS HERE *******************

        //editor.commit();
        // ed1.putString("emailKey","shivi");//***************CHANGES HERE *******
       // ed1.putString("confirmpswd","shivi");//**************CHANGES HERE ***************
      //  ed1.commit();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (strNewPswd.equals(strConfirmPswd)) {
                    try {
                        Log.e("mob",mobNo);
                       /* SharedPreferences.Editor editor2 = sharedpreferences.edit();
                        email=sharedpreferences.getString(Email,"");
                        editor2.putString(Password, strConfirmPswd);
                        editor2.putString(Email, "7000467780");
                        editor2.commit();
                        Log.e("uname,pass",email+","+strConfirmPswd);*/
                        SharedPreferences.Editor editor2 = sharedpreferences.edit();
                        email=sharedpreferences.getString(Email,"");
                        editor2.putString(Password, strConfirmPswd);
                       editor2.putString(Email, email);
                        editor2.commit();
                        Log.e("uname,pass",email+","+strConfirmPswd);
                    /*try {
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                Request.Method.GET,
                                "http://103.241.181.36:8080/FleetForgetPassword/rest/UpdatePassword?MobNo="+mobNo+"&Password="+strConfirmPswd+"&format=json",
                                //"http://103.241.181.36:8080/VehSummary/rest/UserRegistration?Username=" + userName + "&EmailId=" + emailID + "&MobileNo=" + mobileNo + "&Address=" + address + "&CompanyCode=" + compCode + "&imeiNo=" + imeiNo + "&OTP=" + OTP + "&format=json",
                                // "http://192.168.2.124:6060/FWebservice/rest/UserRegistration?Username=" + userName + "&EmailId=" + emailID + "&MobileNo=" + mobileNo + "&Address=" + address + "&CompanyCode=" + compCode + "&imeiNo=" + imeiNo + "&OTP=" + OTP + "&format=json",
                                new JSONObject(),
                                new Response.Listener<JSONObject>() {
                                    public void onResponse(JSONObject response) {
                                        Toast.makeText(getApplicationContext(), "Data posted", Toast.LENGTH_SHORT).show();
                                        Log.e("Exception while 1", "Sending data to server : ");
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //  Toast.makeText(RegisterActivity.this, "Error", Toast.LENGTH_LONG).show();
                                Log.e("Exception while 2", "Sending data to server : ");
                            }
                        });
                        Log.e("ss","data sent");
                        requestQueue.add(jsonObjectRequest);
                    } catch (Exception e) {
                        Log.e("Exception while ", "Sending data to server : " + e.getMessage());
                        Toast.makeText(UserForgetPassword.this, "Oops....Reset Failed Try Later.", Toast.LENGTH_LONG).show();
                    }*/
                        final JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(
                                Request.Method.GET,
                                "http://103.241.181.36:8080/FleetForgetPassword/rest/UpdatePassword?MobNo="+mobNo+"&Password=" + strConfirmPswd + "&format=json",
                                new JSONArray(),
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        String s = response.toString();
                                        Log.e("s","http://103.241.181.36:8080/FleetForgetPassword/rest/UpdatePassword?MobNo=" + mobNo + "&Password=" + strConfirmPswd + "&format=json"+s);
                                        Intent i=new Intent(UserForgetPassword.this,LoginActivity.class);
                                        startActivity(i);
                                        Log.e("Login","login");
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(UserForgetPassword.this, "Server not reachable....Try Later", Toast.LENGTH_SHORT).show();
                                        Log.e("Exception ", "while url hitting" + error);//NoConnectionError
                                    }
                                }
                        );
                        requestQueue.add(jsonarrayRequest);

                    } catch(Exception e){
                        Log.e("Exception while ", "Sending data to server : " + e.getMessage());
                        Toast.makeText(UserForgetPassword.this, "Oops....Registration Failed Try Later.", Toast.LENGTH_LONG).show();
                    }
                    //showOTPDialog();

                }
                else {
                    confirmPassword.setError("Password Not Matched");
                    Log.e("Inside else","Else");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(UserForgetPassword.this,LoginActivity.class);
        startActivity(i);
        finish();
    }
}



