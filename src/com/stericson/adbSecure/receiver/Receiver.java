package com.stericson.adbSecure.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.stericson.RootTools.Command;
import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.Shell;
import com.stericson.adbSecure.Constants;
import com.stericson.adbSecure.bundle.BundleScrubber;
import com.stericson.adbSecure.services.PreferenceService;
import com.stericson.adbSecure.services.adbdSecureService;

public class Receiver extends BroadcastReceiver
{

	PreferenceService p;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		p = new PreferenceService(context);
		
		if (intent.getAction() != null) {
			if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
				if (p.isProtecting() && adbdSecureService.isServiceRunning(context)) {
					Command command = new CommandCapture(0, "setprop persist.service.adb.enable 1", "start adbd");
					
					try {
						Shell shell = RootTools.getShell(true);
						shell.add(command).waitForFinish();
						
				        if (p.isToastNotify())
				        		Toast.makeText(context, "adbd reenabled!", Toast.LENGTH_LONG).show();
					}
					catch (Exception e) {}
				}
			}
			else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				if (p.isProtecting() && adbdSecureService.isServiceRunning(context)) {
					Command command = new CommandCapture(0, "setprop persist.service.adb.enable 0", "stop adbd");
					
					try {
						Shell shell = RootTools.getShell(true);
						shell.add(command).waitForFinish();
						
						if (p.isToastNotify())
							Toast.makeText(context, "adbd disabled!", Toast.LENGTH_LONG).show();				
					}
					catch (Exception e) {}
				}
			}
			else if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
				if (p.isOnBoot() && p.isProtecting()) {

			        Intent intent2 = new Intent(context, adbdSecureService.class);
	
					//Try to stop an existing service..
					context.stopService(intent2);
					
					//Try to start a new one.
				    context.startService(intent2);
				}
			}
			else if (intent.getAction().equals("com.stericson.intent.STOP_SERVICE")) {

				Intent intent2 = new Intent(context, adbdSecureService.class);
		    	
				//Try to stop an existing service..
				context.stopService(intent2);				
			}
	        /*
	         * Locale guarantees that the Intent action will be ACTION_FIRE_SETTING
	         */
			else if (!intent.getAction().equals("com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING"))
	        {

		        /*
		         * A hack to prevent a private serializable classloader attack
		         */
		        BundleScrubber.scrub(intent);
		        BundleScrubber.scrub(intent.getBundleExtra(Constants.BUNDLE_EXTRA_BUNDLE));
	
		        final Bundle bundle = intent.getBundleExtra(Constants.BUNDLE_EXTRA_BUNDLE);

		        if (bundle != null && bundle.containsKey(Constants.BUNDLE_EXTRA_BOOLEAN_ENABLE))
		        {
			        if (bundle.getBoolean(Constants.BUNDLE_EXTRA_BOOLEAN_ENABLE))
			        {
			        	p.setProtecting(true);
			        	
			        	//Enable the service
			        	Intent intent2 = new Intent(context, adbdSecureService.class);
			        		
						//Try to stop an existing service..
						context.stopService(intent2);
						
						//Try to start a new one.
					    context.startService(intent2);
			        }
			        else
			        {
			        	p.setProtecting(false);

			        	//Disable the service
			        	Intent intent2 = new Intent(context, adbdSecureService.class);
				    	
						//Try to stop an existing service..
						context.stopService(intent2);	
			        }
		        }
		        
	        }
		}
	}

}
