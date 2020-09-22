package com.example.patrinavarro.smartsafe;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TransactionActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView title;
    ImageView categoryLogoImageView;
    TextView categoryTextView;
    EditText amount;
    EditText expenseDate;
    Calendar selectedDate;

    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        // Instantiate the RequestQueue: A Queue containing the Network/HTTP Requests that needs to be made.
        queue = Volley.newRequestQueue(this);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TITLE DESCRIPTION___________________________________________________________________________________________________________________________
        title = findViewById(R.id.titleEditText);

        //CATEGORY SELECTION__________________________________________________________________________________________________________________________
        //We identify the elements on the view
        categoryLogoImageView = (ImageView) findViewById(R.id.categoryLogoImageView);
        categoryTextView = (TextView) findViewById(R.id.category_textView);
        final Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        //Remember the adapter is the bridge between the spinner and its data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinnerItems, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                change_imageView(parentView,position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        //AMOUNT___________________________________________________________________________________________________________________________
        amount = findViewById(R.id.amountEditText);
        //DATE SELECTION_______________________________________________________________________________________________________________________________
        expenseDate = (EditText) findViewById(R.id.dateEditText);
        //By default show the current date
        selectedDate = Calendar.getInstance();
        updateLabel();

        //The set date listener must be always accessible to grab its info
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        //When the editText is clicked we want a new dialog to appear with the settings from before
        expenseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dialog = new DatePickerDialog(view.getContext(), date, selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMaxDate(new Date().getTime());
                dialog.show();
            }
        });

        //SAVING TO DATABASE__________________________________________________________________________________________________________________________
        Button saveBut = (Button)findViewById(R.id.save_button);
        saveBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("smartsafe", MODE_PRIVATE);
                final int userID = preferences.getInt("userID",-1);
                if(userID == -1) {
                    Log.d("ERROR", "UserID stored in shared preferences: -1");
                    return;
                }

                final String title_txt = title.getText().toString();
                final String category_txt = spinner.getSelectedItem().toString();
                final float amount_txt = Float.parseFloat(amount.getText().toString());
                SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                final String date_txt = sqlDateFormat.format(selectedDate.getTime());
                request_insert(userID,title_txt,category_txt,amount_txt,date_txt);

                go_back();
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        String label="";
        if(selectedDate.equals(Calendar.getInstance())) label = "(Today) ";
        expenseDate.setText(label+sdf.format(selectedDate.getTime()));
    }

    public void change_imageView(AdapterView<?> parent,int pos) {
        // An item was selected. You can retrieve the selected item using
        //By default shows:
        String category_logo="abstract_logo_azul";
        Log.d("HELP",parent.getItemAtPosition(pos).toString());
        CategoryLogoSelector selector = new CategoryLogoSelector(this);
        //LOADING IMAGES WITH GLIDE
        Glide.with(this).load(selector.getImageId(parent.getItemAtPosition(pos).toString())).into(categoryLogoImageView);
    }

    public void request_insert(final int userID, final String title_txt, final String category_txt, final float amount_txt, final String date_txt)
    {
        String url ="http://api.a17-sd510.studev.groept.be/addTransaction/"+userID+"/"+title_txt+"/"+category_txt+"/"+amount_txt+"/"+date_txt;

        Log.d("URL", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { //if no error occurs, the response is transformed to a string
                        //No response for an insert
                        Log.d("APP", response);
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

    private void go_back()
    {
        //Call the order_review activity to display. Create an intention an specify whereto do you want to navigate
        Intent intent = new Intent(this, MainActivity.class);
        //Add all the extras you need
        startActivity(intent);
    }
}
