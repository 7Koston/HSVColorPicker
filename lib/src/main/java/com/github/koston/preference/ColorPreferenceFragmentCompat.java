package com.github.koston.preference;

import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;
import androidx.preference.PreferenceFragmentCompat;

@SuppressWarnings("unused")
public abstract class ColorPreferenceFragmentCompat extends PreferenceFragmentCompat {

  @Override
  public void onDisplayPreferenceDialog(Preference preference) {
    PreferenceDialogFragmentCompat dialogFragment = null;
    FragmentManager manager = getParentFragmentManager();
    if (preference instanceof ColorPreference) {
      dialogFragment = ColorPickerDialog.newInstance(preference.getKey());
    }
    if (dialogFragment != null) {
      String key = dialogFragment.getClass().getName();
      // Preference library still uses this approach even thought it is deprecated
      dialogFragment.setTargetFragment(this, 0);
      dialogFragment.show(manager, key);
    }
    else {
      super.onDisplayPreferenceDialog(preference);
    }
  }
}
