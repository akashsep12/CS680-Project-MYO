package com.example.myocontroller;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.media.session.MediaSessionManager.OnActiveSessionsChangedListener;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.Arm;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Hub.LockingPolicy;
import com.thalmic.myo.Myo.VibrationType;
import com.thalmic.myo.Pose;
import com.thalmic.myo.Quaternion;
import com.thalmic.myo.XDirection;

public class BackgroundService extends NotificationListenerService {
	
	public static final String ACTION_PLAY = "action_play";
	public static final String ACTION_PAUSE = "action_pause";
	public static final String ACTION_REWIND = "action_rewind";
	public static final String ACTION_FAST_FORWARD = "action_fast_foward";
	public static final String ACTION_NEXT = "action_next";
	public static final String ACTION_PREVIOUS = "action_previous";
	public static final String ACTION_STOP = "action_stop";
	
	private Toast mToast;
    private String TAG = this.getClass().getSimpleName();
    private NLServiceReceiver nlservicereciver;
    MediaSessionManager msm;
    ComponentName cn = new ComponentName("com.example.myocontroller",this.getClass().getName());
    List<MediaController> msl;
    MediaController mc;
	
    @Override
    public void onCreate() {
        super.onCreate();
        nlservicereciver = new NLServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.service.notification.NotificationListenerService");
        registerReceiver(nlservicereciver,filter);
        msm = (MediaSessionManager) this.getSystemService(Context.MEDIA_SESSION_SERVICE);
        
        msl = msm.getActiveSessions(cn);
        Log.e("GOT HERE", "WE MADE IT!");
        msm.addOnActiveSessionsChangedListener(oASC, cn);
        
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            // We can't do anything with the Myo device if the Hub can't be initialized, so exit.
            Toast.makeText(this, "Couldn't initialize Hub", Toast.LENGTH_SHORT).show();
            stopService(new Intent(this,BackgroundService.class));
            return;
        }
        hub.addListener(mListener);
        hub.setLockingPolicy(LockingPolicy.STANDARD);

        if (!msl.isEmpty()) {
			mc = msl.get(0);
		}
        showToast("MyoController Media Service Started.");
        
    }

    OnActiveSessionsChangedListener oASC = new OnActiveSessionsChangedListener() {
    	
    	public void onActiveSessionsChanged (List<MediaController> controllers) {
    		msl = controllers;
    		if (!msl.isEmpty()) {
    			mc = controllers.get(0);
    		}
    		
    	}
    	
    };
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlservicereciver);
        msm.removeOnActiveSessionsChangedListener(oASC);
        showToast("Media Service Stopped");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
    	
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    	
    }

    class NLServiceReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {


        }
    }
    
    private DeviceListener mListener = new AbstractDeviceListener() {
    	
    	private boolean first = true;
    	
    	// onConnect() is called whenever a Myo has been connected.
    	@Override
    	public void onConnect(Myo myo, long timestamp) {
    		myo.vibrate(VibrationType.MEDIUM);
    		showToast("Myo Connected");
    		Configuration.getInstance().updateMyoDetails(myo);
    	}
    	
        @Override
        public void onAttach(Myo myo, long timestamp) {
            // Set the text color of the text view to cyan when a Myo connects.
        	
        }

        // onDisconnect() is called whenever a Myo has been disconnected.
        @Override
        public void onDisconnect(Myo myo, long timestamp) {
            // Set the text color of the text view to red when a Myo disconnects.
        	myo.vibrate(VibrationType.MEDIUM);
        	showToast("Myo Disconnected");
        	Configuration.getInstance().defaultMyoDetails();
        }

        // onArmSync() is called whenever Myo has recognized a Sync Gesture after someone has put it on their
        // arm. This lets Myo know which arm it's on and which way it's facing.
        @Override
        public void onArmSync(Myo myo, long timestamp, Arm arm, XDirection xDirection) {
        	myo.vibrate(VibrationType.LONG);
        	showToast("Myo Synced");
        }

        // onArmUnsync() is called whenever Myo has detected that it was moved from a stable position on a person's arm after
        // it recognized the arm. Typically this happens when someone takes Myo off of their arm, but it can also happen
        // when Myo is moved around on the arm.
        @Override
        public void onArmUnsync(Myo myo, long timestamp) {
        	myo.vibrate(VibrationType.LONG);
        	showToast("Myo Unsynced");
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

        // onOrientationData() is called whenever a Myo provides its current orientation,
        // represented as a quaternion.
        @Override
        public void onOrientationData(Myo myo, long timestamp, Quaternion rotation) {
            // Calculate Euler angles (roll, pitch, and yaw) from the quaternion.
            float roll = (float) Math.toDegrees(Quaternion.roll(rotation));
            float pitch = (float) Math.toDegrees(Quaternion.pitch(rotation));
            float yaw = (float) Math.toDegrees(Quaternion.yaw(rotation));
            
            while (myo.getPose() == Pose.FIST) {
            	
            }
            // Adjust roll and pitch for the orientation of the Myo on the arm.
            if (myo.getXDirection() == XDirection.TOWARD_ELBOW) {
                roll *= -1;
                pitch *= -1;
            }

            // Next, we apply a rotation to the text view using the roll, pitch, and yaw.
        }

        // onPose() is called whenever a Myo provides a new pose.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Handle the cases of the Pose enumeration, and change the text of the text view
            // based on the pose we receive.
        	if (!msl.isEmpty()) {
	            switch (pose) {
		            case UNKNOWN:
		                break;
		            case REST:
		            	showToast("Rest");
		            case DOUBLE_TAP:
		            	showToast("Double Tap");
		                Log.i("Myo", "Double Tap");
		                switch (myo.getArm()) {
		                    case LEFT:break;
		                    case RIGHT:break;
		                }
		                break;
		            case FIST:
		            	showToast("Fist");
		            	mc.getTransportControls().stop();
		            	Log.i("Myo", "Fist");
		                break;
		            case WAVE_IN:
		            	showToast("Wave In");
		            	mc.getTransportControls().skipToPrevious();
		                Log.i("Myo", "Wave In");
		                break;
		            case WAVE_OUT:
		            	showToast("Wave Out");
		            	mc.getTransportControls().skipToNext();
		                Log.i("Myo", "Wave Out");
		                break;
		            case FINGERS_SPREAD:
		            	showToast("Fingers Spread");
		            	switch(mc.getPlaybackState().getState()) {
			            	case PlaybackState.STATE_BUFFERING : break;
			            	case PlaybackState.STATE_CONNECTING : break;
			            	case PlaybackState.STATE_ERROR : break;
			            	case PlaybackState.STATE_FAST_FORWARDING : 
			            		mc.getTransportControls().play();
			            		break;
			            	case PlaybackState.STATE_NONE : break;
			            	case PlaybackState.STATE_PAUSED : 
			            		mc.getTransportControls().play();
			            		break;
			            	case PlaybackState.STATE_PLAYING : 
			            		mc.getTransportControls().pause();
			            		break;
			            	case PlaybackState.STATE_REWINDING : 
			            		mc.getTransportControls().play();
			            		break;
			            	case PlaybackState.STATE_SKIPPING_TO_NEXT : break;
			            	case PlaybackState.STATE_SKIPPING_TO_PREVIOUS : break;
			            	case PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM : break;
			            	case PlaybackState.STATE_STOPPED : 
			            		mc.getTransportControls().play();
			            		break;
		            	}
		            	Log.i("Myo", "Spread");
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
        }
    };
    
    private void showToast(String text) {
        Log.w(TAG, text);
        if (mToast == null) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }
    
}
