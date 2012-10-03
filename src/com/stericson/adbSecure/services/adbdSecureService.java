package com.stericson.adbSecure.services;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import com.stericson.adbSecure.Constants;
import com.stericson.adbSecure.R;
import com.stericson.adbSecure.receiver.Receiver;

public class adbdSecureService extends Service
{
	
	static PreferenceService p;
	static BroadcastReceiver mReceiver;

    @Override
	public void onCreate() {
		p = new PreferenceService(this);
		//I'll hand the notification on my own, however we want the service to be a foreground service
		//this way it is less likely to be killed.
		this.startForeground(0, null);
	}
	
    @Override
    public void onDestroy() {

    	if (p == null)
    		p = new PreferenceService(this);

        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        // Cancel the persistent notification.
    	mNotificationManager.cancelAll();

        // Tell the user we stopped.
        if (p.isToastNotify())
        	Toast.makeText(this, "adbdSecure stopped", Toast.LENGTH_SHORT).show();
        
        if (mReceiver != null)
        	unregisterReceiver(mReceiver);
        
        this.stopForeground(false);
    }
    
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        mReceiver = new Receiver();
        registerReceiver(mReceiver, filter);

        if (p.isNotifyStatusBar()) {
	        String ns = Context.NOTIFICATION_SERVICE;
	        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	
			Notification notification = new Notification();
			notification.icon = R.drawable.ic_stat;
			notification.when = System.currentTimeMillis();
			
			Intent notificationIntent = new Intent("com.stericson.intent.STOP_SERVICE");
			PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, 0);
	
			notification.contentIntent = contentIntent;
			
			notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_AUTO_CANCEL;
	
			notification.setLatestEventInfo(this, "adbd protected",
					"Tap to end protection", contentIntent);
			
			mNotificationManager.notify(Constants.NOTIFICATION, notification);
        }
		
        if (p.isToastNotify())
        	Toast.makeText(this, "adbdSecure started.", Toast.LENGTH_LONG).show();

		// If we get killed, after returning from here, restart
		return START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	public static boolean isServiceRunning(Context context) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if ("com.stericson.adbSecure.services.adbdSecureService".equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
}
