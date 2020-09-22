package com.example.patrinavarro.smartsafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.HorizontalBarChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class BudgetActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private HorizontalBarChart total_barChart;

    private SharedPreferences preferences;
    private RequestQueue queue;

    private CategoryBudget total_category;
    private CategoryBudget leisure_category;
    private CategoryBudget travel_category;
    private CategoryBudget personal_category;
    private CategoryBudget shopping_category;
    private CategoryBudget other_category;
    private ArrayList<CategoryBudget> categories;

    private String date_txt;
    private int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        // Instantiate the RequestQueue: A Queue containing the Network/HTTP Requests that needs to be made.
        queue = Volley.newRequestQueue(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        preferences = getSharedPreferences("smartsafe", MODE_PRIVATE);
        View headerView = navigationView.getHeaderView(0);
        TextView navUserName = (TextView) headerView.findViewById(R.id.user_email);
        navUserName.setText(preferences.getString("userEmail",""));

        //Initialize the categories: name and current value 0
        categories = new ArrayList<>();
        createAndInsertAllCategories();

        total_barChart = findViewById(R.id.total_barchart);

        SharedPreferences preferences = getSharedPreferences("smartsafe", MODE_PRIVATE);
        userID = preferences.getInt("userID",-1);
        if(userID == -1) {
            Log.d("ERROR", "UserID stored in shared preferences: -1");
            return;
        }
        SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        final String date_txt = sqlDateFormat.format(c.getTime());

        //Get the data: monthly budget (to know the limits) and current consumption
        Log.d("HELP","Requesting settings");
        request_Settings(userID);
    }

    private void createAndInsertAllCategories() {
        leisure_category = new CategoryBudget("Leisure");
        leisure_category.setCurrent(0f);
        categories.add(leisure_category);

        other_category = new CategoryBudget("Other");
        other_category.setCurrent(0f);
        categories.add(other_category);

        shopping_category = new CategoryBudget("Shopping");
        shopping_category.setCurrent(0f);
        categories.add(shopping_category);

        travel_category = new CategoryBudget("Travel");
        travel_category.setCurrent(0f);
        categories.add(travel_category);

        personal_category = new CategoryBudget("Personal");
        personal_category.setCurrent(0f);
        categories.add(personal_category);

        total_category = new CategoryBudget("Total");
        total_category.setCurrent(0f);
        categories.add(total_category);
    }

    public void request_Settings(final int userID)
    {
        //Query the email to check if it is already stored in the db
        String url ="http://api.a17-sd510.studev.groept.be/getSettings/"+userID+"/"+userID;

        //To debug and see in the Logcat (put in Debug mode)
        Log.d("URL", url);

        // Request a string response from the provided URL (HTTP Request where the response is parsed a String)
        //public StringRequest(int method, String url, Listener<String> listener (invoked on success),ErrorListener errorListener)
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
                        Toast.makeText(getApplicationContext(), "Network failed", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(stringRequest);
    }
    private void parseResponse(String response) {
        /*
            JSON stands for JavaScript Object Notation, and it is based on a subset of JavaScript.
            Using the org.json library.
        */
        try {
            JSONArray data = new JSONArray(response);
            JSONObject user = data.getJSONObject(0);

            double total_limit=0;
            for(CategoryBudget cat:categories)
            {
                if(cat.getCategoryName().equals("Total"))continue;
                else cat.setLimit(user.getDouble(cat.getCategoryName().toLowerCase()));
                total_limit+=cat.getLimit();
            }
            //Once we have got all the categories' limit added we store total's one
            categories.get(categories.size()-1).setLimit(total_limit);

            String msg = "Latest settings successfully loaded from database";
            Log.d("APP",msg);

            //Once we have the limits we get the current values
            Log.d("HELP","Requesting current data");
            requestCurrentData(userID,date_txt);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestCurrentData(final int userID, final String date_txt)
    {
        //Query the email to check if it is already stored in the db
        String url ="http://api.a17-sd510.studev.groept.be/getTotalPerCategory/"+userID+"/"+date_txt;
        //To debug and see in the Logcat (put in Debug mode)
        Log.d("URL", url);
        // Request a string response from the provided URL (HTTP Request where the response is parsed a String)
        //public StringRequest(int method, String url, Listener<String> listener (invoked on success),ErrorListener errorListener)
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        //No response for an insert
                        Log.d("APP-BarChart", response);
                        parseCurrentDataResponse(response);
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
    private void parseCurrentDataResponse(String response) {
        /*
            JSON stands for JavaScript Object Notation, and it is based on a subset of JavaScript.
            Using the org.json library.
        */
        try {
            JSONArray data = new JSONArray(response);
            int n = data.length();
            float totalBalance=0;

            Log.d("HELP","Number of elements to process for barchart:"+n);
            if(n==0)return;
            Log.d("HELP","Entering for loop to get data from each object");
            //We first insert into the arraylist all the current categories and amount
            for (int i = 0; i < n; ++i) {
                JSONObject category_object = data.getJSONObject(i);
                float current = (float) category_object.getDouble("total");
                categories.get(getIndexOfCategoryName(category_object.getString("category"))).setCurrent(current);
                totalBalance+=current;
            }
            categories.get(categories.size()-1).setCurrent(totalBalance);
            for(CategoryBudget cat:categories)
            {
                float percentage = (float)(cat.getCurrent()/cat.getLimit())*100;
                if(percentage>100)cat.setPercentage(100);
                else cat.setPercentage(percentage);

            }

            BarChartConfig config = new BarChartConfig(this,total_barChart, categories);
            total_barChart=config.getBarChart();
            total_barChart.notifyDataSetChanged();
            total_barChart.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    int getIndexOfCategoryName(String categoryName)
    {
        int length = categories.size();
        for(int i=0; i<length;i++)
        {
            if(categories.get(i).getCategoryName().equals(categoryName))return i;
        }
        return -1;
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
