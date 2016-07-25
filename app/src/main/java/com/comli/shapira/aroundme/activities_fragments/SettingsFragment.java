package com.comli.shapira.aroundme.activities_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import com.comli.shapira.aroundme.R;
import com.comli.shapira.aroundme.helpers.SharedPrefHelper;


public class SettingsFragment extends PreferenceFragment
        implements Preference.OnPreferenceChangeListener {


    public SettingsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(getActivity());
        sharedPrefHelper.changeLocale();
        addPreferencesFromResource(R.xml.pref_general);

        ListPreference langPref = (ListPreference) findPreference(getString(R.string.pref_lang_key));
        sharedPrefHelper.setFirstTimeLanguageSummary(langPref);

        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_lang_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_radius_key)));
    }


    private void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(this);

        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        String valueStr = newValue.toString();

        if (preference instanceof ListPreference)
        {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(valueStr);

            if (index >= 0) {
                preference.setSummary(listPreference.getEntries()[index]);

                boolean langSelected = preference.getKey().equals(getString(R.string.pref_lang_key));
                boolean valueChanged = !((ListPreference) preference).getValue().equals(newValue);

                if ( langSelected && valueChanged )
                {
                    SharedPrefHelper sharedPrefHelper = new SharedPrefHelper(getActivity());
                    sharedPrefHelper.setLangChanged(true);
                    restartSettingsActivity(); // lang change to take affect immediately !
                }
            }

        } else {

            preference.setSummary(valueStr);
        }

        return true;
    }

    private void restartSettingsActivity() // this works, while getActivity().recreate() fails...
    {
        Intent intent = getActivity().getIntent();
        startActivity(intent);
        getActivity().finish();
    }
}
