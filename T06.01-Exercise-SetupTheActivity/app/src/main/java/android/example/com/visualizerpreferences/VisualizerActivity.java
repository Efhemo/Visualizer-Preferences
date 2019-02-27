package android.example.com.visualizerpreferences;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.example.com.visualizerpreferences.AudioVisuals.AudioInputReader;
import android.example.com.visualizerpreferences.AudioVisuals.VisualizerView;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class VisualizerActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizer);
        mVisualizerView = (VisualizerView) findViewById(R.id.activity_visualizer);
        setupSharedPreferences();
        setupPermissions();
    }

    private void setupSharedPreferences() {
        /*Gets a SharedPreferences instance that points to the default file that is
         used by the preference framework in the given context.*/
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        mVisualizerView.setShowBass(sharedPreferences.getBoolean("show_bass", true));
        mVisualizerView.setShowMid(sharedPreferences.getBoolean("pref_show_mid_key", true));
        mVisualizerView.setShowTreble(sharedPreferences.getBoolean("pref_show_treble_key", true));
        //loadSizeFromPreferences(sharedPreferences);
        //mVisualizerView.setMinSizeScale(1);
        loadColorFromPreferences(sharedPreferences);
        //mVisualizerView.setColor(sharedPreferences.getString(getString(R.string.pref_color_red_value),"red"));
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    /*Called when(After) a sharedPreference is changed, added, or removed.*/
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        /*{@link key} is the key that was changed*/

        if(key.equals("show_bass")){
            mVisualizerView.setShowBass(sharedPreferences.getBoolean(key, true));
        }else if(key.equals("pref_show_mid_key")){
            mVisualizerView.setShowMid(sharedPreferences.getBoolean(key, true));
        }else if(key.equals("pref_show_treble_key")){
            mVisualizerView.setShowTreble(sharedPreferences.getBoolean(key, true));
        }else if(key.equals(getString(R.string.pref_color_key))){
            loadColorFromPreferences(sharedPreferences);
        }else if(key.equals("show_size")){
            loadSizeFromPreferences(sharedPreferences);
        }
    }

    private void loadSizeFromPreferences(SharedPreferences sharedPreferences){
        Float minSize = Float.parseFloat(sharedPreferences.getString("show_size", "1"));
        mVisualizerView.setMinSizeScale(minSize);
    }

    private void loadColorFromPreferences(SharedPreferences sharedPreferences){
        mVisualizerView.setColor(sharedPreferences.getString(getString(R.string.pref_color_key), "red" ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     **/

    /**
     * onPause Cleanup audio stream
     **/
    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioInputReader != null) {
            mAudioInputReader.shutdown(isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAudioInputReader != null) {
            mAudioInputReader.restart();
        }
    }

    /**
     * App Permissions for Audio
     **/
    private void setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{ Manifest.permission.RECORD_AUDIO };
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = new AudioInputReader(mVisualizerView, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                }
            }
            // Other permissions could go down here

        }
    }

    // TODO (1) Create a new Empty Activity named SettingsActivity; make sure to generate the
    // activity_settings.xml layout file as well and add the activity to the manifest

    // TODO (2) Add a new resource folder called menu and create visualizer_menu.xml
    // TODO (3) In visualizer_menu.xml create a menu item with a single item. The id should be
    // "action_settings", title should be saved in strings.xml, the item should never
    // be shown as an action, and orderInCategory should be 100

    // TODO (5) Add the menu to the menu bar
    // TODO (6) When the "Settings" menu item is pressed, open SettingsActivity


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.visualizer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            Intent intent = new Intent(VisualizerActivity.this, SettingsActivity.class);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}