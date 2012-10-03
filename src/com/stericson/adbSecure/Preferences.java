package com.stericson.adbSecure;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.provider.Settings;

import com.stericson.adbSecure.services.PreferenceService;
import com.stericson.adbSecure.services.adbdSecureService;

public class Preferences extends PreferenceActivity {

	PreferenceService p;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
        
		p = new PreferenceService(this);
		
		Preference secure = (Preference) this.findPreference("secure");
		secure.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(Preference preference) {

				try
				{
					ComponentName c = new ComponentName("com.android.settings","com.android.settings.SecuritySettings");

			        Intent i = new Intent(Settings.ACTION_SECURITY_SETTINGS);
			        i.addCategory(Intent.CATEGORY_LAUNCHER);
			        i.setComponent(c);
			        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        startActivity(i);
				}
				catch (Exception ignore) {}
				
				return true;
			}
				
		});
		
		CheckBoxPreference protecting = (CheckBoxPreference) this.findPreference("protecting");
		protecting.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				
				try {
		            Boolean val = (Boolean)newValue;
					
		            if (val) {
		                Intent intent = new Intent(Preferences.this, adbdSecureService.class);
		        		
		        		//Try to start a new one.
		        	    startService(intent);		                
		            } else {
		                Intent intent = new Intent(Preferences.this, adbdSecureService.class);

		        		//Try to stop an existing service..
		        		stopService(intent);
		            }
				}
				catch (Exception ignore) {}
				
				return true;
			}
			
		});
		
		CheckBoxPreference statusbar_notify = (CheckBoxPreference) this.findPreference("statusbar_notify");
		statusbar_notify.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				
				try {
		            Boolean val = (Boolean) newValue;
					
		            if (val) {
		        		if (!adbdSecureService.isServiceRunning(Preferences.this)) {
		                	String ns = Context.NOTIFICATION_SERVICE;
		                	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		        	
		        			Notification notification = new Notification();
		        			notification.icon = R.drawable.ic_stat;
		        			notification.when = System.currentTimeMillis();
		        			
		        			Intent notificationIntent = new Intent("com.stericson.intent.STOP_SERVICE");
		        			PendingIntent contentIntent = PendingIntent.getBroadcast(Preferences.this, 0, notificationIntent, 0);
		        	
		        			notification.contentIntent = contentIntent;
		        			
		        			notification.flags |= Notification.FLAG_ONGOING_EVENT;
		        	
		        			notification.setLatestEventInfo(Preferences.this, "adbd protected",
		        					"Tap to end protection", contentIntent);
		        			
		        			mNotificationManager.notify(Constants.NOTIFICATION, notification);
		                }
		            } else {
		            	String ns = Context.NOTIFICATION_SERVICE;
	                	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
	                    // Cancel the persistent notification.
	                	mNotificationManager.cancelAll();
		            }
				}
				catch (Exception ignore) {}
				
				return true;
			}
			
		});
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	
		//Start the service if not running and we are protecting.
		if (!adbdSecureService.isServiceRunning(this) && p.isProtecting()) {
            Intent intent = new Intent(Preferences.this, adbdSecureService.class);
    		
    		//Try to start a new one.
    	    startService(intent);		                			
		}
    }
}
