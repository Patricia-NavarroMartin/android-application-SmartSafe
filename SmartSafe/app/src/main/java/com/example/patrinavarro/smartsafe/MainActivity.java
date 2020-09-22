package com.example.patrinavarro.smartsafe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static  final String TAG = "MainActivity";
    /*
    Layout manager that allows the user to flip left and right through pages of data.
    You supply an implementation of a PagerAdapter to generate the pages that the view shows.

    ViewPager is most often used in conjunction with Fragment, which is a convenient way to supply
    and manage the lifecycle of each page.

    The ViewPager is a layout widget in which each child view is a separate page (a separate tab)
    in the layout.
     */
    private ViewPager mViewPager;
    /*
    To insert child views that represent each page, you need to hook this layout to a PagerAdapter.
    There are two kinds of adapter you can use:
     */
    private SectionsPageAdapter mSectionsPageAdapter;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPageAdapter = new SectionsPageAdapter(getSupportFragmentManager());
        //Set up the ViewPager with the section adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(mViewPager);

        //Finally we set the tab layout object and set the 'id' to tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

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


        FloatingActionButton addBut = (FloatingActionButton) findViewById(R.id.add);
        addBut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                newTransaction();
            }
        });

    }

    private void newTransaction()
    {
        //Call the activity to display. Create an intention an specify whereto do you want to navigate
        Intent intent = new Intent(this, TransactionActivity.class);
        startActivity(intent);
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

    //We need a section page adapter to add the fragments to it with titles
    //(just like adding it to a list)
    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());

        //We are just adding these to the list to keep track of the items and titles
        adapter.addFragment(new Tab1_fragment(),getResources().getString(R.string.tab_text_1));
        adapter.addFragment(new Tab2_fragment(),getResources().getString(R.string.tab_text_2));
        adapter.addFragment(new Tab3_fragment(),getResources().getString(R.string.tab_text_3));

        //Set the adapter to the viewpager
        viewPager.setAdapter(adapter);
    }
}
