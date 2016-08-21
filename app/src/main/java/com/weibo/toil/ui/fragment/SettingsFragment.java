package com.weibo.toil.ui.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.weibo.toil.BuildConfig;
import com.weibo.toil.R;
import com.weibo.toil.ui.event.StatusBarEvent;
import com.weibo.toil.utils.CacheUtil;
import com.weibo.toil.utils.RxBus;

public class SettingsFragment extends PreferenceFragment{

    private Preference prefCache;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefCache = findPreference(getString(R.string.pre_cache_size));
        prefCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CacheUtil.deleteDir(SettingsFragment.this.getActivity().getCacheDir());
                showCacheSize(prefCache);
                return true;
            }
        });
        findPreference(getString(R.string.pre_status_bar)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                RxBus.getDefault().send(new StatusBarEvent());
                return true;
            }
        });
        findPreference(getString(R.string.pre_nav_color)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                RxBus.getDefault().send(new StatusBarEvent());
                return true;
            }
        });
        Preference version = findPreference(getString(R.string.pre_version));
        version.setSummary(BuildConfig.VERSION_NAME);
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showCacheSize(prefCache);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void showCacheSize(Preference preference) {
        preference.setSummary(getActivity().getString(R.string.cache_size) + CacheUtil.getCacheSize(getActivity().getCacheDir()));
    }

}
