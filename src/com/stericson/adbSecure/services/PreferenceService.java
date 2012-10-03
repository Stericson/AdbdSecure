package com.stericson.adbSecure.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferenceService
{
	//All encryption AND decryption happens here.
	
	private SharedPreferences sharedPreferences = null;
	private Editor editor = null;
	
	public PreferenceService(Context context)
	{
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public boolean isProtecting()
	{
		return sharedPreferences.getBoolean("protecting", true);
	}
	
	public void setProtecting(boolean protecting)
	{
		getEditor().putBoolean("protecting", protecting).commit();		
	}

	public boolean isOnBoot()
	{
		return sharedPreferences.getBoolean("onBoot", true);
	}
	
	public boolean isNotifyStatusBar()
	{
		return sharedPreferences.getBoolean("statusbar_notify", true);
	}
	
	public boolean isToastNotify()
	{
		return sharedPreferences.getBoolean("toast_notify", true);
	}
	
	public void commit()
	{
		getEditor().commit();
	}
	
	private Editor getEditor()
	{
		if (editor == null)
			editor = sharedPreferences.edit();
		return editor;
	}
}
