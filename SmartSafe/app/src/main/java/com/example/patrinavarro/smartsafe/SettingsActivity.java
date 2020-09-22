package com.example.patrinavarro.smartsafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SharedPreferences preferences;
    private TextView total_budget;
    private EditText leisure_budget;
    private EditText personal_budget;
    private EditText travel_budget;
    private EditText shopping_budget;
    private EditText other_budget;

    private ImageView leisure_img;
    private ImageView personal_img;
    private ImageView shopping_img;
    private ImageView travel_img;
    private ImageView other_img;

    private Double parsed_leisure;
    private Double parsed_personal;
    private Double parsed_shopping;
    private Double parsed_travel;
    private Double parsed_other;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // Instantiate the RequestQueue: A Queue containing the Network/HTTP Requests that needs to be made.
        queue = Volley.newRequestQueue(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        preferences = getSharedPreferences("smartsafe", MODE_PRIVATE);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = (TextView) headerView.findViewById(R.id.user_email);
        navUserName.setText(preferences.getString("userEmail",""));

        total_budget = findViewById(R.id.total_TextView);
        leisure_budget = findViewById(R.id.leisure_EditText);
        shopping_budget = findViewById(R.id.shopping_EditText);
        personal_budget = findViewById(R.id.personal_EditText);
        travel_budget = findViewById(R.id.travel_EditText);
        other_budget = findViewById(R.id.other_EditText);

        //LOADING IMAGES WITH GLIDE_________________________________________________________________________________________________________________
        leisure_img =findViewById(R.id.leisure_ImageView);
        Glide.with(this).load(new CategoryLogoSelector(this).getImageId("Leisure")).into(leisure_img);
        shopping_img=findViewById(R.id.shopping_ImageView);
        Glide.with(this).load(new CategoryLogoSelector(this).getImageId("Shopping")).into(shopping_img);
        personal_img=findViewById(R.id.personal_ImageView);
        Glide.with(this).load(new CategoryLogoSelector(this).getImageId("Personal")).into(personal_img);
        travel_img=findViewById(R.id.travel_ImageView);
        Glide.with(this).load(new CategoryLogoSelector(this).getImageId("Travel")).into(travel_img);
        other_img=findViewById(R.id.other_ImageView);
        Glide.with(this).load(new CategoryLogoSelector(this).getImageId("Other")).into(other_img);

        SharedPreferences preferences = getSharedPreferences("smartsafe", MODE_PRIVATE);
        final int userID = preferences.getInt("userID",-1);
        if(userID == -1) {
            Log.d("ERROR", "UserID stored in shared preferences: -1");
            return;
        }

        //LOADING FROM DATABASE LATEST SETTINGS_______________________________________________________________________________________________________
        Log.d("HELP","Loading latest settings from the database");
        request_read(userID);

        //SAVING TO DATABASE__________________________________________________________________________________________________________________________
        Button saveBut = (Button)findViewById(R.id.save_button);
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final double leisure_txt = select_insertedValue(parsed_leisure,leisure_budget.getText().toString());
                final double personal_txt = select_insertedValue(parsed_personal,personal_budget.getText().toString());
                final double shopping_txt = select_insertedValue(parsed_shopping,shopping_budget.getText().toString());
                final double travel_txt = select_insertedValue(parsed_travel,travel_budget.getText().toString());
                final double other_txt = select_insertedValue(parsed_other,other_budget.getText().toString());

                request_insert(view,userID,leisure_txt,personal_txt,shopping_txt,travel_txt,other_txt);
                //We add the numerical values and edit the total
                double total = leisure_txt+personal_txt+shopping_txt+travel_txt+other_txt;
                total_budget.setText(String.format("%.2f", total) + "€");
            }
        });
    }

    public void request_insert(final View v,final int userID, final double leisure_txt, final double personal_txt,final double shopping_txt,final double travel_txt,final double other_txt)
    {
        String url ="http://api.a17-sd510.studev.groept.be/saveSettings/"+userID+"/"+leisure_txt+"/"+personal_txt+"/"+shopping_txt+"/"+travel_txt+"/"+other_txt;

        //To debug and see in the Logcat (put in Debug mode)
        Log.d("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        //No response for an insert
                        Log.d("APP", response);
                        //show snackbar to confirm changes saved;
                        //Snackbar mySnackbar = Snackbar.make(v, R.string.saved_correctly, Snackbar.LENGTH_LONG);
                        //mySnackbar.show();
                        Toast.makeText(getApplicationContext(), R.string.saved_correctly, Toast.LENGTH_LONG).show();
                        //reload view so that the saved values appear as hint
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                startActivity(getIntent());
                            }
                        }, 2000);

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

    public void request_read(final int userID)
    {
        String url ="http://api.a17-sd510.studev.groept.be/getSettings/"+userID+"/"+userID;
        Log.d("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        //No response for an insert
                        Log.d("APP", response);
                        //Parse response
                        parseResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR",error.toString());
                        //stop executing
                    }
                });

        queue.add(stringRequest);
    }
    private void parseResponse(String response) {

        try {
            JSONArray data = new JSONArray(response);

            JSONObject user = data.getJSONObject(0);
            parsed_leisure = user.getDouble("leisure");
            parsed_personal = user.getDouble("personal");
            parsed_shopping = user.getDouble("shopping");
            parsed_travel = user.getDouble("travel");
            parsed_other = user.getDouble("other");

            //SHOW CURRENT BUDGET AS THE EDITTEXT'S HINT TEXT
            leisure_budget.setHint(String.format("%.2f", parsed_leisure) + "€");
            personal_budget.setHint(String.format("%.2f", parsed_personal) + "€");
            travel_budget.setHint(String.format("%.2f", parsed_travel) + "€");
            shopping_budget.setHint(String.format("%.2f", parsed_shopping) + "€");
            other_budget.setHint(String.format("%.2f", parsed_other) + "€");


            double total = parsed_leisure+parsed_personal+parsed_travel+parsed_shopping+parsed_other;
            total_budget.setText(String.format("%.2f", total) + "€");

            String msg = "Latest settings successfully loaded from database";
            Log.d("APP",msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private double select_insertedValue(Double hintValue,String currentValue)
    {
        if(currentValue.equals(""))return hintValue;
        else return Double.parseDouble(currentValue);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_overview) {
            //Call the order_review activity to display. Create an intention an specify whereto do you want to navigate
            Intent intent = new Intent(this, MainActivity.class);
            //Add all the extras you need
            startActivity(intent);
        } else if (id == R.id.nav_budget) {
            //Call the order_review activity to display. Create an intention an specify whereto do you want to navigate
            Intent intent = new Intent(this, BudgetActivity.class);
            //Add all the extras you need
            startActivity(intent);

        } else if (id == R.id.nav_settings) {
            //Call the order_review activity to display. Create an intention an specify whereto do you want to navigate
            Intent intent = new Intent(this, SettingsActivity.class);
            //Add all the extras you need
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("rememberMe",false);
            editor.apply();
            //Call the order_review activity to display. Create an intention an specify whereto do you want to navigate
            Intent intent = new Intent(this, LoginActivity.class);
            //Add all the extras you need
            startActivity(intent);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
