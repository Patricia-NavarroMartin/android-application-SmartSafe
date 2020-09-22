package com.example.patrinavarro.smartsafe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Patri Navarro on 01/05/2018.
 */

public class Tab2_fragment  extends Fragment {

    //    HashMap<String,Float> category_amount;
    private ArrayList<String> title_listV;
    private ArrayList<String> category_listV;
    private ArrayList<String> date_listV;
    private ArrayList<Float> amount_listV;

    private ArrayList<String> category_list;
    private ArrayList<Float> amount_list;
    float totalBalance;
    private RequestQueue queue;
    private PieChart pieChart;
    private ListView lv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflater: used to inflate/add a view to a viewgroup taking into account the container measurements
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);
        // Instantiate the RequestQueue: A Queue containing the Network/HTTP Requests that needs to be made.
        queue = Volley.newRequestQueue(this.getContext());
        lv=(ListView) view.findViewById(R.id.tab2_listview);
        title_listV =new ArrayList<>();
        category_listV = new ArrayList<>();
        date_listV = new ArrayList<>();
        amount_listV = new ArrayList<>();

        category_list = new ArrayList<>();
        amount_list = new ArrayList<>();
        //We create a new piechart with the data gathered in form of Arrays of float and Arraylist of Strings
        pieChart = (PieChart) view.findViewById(R.id.tab2_pieChart);

        //We gather all the information from the database
        extract_fromDB();

        //We set the viewList with the data too
        return view;
    }
    private void extract_fromDB() {
        SharedPreferences preferences = getActivity().getSharedPreferences("smartsafe", MODE_PRIVATE);
        final int userID = preferences.getInt("userID",-1);
        if(userID == -1) {
            Log.d("ERROR", "UserID stored in shared preferences: -1");
            return;
        }
        SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        final String date_txt = sqlDateFormat.format(c.getTime());


        Log.d("HELP","Edit text to string done");
        Log.d("HELP","User: " +userID);
        Log.d("HELP","Date: " + date_txt);
        requestPieChartData(userID,date_txt);
        requestListViewData(userID,date_txt);
    }

    public void requestPieChartData(final int userID, final String date_txt)
    {
        String url ="http://api.a17-sd510.studev.groept.be/getTotalPerCategory/"+userID+"/"+date_txt;
        Log.d("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        //No response for an insert
                        Log.d("APP-PieChart", response);
                        parsePieChartDataResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR",error.toString());
                        Toast.makeText(getView().getContext(), "Network failed", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(stringRequest);
    }
    private void parsePieChartDataResponse(String response) {

        try {
            JSONArray data = new JSONArray(response);
            int n = data.length();
            totalBalance=0;
            category_list.clear();
            amount_list.clear();
            Log.d("HELP","Number of elements to process for piechart:"+n);
            if(n==0)return;
            for (int i = 0; i < n; ++i) {
                JSONObject category_object = data.getJSONObject(i);
                category_list.add(category_object.getString("category"));
                amount_list.add((float) category_object.getDouble("total"));
                totalBalance+= amount_list.get(i);
                Log.d("HELP","Storing in position "+i+" "+ category_list.get(i)+" "+ amount_list.get(i));
            }
            Log.d("BEFORE", "SIZE OF CATEGORY LIST: "+ category_list.size()+"\n SIZE OF AMOUNT LIST:"+ amount_list.size());
            //Draw the pie chart
            PieChartConfig config = new PieChartConfig(pieChart, category_list, amount_list,totalBalance);
            pieChart=config.getPieChart();
            pieChart.notifyDataSetChanged();
            pieChart.invalidate();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void requestListViewData(final int userID, final String date_txt)
    {
        String url ="http://api.a17-sd510.studev.groept.be/getExpensesDetails/"+userID+"/"+date_txt;
        Log.d("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        //No response for an insert
                        Log.d("APP-ListView", response);
                        parseListViewDataResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ERROR",error.toString());
                        Toast.makeText(getView().getContext(), "Network failed", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(stringRequest);
    }
    private void parseListViewDataResponse(String response) {

        try {
            JSONArray data = new JSONArray(response);
            int n = data.length();
            title_listV.clear();
            category_listV.clear();
            date_listV.clear();
            amount_listV.clear();
            Log.d("HELP","Number of elements to process for listview:"+n);
            for (int i = 0; i < n; ++i) {
                JSONObject expense_object = data.getJSONObject(i);
                title_listV.add(expense_object.getString("title"));
                category_listV.add(expense_object.getString("category"));
                date_listV.add(expense_object.getString("full_date"));
                amount_listV.add((float) expense_object.getDouble("amount"));
                Log.d("HELP","Storing in position "+i+" "+ title_listV.get(i)+" "+ amount_listV.get(i)+ date_listV.get(i)+" "+ amount_listV.get(i));
            }
            //Add to the listview
            lv.setAdapter(new CustomAdapter(getContext(),n,title_listV,category_listV,amount_listV,date_listV));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
