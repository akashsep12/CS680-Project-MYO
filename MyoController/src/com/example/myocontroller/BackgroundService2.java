package com.example.myocontroller;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Camera;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Toast;


import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;
import com.thalmic.myo.Myo.VibrationType;



public class BackgroundService2 extends Service implements LocationListener{
    private static final String TAG = "BackgroundService2";
    private MediaController m;
    private Toast mToast;
    ActivityManager am;//To get information about the front activity
    Intent downIntent ; 
    Intent upIntent ; 
    int t=0;
    KeyEvent downEvent;
    KeyEvent upEvent ;
    long eventtime = SystemClock.uptimeMillis(); 
    AudioManager audioManager ; //For phone service
	int count = 0;
	private final String file = "list.txt";
	private double latitude,longitude;
	 LocationManager locationManager ;
	    String provider;
    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
      
	private void test() {  //TO check front activity and apply logic accordingly
    	
        @SuppressWarnings("deprecation")
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
        Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());
        ComponentName componentInfo = taskInfo.get(0).topActivity;
        componentInfo.getPackageName();
        Notification.Builder builder = new Notification.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Myo is connected")
				.setContentText("Time to sit back and relax");

		Intent notificationIntent = new Intent(this, Configuration.class);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);

		// Add as notification
		NotificationManager manager1 = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		manager1.notify(1, builder.build());

		 if(taskInfo.get(0).topActivity.getClassName().indexOf("com.example.myocontroller.Game")!=-1)
	        {
	        	t=3;
	        	Log.d("topActivity", "hello");
	        	
	        }
	        
	        else if(taskInfo.get(0).topActivity.getClassName().indexOf("com.android.phone.InCallScreen")!=-1)
        {
        	t=2;
        	Log.d("topActivity", "hello");
        	audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
       	
        }
        
        else if(taskInfo.get(0).topActivity.getClassName().indexOf("com.android.launcher")!=-1 ||taskInfo.get(0).topActivity.getClassName().indexOf("com.android.music")!=-1)
        {
        	int check=0;
        	ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
    	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
    	            {
    	                if ("com.example.myocontroller.BackgroundService".equals(service.service.getClassName())) 
    	                {
    	                    check=1;
    	                }
    	            }
        	
        	
        	if(check!=1)
        	{
        upIntent = new Intent("com.android.music.musicservicecommand");
        t=1;
        	}
        }
        else if(taskInfo.get(0).topActivity.getClassName().indexOf("com.android.launcher")==-1 ||taskInfo.get(0).topActivity.getClassName().indexOf("com.android.music")==-1)
        {
        //downIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.music");
        upIntent = null;
        t=0;
        
        
        
        }
        else ;
    	
    }
    
    private DeviceListener mListener = new AbstractDeviceListener() {
    	
    	
    	 
    	// onConnect() is called whenever a Myo has been connected.
    	@Override
    	public void onConnect(Myo myo, long timestamp) {
    		myo.vibrate(VibrationType.SHORT);
    		myo.vibrate(VibrationType.SHORT);
    		Configuration.getInstance().updateMyoDetails(myo);
    	}
    	
        @Override
        public void onAttach(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
            myo.vibrate(VibrationType.SHORT);
            
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
        	myo.vibrate(VibrationType.SHORT);
        	Configuration.getInstance().defaultMyoDetails();

        }

     // onUnlock() is called whenever a synced Myo has been unlocked. Under the standard locking
        // policy, that means poses will now be delivered to the listener.
        @Override
        public void onUnlock(Myo myo, long timestamp) {
        	myo.vibrate(VibrationType.SHORT);
        	myo.vibrate(VibrationType.SHORT);
        }

        // onLock() is called whenever a synced Myo has been locked. Under the standard locking
        // policy, that means poses will no longer be delivered to the listener.
        @Override
        public void onLock(Myo myo, long timestamp) {
        	myo.vibrate(VibrationType.SHORT);
        }

        // onPose() is called whenever the Myo detects that the person wearing it has changed their pose, for example,
        // making a fist, or not making a fist anymore.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Show the name of the pose in a toast.
        	
        	showToast(getString(R.string.pose, pose.toString()));
        	
             test();
        	
            if(t==1){
            switch (pose) {
            case UNKNOWN:
                
                break;
            case REST:
            	
            case DOUBLE_TAP:
                
                switch (myo.getArm()) {
                    case LEFT:
                        
                        break;
                    case RIGHT:
                       
                        break;
                }
                
                break;
            case FIST:
            	
            	upIntent.putExtra("command", "play");
            	sendBroadcast(upIntent);
                break;
            case WAVE_IN:
            	
            	upIntent.putExtra("command", "previous");
            	sendBroadcast(upIntent);
                break;
            case WAVE_OUT:
            	upIntent.putExtra("command", "next");
            	sendBroadcast(upIntent);
                break;
            case FINGERS_SPREAD:
            	upIntent.putExtra("command", "pause");
            	sendBroadcast(upIntent);
                break;
        }

            
            }
            
            else if(t==2)
            {
            	downEvent=null;
            	upEvent=null;
            	switch (pose) {
                case UNKNOWN:
                    
                    break;
                case REST:
                	
                case DOUBLE_TAP:
                    
                    switch (myo.getArm()) {
                        case LEFT:
                            
                            break;
                        case RIGHT:
                           
                            break;
                    }
                    
                    break;
                case FIST:
                	
                	
                	 
                    break;
                case WAVE_IN:
                	
                              	
                	 audioManager.setMode(AudioManager.MODE_IN_CALL);
                   	audioManager.setSpeakerphoneOn(true);
                	Intent answer = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    answer.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                	 showToast("I am Here");
                	 Log.d("topActivity", "I am Here");
                	 
                	 sendOrderedBroadcast(answer,"android.permission.CALL_PRIVILEGED");
                	 audioManager.setMode(AudioManager.MODE_IN_CALL);
                  	audioManager.setSpeakerphoneOn(true);
                	 
 
                  	
                    break;
                case WAVE_OUT:
                	
                	
                    break;
                case FINGERS_SPREAD:
                   
                    break;
            }
            	
            	
            }
            else if(t==3)
            {
            	downEvent=null;
            	upEvent=null;
            	switch (pose) {
                case UNKNOWN:
                    
                    break;
                case REST:
                	
                case DOUBLE_TAP:
                    
                    switch (myo.getArm()) {
                        case LEFT:
                            
                            break;
                        case RIGHT:
                           
                            break;
                    }
                    
                    break;
                case FIST:
                	
                	Game.getInstance().makemove(1);
                	 
                    break;
                case WAVE_IN:
                	
                	Log.d("flow", "u came here");
        			
        			
                	Game.getInstance().makemove(5);
        			
                	
                	 Log.d("flow", "I am Here");
                  	
                  	
                    break;
                case WAVE_OUT:
                	
                	Game.getInstance().makemove(4);
                    break;
                case FINGERS_SPREAD:
                	Game.getInstance().makemove(2);
                    break;
            }
            	
            	
            	
              	
              	
            }
           
            
            
            
            if (pose != Pose.UNKNOWN && pose != Pose.REST) {
            // Tell the Myo to stay unlocked until told otherwise. We do that here so you can
            // hold the poses without the Myo becoming locked.
            myo.unlock(Myo.UnlockType.HOLD);

            // Notify the Myo that the pose has resulted in an action, in this case changing
            // the text on the screen. The Myo will vibrate.
            myo.notifyUserAction();
        } else {
            // Tell the Myo to stay unlocked only for a short period. This allows the Myo to
            // stay unlocked while poses are being performed, but lock after inactivity.
            myo.unlock(Myo.UnlockType.TIMED);
        }
    		if (pose == Pose.FIST) {
				count++;
				if (count == 3) {
					try {
						
						
							
							if(new File(getFilesDir().toString()+"/"+"list.txt").exists())
							{
								InputStream in = openFileInput(file);
								InputStreamReader isr = new InputStreamReader(in);
								BufferedReader reader = new BufferedReader(isr);
								String str = null;
								getLocation();
								//showToast("hello "+latitude+" "+longitude);
								while ((str = reader.readLine()) != null) {
									Log.i("Emergency", "Count=" + count);
									showToast(str);
									sendSMS(str, "Its an emergency \n maps://maps.google.com/maps?daddr="+longitude+","+latitude +"\n https://www.google.com/maps?q="+longitude+","+latitude);
								}
								 
								reader.close();
							}
						 }catch (IOException e) {}
					
					count = 0;
				}
			}
			else if(pose == Pose.REST){
				// Do Nothing
			}
			else
				count=0;   
            
    }
            
            
        
    };
    private void sendSMS(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}

  
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        
          
        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            showToast("Couldn't initialize Hub");
            stopSelf();
            return;
        }
        Toast.makeText(this, "working", Toast.LENGTH_SHORT).show();
        // Disable standard Myo locking policy. All poses will be delivered.
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);
        
        showToast("MyoController Service Started.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Service is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);

        Hub.getInstance().shutdown();
        showToast("MyoController Service Stopped.");
    }

  
    public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
        // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
        float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
        float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
        float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));

        
        Log.i("test", roll + " "+ pitch+" "+ yaw);
        
        int t=(int)roll;
        
        if(t==45){
        	Toast.makeText(this, "You Rock", Toast.LENGTH_SHORT).show();
        }
        
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
    
    @Override
    public void onLocationChanged(Location location) {
      latitude= location.getLongitude();
 
       longitude=location.getLatitude();
    }
 
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
 
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    
    public void getLocation(){
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
 
        // Creating an empty criteria object
        Criteria criteria = new Criteria();
 
        // Getting the name of the provider that meets the criteria
        provider = locationManager.getBestProvider(criteria, false);
 
        if(provider!=null && !provider.equals("")){
 
            // Get the location from the given provider
            Location location = locationManager.getLastKnownLocation(provider);
 
            locationManager.requestLocationUpdates(provider, 20000, 1, this);
 
            if(location!=null)
                onLocationChanged(location);
            else
                Toast.makeText(getBaseContext(), "Location can't be retrieved", Toast.LENGTH_SHORT).show();
 
        }else{
            Toast.makeText(getBaseContext(), "No Provider Found", Toast.LENGTH_SHORT).show();
        }
    }
    
}
