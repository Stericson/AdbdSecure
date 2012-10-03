package com.stericson.adbSecure;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Tasker_edit extends PreferenceActivity {

	CheckBoxPreference protecting;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.tasker_preferences);

		Toast.makeText(this, "Hit the back button when you are done to finish configuration.", Toast.LENGTH_LONG).show();
		
		protecting = (CheckBoxPreference) this.findPreference("unrelated");
		protecting.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				
				try {
		            Boolean val = (Boolean)newValue;
					
		            if (val) {

		            } else {

		            }
				}
				catch (Exception ignore) {}
				
				return true;
			}
			
		});
    }
    
    @Override
    public void finish()
    {
        /*
         * This is the result Intent to Locale
         */
        final Intent resultIntent = new Intent();

        final Bundle resultBundle = new Bundle();
        resultIntent.putExtra(Constants.BUNDLE_EXTRA_BUNDLE, resultBundle);
        
        resultBundle.putBoolean(Constants.BUNDLE_EXTRA_BOOLEAN_ENABLE, protecting.isChecked());

        /*
         * This is the blurb concisely describing what your setting's state is. This is simply used
         * for display in the UI.
         */
         resultIntent.putExtra(Constants.BUNDLE_EXTRA_STRING_BLURB, "adbdSecure service will be " + (protecting.isChecked() ? "enabled." : "disabled."));

        setResult(RESULT_OK, resultIntent);

        super.finish();
    }
}
