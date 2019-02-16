package com.pref.color.picker;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

/**
 * Support for Androidx.Preference.
 * need to extends this class.
 *
 * @author u1aryz
 * @author 7koston
 */
public abstract class ColorPreferenceFragmentCompat extends PreferenceFragmentCompat {

  private static final String COLOR_PICKER_DIALOG_TAG = "COLOR_PICKER_DIALOG";

  private FragmentManager mManager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mManager = getFragmentManager();
  }

  @Override
  public void onDisplayPreferenceDialog(Preference preference) {
    // check if dialog is already showing
    if (mManager.findFragmentByTag(COLOR_PICKER_DIALOG_TAG) != null) {
      return;
    }

    if (preference instanceof ColorPreference) {
      ColorPickerDialog dialog = ColorPickerDialog.newInstance(preference.getKey());
      dialog.setTargetFragment(this, 0);
      dialog.show(mManager, COLOR_PICKER_DIALOG_TAG);
    } else {
      super.onDisplayPreferenceDialog(preference);
    }
  }
}
