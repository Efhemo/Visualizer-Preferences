package android.example.com.visualizerpreferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

/**
 * Created by EFHEMO on 5/22/2018.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

     EditTextPreference editTextPreference;
     ListPreference listPreference;
    /**
     * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
     * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
     * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           {@link PreferenceScreen} with this key.
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);
        editTextPreference = (EditTextPreference)findPreference("show_size");

        loadEditPreferenceSummary();

    }

    private void loadEditPreferenceSummary(){

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editTextPreference.setSummary(sharedPreferences.getString("show_size", "1"));
        editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            /* This is called before the state
             of the Preference is about to be updated and before the state is persisted.*/
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast erroMsg = Toast.makeText(getContext(),"Please Select a Number Between 0.1 and 3", Toast.LENGTH_LONG);
                String newString = newValue.toString();
                try {
                    Float size = Float.parseFloat(newString);
                    if(size>3 || size <=0){
                        erroMsg.show();
                        return false;
                    }
                }catch (NumberFormatException fe){ //this is for throwing a String that is not a number
                    erroMsg.show();
                    return false;
                }

                sharedPreferences.edit().putString("show_size", newString).apply();
                editTextPreference.setSummary(newString);
                return true;
            }
        });

    }

}
