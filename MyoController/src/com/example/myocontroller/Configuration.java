package com.example.myocontroller;

import java.io.File;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Hub.LockingPolicy;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Myo.VibrationType;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.scanner.ScanActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;


public class Configuration extends ListActivity {
	
    String[] items = {"Google Music","Spotify","Audible","Pandora","Emergency Contact","SNAKE"};
    public static Myo myo;
	private OutputStreamWriter out;
	private final String defaultFile = "default.txt";
	private final String currentFile = "current.txt";
	private String line;
	File fileObjectDefault = new File(defaultFile);
	File fileObjectCurrent = new File(currentFile);
	String name = "";
	String macAddress = "";
    private NotificationReceiver nReceiver;
    private Toast mToast;
    private static final String TAG = "BackgroundService2";
    Context context;
    ContentResolver contentResolver;
    String enabledNotificationListeners;
    String packageName;
    TextView tv;
    public static Configuration c;
	
    public static Configuration getInstance() {
    	return c;
    }
    public void updateMyoDetails(Myo myo) {
    	tv = (TextView) findViewById(R.id.myo_name);
    	tv.setText("Myo Name: " + myo.getName());
    	tv = (TextView) findViewById(R.id.mac_address);
    	tv.setText("MAC Address: " + myo.getMacAddress());
    	tv = (TextView) findViewById(R.id.myo_connected);
    	tv.setText("Connected");
    }
    
    public void defaultMyoDetails() {
    	tv = (TextView) findViewById(R.id.myo_name);
    	tv.setText("Myo Name: ");
    	tv = (TextView) findViewById(R.id.mac_address);
    	tv.setText("MAC Address: ");
    	tv = (TextView) findViewById(R.id.myo_connected);
    	tv.setText("Not Connected");
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
	    
        c = this;
        
		TabHost tabs=(TabHost)findViewById(R.id.tabhost);
		tabs.setup();
				
		TabHost.TabSpec spec;
		
		// Initialize a TabSpec for tab1 and add it to the TabHost
		spec=tabs.newTabSpec("tag1");	//create new tab specification
		spec.setContent(R.id.tab1);    //add tab view content
		spec.setIndicator("Supported Installed Apps");    //put text on tab
		tabs.addTab(spec);             //put tab in TabHost container
		
        context = getApplicationContext();
        contentResolver = context.getContentResolver();
        packageName = context.getPackageName();
		
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
		
		
		
		// Initialize a TabSpec for tab2 and add it to the TabHost
		spec=tabs.newTabSpec("tag3");		//create new tab specification
		spec.setContent(R.id.tab3);			//add view tab content
		spec.setIndicator("Connected Myo");
		tabs.addTab(spec);					//put tab in TabHost container
		
        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.bourdeau.NotificationsTest");
        registerReceiver(nReceiver,filter);
        // start notification listener service
        //startService(new Intent(this, BackgroundService2.class));
        
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
        } else if (R.id.action_start_service_media == id) {
        	
        	if (android.os.Build.VERSION.SDK_INT >= 21) {
        		
        		enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");

	        	// check to see if the enabledNotificationListeners String contains our package name
	        	if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
	        	{
	        		showToast("This feature requires notification access.");
	    			startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
	        	}
	        	else
	        	{
	        		startService(new Intent(this, BackgroundService.class));
	        	}
        	} else {
        		Toast.makeText(this, "Your phone does not support this service", Toast.LENGTH_LONG).show();
        	}
        } else if (R.id.action_start_service == id) {
        	if (!isMyServiceRunning(BackgroundService.class)) {
        		startService(new Intent(this, BackgroundService2.class));
        	} else {
        		showToast("You must stop the Media Background Service before performing this action.");
        	}
        } else if (R.id.action_stop_service_media == id) {
        	if (isMyServiceRunning(BackgroundService.class)) {
        		stopService(new Intent(this, BackgroundService.class));
        	} else {
        		showToast("Media Background Service not running.");
        	}
        } else if (R.id.action_stop_service == id) {
        	stopService(new Intent(this, BackgroundService2.class));
        } else if (R.id.action_set_default == id) {
        	
        }
        return super.onOptionsItemSelected(item);
    }

    private void onScanActionSelected() {
        // Launch the ScanActivity to scan for Myos to connect to.
    	if (isMyServiceRunning(BackgroundService.class) || isMyServiceRunning(BackgroundService2.class)) {
    		Intent intent = new Intent(this, ScanActivity.class);
	        startActivity(intent);
    	} else {
    		showToast("Background Service or Media Background Service must be running to perform this action.");
    	}
	        
    }
    
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		
		String text = items[position];
		
		int check=0;
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
	            {
	                if ("com.example.myocontroller.BackgroundService".equals(service.service.getClassName())) 
	                {
	                    check=1;
	                }
	            }
		
		
		if(text.equals("Google Music"))
		{
			if(isPackageExisted("com.google.android.music")) {
				Intent i=new Intent();
				i=getPackageManager().getLaunchIntentForPackage("com.google.android.music");
				startActivity(i);
			} else {
				showToast("App is not installed.");
			}
		}
		else if(text.equals("Spotify"))
		{
			if(check==1)
			{
				if(isPackageExisted("com.spotify.music")) {
					Intent i=new Intent();
					i=getPackageManager().getLaunchIntentForPackage("com.spotify.music");
					startActivity(i);
				} else {
					showToast("App is not installed.");
				}
			}
			else
					Toast.makeText(this, "You must start the MyoController Media Service first", Toast.LENGTH_LONG).show();
			
			
		} else if (text.equals("Pandora")) {
			if(check==1)
			{
				if(isPackageExisted("com.pandora.android")) {
					Intent i=new Intent();
					i=getPackageManager().getLaunchIntentForPackage("com.pandora.android");
					startActivity(i);
				} else {
					showToast("App is not installed.");
				}
			}
			else
					Toast.makeText(this, "You must start the MyoController Media Service first", Toast.LENGTH_LONG).show();
		}
		else if (text.equalsIgnoreCase("Emergency Contact")){
			Intent i=new Intent(this,MainActivity.class);
			startActivity(i);
		}else if (text.equalsIgnoreCase("SNAKE")){
			Intent i=new Intent(this,Game.class);
			startActivity(i);
			
		}
		
	}
	
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
    private void showToast(String text) {
        Log.w(TAG, text);
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
    
    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
        	
        }
    }
    
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isPackageExisted(String targetPackage){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getPackageManager();        
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }
    
}
