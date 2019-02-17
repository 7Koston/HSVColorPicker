package com.pref.color.picker;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;

public abstract class ColorPreferenceFragmentCompat extends PreferenceFragmentCompat {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public void onDisplayPreferenceDialog(Preference preference) {
    PreferenceDialogFragmentCompat dialogFragment = null;
    FragmentManager manager = getFragmentManager();
    if (preference instanceof ColorPreference) {
      dialogFragment = ColorPickerDialog.newInstance(preference.getKey());
    }
    if (dialogFragment != null && manager != null) {
      dialogFragment.setTargetFragment(this, 0);
      dialogFragment.show(manager, ColorPickerDialog.class.getName());
    } else {
      super.onDisplayPreferenceDialog(preference);
    }
  }
}
