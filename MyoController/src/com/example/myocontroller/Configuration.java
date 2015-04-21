package com.example.myocontroller;

import java.util.ArrayList;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TabHost;
import android.widget.TextView;


public class Configuration extends ListActivity {
	
    String[] items = { "Music Player", "Video Player", "Google Maps", "Tinder", 
 		   "Camera" };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
	    
		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		tabs.setup();
				
		TabHost.TabSpec spec;
		
		// Initialize a TabSpec for tab1 and add it to the TabHost
		spec=tabs.newTabSpec("tag1");	//create new tab specification
		spec.setContent(R.id.tab1);    //add tab view content
		spec.setIndicator("Supported Installed Apps");    //put text on tab
		tabs.addTab(spec);             //put tab in TabHost container
        
	    ArrayAdapter<String> aa = new ArrayAdapter<String>(
		      this,
		      android.R.layout.simple_list_item_1,     //Android supplied List item format
		      items);
	    setListAdapter(aa);    //connect ArrayAdapter to <ListView>
	    
		// Initialize a TabSpec for tab2 and add it to the TabHost
		spec=tabs.newTabSpec("tag2");		//create new tab specification
		spec.setContent(R.id.tab2);			//add view tab content
		spec.setIndicator("Gestures");
		tabs.addTab(spec);					//put tab in TabHost container
		
		// Start the BackgroundService to receive and handle Myo events.
        startService(new Intent(this, BackgroundService.class));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.configuration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
            onScanActionSelected();
            return true;
        } else if (R.id.start_service == id) {
        	startService(new Intent(this, BackgroundService.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }
    
}
