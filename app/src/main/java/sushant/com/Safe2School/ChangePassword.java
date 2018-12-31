package sushant.com.Safe2School;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

public class ChangePassword extends AppCompatActivity {
    SharedPreferences sh_pref,sharedpreferences;
    String oldPassword,oldUsername;
    TextView tvOldPassword,tvOldUsername,tvChangeUsrName;
    TextView newPassword,confirmPassword,error;
    EditText newUsrName;
    String newUserName = "";
    Button changePassword;
    RequestQueue requestQueue;
    String strNewPswd,strConfirmPswd;
    String email,password;

    //*****************************SHARRED PREFERENCES TO STORE DATA****
    public static final String mypreference = "mypref";
    public static final String Password = "nameKey";
    public static final String Email = "emailKey";
    //****************************END HERE*************************



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);


        sharedpreferences = getApplication().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        sh_pref=getSharedPreferences("userlogin",Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sh_pref.edit();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        oldPassword= Preferences.getInstance(getApplicationContext()).getdata(Preferences.Password);
        oldUsername= Preferences.getInstance(getApplicationContext()).getdata(Preferences.Email);
//******************PREFERENCE CHANGES **********************
      /*  oldPassword=sharedpreferences.getString(Password,"");
        oldUsername=sharedpreferences.getString(Email,"");*/

      //****************ENDS HERE ***************************


        Log.e("old","msg"+oldUsername);
        newPassword=(TextView)findViewById(R.id.newpswd);
        confirmPassword=(TextView)findViewById(R.id.confirmpswd);
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
        error = (TextView)findViewById(R.id.TextView_PwdProblem);
        tvOldPassword=(TextView)findViewById(R.id.old_password);
        tvOldUsername=(TextView)findViewById(R.id.old_username);
        tvChangeUsrName=(TextView)findViewById(R.id.change_link);
        newUsrName=(EditText)findViewById(R.id.newusrname);
        changePassword=(Button) findViewById(R.id.btn_pswd_change);
        tvChangeUsrName.setPaintFlags(tvChangeUsrName.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvChangeUsrName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             newUsrName.setVisibility(View.VISIBLE);
             newUserName=newUsrName.getText().toString();
             Log.e("newUsrname",newUserName);
            }
        });
            tvOldPassword.setText(oldPassword);
            Log.e("oldPassword", oldPassword);
            Log.e("newUserED", String.valueOf(newUsrName));
            tvOldUsername.setText(oldUsername);

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserName=newUsrName.getText().toString();
                try {
                    if (strNewPswd.equals(strConfirmPswd)) {
                        try {
                            if (newUserName.equals("")) {

                                oldUsername= Preferences.getInstance(getApplicationContext()).getdata(Preferences.Email);


                                //************PREFERENCE
                                /*
                                oldUsername = sharedpreferences.getString(Email, "");
*/

                                //***********ENDS HERE ************
                                GetJSONData3(oldUsername, strConfirmPswd, oldUsername);
                                Log.e("inside if", "inside if1" + oldUsername);
                                Log.e("inside if", "inside if2" + newUserName);
                                Log.e("inside if", "inside if3" + strConfirmPswd);
                                /*SharedPreferences.Editor editor = sh_pref.edit();
                                editor.putString("username", oldUsername);
                                editor.putString("usrpswd", strConfirmPswd);
                                editor.commit();*/

                                //***********PREFERENCE CHANGES HERE *******
                               /* SharedPreferences.Editor editor2 = sharedpreferences.edit();
                                editor2.putString(Password, strConfirmPswd);
                                editor2.putString(Email, oldUsername);
                                editor2.commit();*/


                                Preferences.getInstance(getApplicationContext()).update(Preferences.Password,strConfirmPswd);
                                Preferences.getInstance(getApplicationContext()).update(Preferences.Email,oldUsername);


                                //*****************ENDS HERE **************
                                Toast.makeText(ChangePassword.this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                newUserName = newUsrName.getText().toString();
                                GetJSONData3(oldUsername, strConfirmPswd, newUserName);
                                Log.e("inside else", "inside else1" + oldUsername);
                                Log.e("inside else", "inside else2" + newUserName);
                                Log.e("inside else", "inside else3" + strConfirmPswd);
                               /* SharedPreferences.Editor editor = sh_pref.edit();
                                editor.putString("username", newUserName);
                                editor.putString("usrpswd", strConfirmPswd);
                                editor.commit();*/

                               //*************PEREFERENCE CHANGES HERE*********
                                /*SharedPreferences.Editor editor2 = sharedpreferences.edit();
                                editor2.putString(Password,strConfirmPswd);
                                editor2.putString(Email, newUserName);
                                editor2.commit();*/


                                Preferences.getInstance(getApplicationContext()).update(Preferences.Password,strConfirmPswd);

                                Preferences.getInstance(getApplicationContext()).update(Preferences.Email,newUserName);


                                //*************ends here *********************
                                Toast.makeText(ChangePassword.this, "Username and Password Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("e", e.getMessage());
                        }
                    } else {
                        confirmPassword.setError("Password Not Matched");
                        Log.e("Inside else", "Else");
                    }
                }catch (Exception e){
                    Log.e("Exception",e.getMessage());
                    confirmPassword.setError("Enter Password");
                }
            }
        });
    }

    public void GetJSONData3(String oldUsername,String newPswd,String newUsrNm) {
        try {
            final JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(
                    Request.Method.GET,
                    Url.reset+"UpdateUsername?OldUsername="+oldUsername+"&Password="+newPswd+"&Username="+newUsrNm+"&format=json",
                    new JSONArray(),
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            String s = response.toString();
                            Log.e("s",s);
                            Intent i=new Intent(ChangePassword.this,MainActivity.class);
                            startActivity(i);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ChangePassword.this, "Server not reachable....Try Later", Toast.LENGTH_SHORT).show();
                            Log.e("Exception ", "while url hitting" + error);//NoConnectionError
                        }
                    }
            );
            Log.e("s","http://103.241.181.36:8080/FleetForgetPassword/rest/UpdateUsername?OldUsername="+oldUsername+"&Password="+newPswd+"&Username="+newUsrNm+"&format=json");
            requestQueue.add(jsonarrayRequest);

        } catch(Exception e){
            Log.e("Exception while ", "Sending data to server : " + e.getMessage());
            Toast.makeText(ChangePassword.this, "Oops....Registration Failed Try Later.", Toast.LENGTH_LONG).show();
        }
        //showOTPDialog();


}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i=new Intent(ChangePassword.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}
