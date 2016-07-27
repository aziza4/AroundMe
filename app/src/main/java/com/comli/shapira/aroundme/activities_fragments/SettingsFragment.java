package com.comli.shapira.aroundme.activities_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.geoFencing.GeofenceAppHelper;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;


public class SettingsFragment extends PreferenceFragment {

    private GeofenceAppHelper mGeofenceAppHelper;

    public SettingsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGeofenceAppHelper = new GeofenceAppHelper(getActivity());

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(getActivity());
        sharedPrefHelper.changeLocale();
        addPreferencesFromResource(R.xml.pref_general);

        ListPreference langPref = (ListPreference) findPreference(getString(R.string.pref_lang_key));
        sharedPrefHelper.setFirstTimeLanguageSummary(langPref);

        loopOverAllPreferencesToSetSummary();

        PreferenceManager pm = getPreferenceManager();
        bindListPreferenceSummaryToValue(pm.findPreference(getString(R.string.pref_lang_key)));
        bindListPreferenceSummaryToValue(pm.findPreference(getString(R.string.pref_units_key)));
        bindListPreferenceSummaryToValue(pm.findPreference(getString(R.string.pref_geofences_type_key)));

        bindEditTextPreferenceSummaryToValue(pm.findPreference(getString(R.string.pref_radius_key)));
        bindEditTextPreferenceSummaryToValue(pm.findPreference(getString(R.string.pref_geofences_radius_key)));

        bindCheckBoxPreferenceSummaryToValue(pm.findPreference(getString(R.string.pref_geofences_show_notification_key)));
        bindCheckBoxPreferenceSummaryToValue(pm.findPreference(getString(R.string.pref_geofences_show_notification_sound_key)));
    }

    private void bindListPreferenceSummaryToValue(Preference preference)
    {
        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                ListPreference listPreference = (ListPreference) preference;
                String newValue = o.toString();
                String oldValue = listPreference.getValue();

                int index = listPreference.findIndexOfValue(newValue);

                if (index < 0)
                    return true;

                preference.setSummary(listPreference.getEntries()[index]);

                boolean geofenceTypeSelected = listPreference.getKey().equals(getString(R.string.pref_geofences_type_key));
                if (geofenceTypeSelected) {
                    refreshGeofences();
                    return true;
                }

                boolean langSelected = listPreference.getKey().equals(getString(R.string.pref_lang_key));
                boolean valueChanged = ! oldValue.equals(newValue);

                if ( langSelected && valueChanged )
                {
                    SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(getActivity());
                    sharedPrefHelper.setLangChanged(true);

                    // restart activity --> lang change to take affect immediately !
                    Intent intent = getActivity().getIntent();
                    startActivity(intent);
                    getActivity().finish();
                }

                return true;
            }
        });
    }

    private void bindEditTextPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {

                EditTextPreference editPreference = (EditTextPreference) preference;
                String oldValue = editPreference.getText();
                String newValue = o.toString();
                boolean valueChanged = ! oldValue.equals(newValue);

                editPreference.setSummary(newValue);

                if (valueChanged) {

                    boolean geofenceRadiusSelected = editPreference.getKey().equals(getString(R.string.pref_geofences_radius_key));
                    if (geofenceRadiusSelected)
                        refreshGeofences();
                }

                return true;
            }
        });
    }

    private void bindCheckBoxPreferenceSummaryToValue(Preference preference) {

        preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                refreshGeofences();
                return true;
            }
        });
    }

    private void refreshGeofences()
    {
        mGeofenceAppHelper.refresh();
    }



    private void loopOverAllPreferencesToSetSummary()
    {
        int prefCategoryCount = getPreferenceScreen().getPreferenceCount();
        for(int i = 0; i < prefCategoryCount; i++) {
            Preference p = getPreferenceScreen().getPreference(i);
            if (p instanceof PreferenceCategory) {
                PreferenceCategory prefCategory = (PreferenceCategory)p;
                int prefsCount = prefCategory.getPreferenceCount();
                for (int j=0; j < prefsCount; j++) {
                    Preference pref = prefCategory.getPreference(j);
                    initializeSummary(pref);
                }
            }
        }
    }

    private void initializeSummary(Preference p)
    {
        if(p instanceof ListPreference) {
            ListPreference listPref = (ListPreference)p;
            CharSequence entry = listPref.getEntry();
            p.setSummary(entry);
            return;
        }

        if(p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference)p;
            CharSequence entry = editTextPref.getText();
            p.setSummary(entry);
        }
    }

}
