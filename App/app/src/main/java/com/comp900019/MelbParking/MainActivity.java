package com.comp900019.MelbParking;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.libraries.places.api.Places;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity
    implements AppointmentAdapter.OnAppointmentSelectedListener {

    private static final String TAG = "main_activity";
    private boolean calendarPermission;
    private static final int calendarPerm = 21;


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case calendarPerm: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    calendarPermission = true;
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, Settings.class));
        }
        return true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.navibar, menu);
        return true;
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewPager viewPager = findViewById(R.id.view_pager);
        TabPageAdapter tabPagerAdapter = new TabPageAdapter(this, getSupportFragmentManager());
        viewPager.setAdapter(tabPagerAdapter);
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        getCalendarPermission();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }

    public void getCalendarPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALENDAR)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("CAL_PER", "true");
            calendarPermission = true;
        } else {
            Log.d("CAL_PER", "false");
            requestPermissions(new String[] {android.Manifest.permission.READ_CALENDAR},
                    calendarPerm);
        }
    }

    @Override
    public void onAppointmentSelected(String location) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof ParkFragment) {
                ViewPager viewPager = findViewById(R.id.view_pager);
                viewPager.setCurrentItem(0, true);

                ParkFragment parkFragment = (ParkFragment) fragment;
                parkFragment.searchPredefinedDestination(location);

                break;
            }
        }
    }
}