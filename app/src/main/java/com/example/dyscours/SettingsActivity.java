package com.example.dyscours;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Allows settings to be set
 */
public class SettingsActivity extends AppCompatActivity {
    private Settings settings;
    private Switch colorSwitch;
    private Switch applauseSwitch;
    private Spinner applauseSoundSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSettings);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title = toolbar.findViewById(R.id.titleTextSettings);
        title.setText("Settings");
        // Get the current settings
        settings = FirebaseHelper.getInstance().getSettings();
        // Get/set a variety of views
        colorSwitch = findViewById(R.id.colorSwitch);
        applauseSwitch = findViewById(R.id.applauseSwitch);
        colorSwitch.setChecked(settings.isColorRed());
        applauseSwitch.setChecked(settings.isApplauseOn());
        ApplauseSound[] applauseSounds = ApplauseSound.getSounds();
        ArrayAdapter<ApplauseSound> adapter = new ArrayAdapter<ApplauseSound>(this, android.R.layout.simple_spinner_dropdown_item, applauseSounds);
        applauseSoundSpinner = findViewById(R.id.spinnerApplauseSound);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item );
        applauseSoundSpinner.setAdapter(adapter);
        for (int i = 0; i < applauseSounds.length; i++){
            if (applauseSounds[i].getId() == settings.getApplauseSound()){
                applauseSoundSpinner.setSelection(i);
            }
        }
    }

    /**
     * Saves settings: updates the local settings and updates the version in the Firebase database.
     * @param v
     */
    public void saveSettings(View v){
        Settings newSettings = new Settings();
        newSettings.setApplauseOn(applauseSwitch.isChecked());
        newSettings.setColorRed(colorSwitch.isChecked());
        newSettings.setApplauseSound(((ApplauseSound)applauseSoundSpinner.getSelectedItem()).getId());
        FirebaseHelper.getInstance().setSettings(newSettings);
        Toast.makeText(this, "Settings Saved", Toast.LENGTH_SHORT).show();
    }
}
