package com.example.patrinavarro.smartsafe;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private RequestQueue queue;
    private int currentUserID;
    private CheckBox rememberMe;
    private boolean correctCombo = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Instantiate the RequestQueue: A Queue containing the Network/HTTP Requests that needs to be made.
        queue = Volley.newRequestQueue(this);
        //Check shared preferences to configure depending on the checkbox "Remember me"

        //We get the data from the view.
        email = findViewById(R.id.email_item);
        password = findViewById(R.id.password_item);
        rememberMe = findViewById(R.id.checkbox);

        SharedPreferences preferences = getSharedPreferences("smartsafe", MODE_PRIVATE);
        if(preferences.getBoolean("rememberMe",false) )
        {
            //Call the order_review activity to display. Create an intention an specify whereto do you want to navigate
            Intent intent = new Intent(this, MainActivity.class);
            //Add all the extras you need
            startActivity(intent);
        }

        //We set the action done by the button depending on if the user
        //already exists in the database or not
        Button signInBut = (Button) findViewById(R.id.done_btn);
        signInBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String email_txt = email.getText().toString();
                final String password_txt = password.getText().toString();

                Log.d("HELP","Edit text to string done");
                Log.d("HELP",email_txt + " " + password_txt);

                sign_in_up(email_txt,password_txt);

                //if everything went fine it returns without errors and we go to the next activity
                if(correctCombo)load_data();
            }
        });
    }

    public void sign_in_up(final String email_txt, final String password_txt)
    {
        //Query the email to check if it is already stored in the db
        String url ="http://api.a17-sd510.studev.groept.be/searchUser/" + email_txt;
        Log.d("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        // Parse data from the response string
                        Log.d("APP", response);
                        getParseResponse(response,email_txt,password_txt);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR",error.toString());
                        Toast.makeText(getApplicationContext(), "Network failed", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
    }

    private void getParseResponse(String response, final String email_txt, final String password_txt)
    {
        try {
            JSONArray data = new JSONArray(response);
            int n = data.length();
            //There should only be one email per user so
            if(n>1)Log.d("ERROR","There are more than one objects with the same email");
            else if (n==1)
            {
                email.setBackgroundColor(getColor(R.color.colorPrimaryTrans));
                email.setTextColor(getColor(R.color.colorWhite));
                Log.d("APP", "Existing user...checking password");
                //check password
                JSONObject user = data.getJSONObject(0);
                String parsed_pwd = user.getString("password");
                if(parsed_pwd.equals(password_txt))
                {
                    Log.d("APP","Password okay.");
                    password.setBackgroundColor(getColor(R.color.colorPrimaryTrans));
                    correctCombo=true;
                }
                else
                {
                    Log.d("APP","Wrong password.");
                    password.setBackgroundColor(getResources().getColor(R.color.colorError));
                }
                password.setTextColor(getResources().getColor(R.color.colorWhite));
                currentUserID = user.getInt("id");
                String msg = "User ID = "+currentUserID;
                Log.d("APP",msg);
            }
            else
            {
                //create new user
                Log.d("APP", "No existing user...creating new");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.confirmation_dialog)
                        .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                createNewUser(email_txt,password_txt);
                            }
                        })
                        .setNegativeButton(R.string.no_dialog, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                dialog.cancel();
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createNewUser(final String email_txt, final String password_txt) {
        String url ="http://api.a17-sd510.studev.groept.be/newUser/" + email_txt +"/" + password_txt;
        Log.d("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        getUserId(email_txt);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR",error.toString());
                        Toast.makeText(getApplicationContext(), "Network failed", Toast.LENGTH_SHORT).show();
                    }
                });
        //We create the new user
        queue.add(stringRequest);
        //We need to get the userID from the database so after creating the user we sign it with it
        Log.d("APP","New user created. Signing in...");

    }

    void getUserId(final  String email)
    {
        String url ="http://api.a17-sd510.studev.groept.be/getUserId/" + email;
        Log.d("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        try {
                            JSONArray data = new JSONArray(response);
                            int n = data.length();
                            //There should only be one email per user so
                            if(n>1)Log.d("ERROR","There are more than one objects with the same email");
                            else if (n==1)
                            {
                                JSONObject user = data.getJSONObject(0);
                                currentUserID = user.getInt("id");

                                load_data();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR",error.toString());
                        Toast.makeText(getApplicationContext(), "Network failed", Toast.LENGTH_SHORT).show();
                    }
                });
        //We get new user's id
        queue.add(stringRequest);
    }

    public void load_data()
    {
        //We store the data to shared preferences before going to the next activity
        //Check shared preferences to configure depending on the checkbox "Remember me"
        SharedPreferences preferences = getSharedPreferences("smartsafe", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("userID", currentUserID);
        editor.putBoolean("rememberMe",rememberMe.isChecked());
        editor.putString("userEmail",email.getText().toString());
        Log.d("HELP","Applying userID:"+currentUserID);
        editor.apply();

        //Call the order_review activity to display. Create an intention an specify whereto do you want to navigate
        Intent intent = new Intent(this, MainActivity.class);
        //Add all the extras you need
        startActivity(intent);
    }



}


