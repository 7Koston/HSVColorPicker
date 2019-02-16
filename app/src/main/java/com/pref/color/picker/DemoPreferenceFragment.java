package com.pref.color.picker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.preference.SwitchPreference;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DemoPreferenceFragment extends ColorPreferenceFragmentCompat {

  private Context mContext;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getContext();
  }

  @Override
  public void onCreatePreferences(Bundle bundle, String s) {
    addPreferencesFromResource(R.xml.demo);

    SwitchPreference themeMode = findPreference("theme");
    themeMode.setOnPreferenceChangeListener(
        (preference, newValue) -> {
          new MaterialAlertDialogBuilder(mContext, R.style.AppTheme_AlertDialog)
              .setMessage("Restart?")
              .setPositiveButton(
                  android.R.string.yes,
                  (dialog, which) ->
                      startActivity(
                          mContext
                              .getApplicationContext()
                              .getPackageManager()
                              .getLaunchIntentForPackage(
                                  mContext.getApplicationContext().getPackageName())
                              .addFlags(
                                  Intent.FLAG_ACTIVITY_CLEAR_TOP
                                      | Intent.FLAG_ACTIVITY_CLEAR_TASK)))
              .setNegativeButton(
                  android.R.string.cancel,
                  (dialog, which) -> themeMode.setChecked(!themeMode.isChecked()))
              .setOnCancelListener(d -> themeMode.setChecked(!themeMode.isChecked()))
              .setCustomTitle(null)
              .show();
          return true;
        });
  }
}
