package com.github.koston.preference;

import android.os.Bundle;
import android.view.View;
import androidx.preference.PreferenceDialogFragmentCompat;
import com.github.koston.preference.view2.ColorPicker;
import com.github.koston.preference.view2.OmniBar;
import com.github.koston.preference.view2.OpacityBar;

public class ColorPickerDialog extends PreferenceDialogFragmentCompat
    implements ColorPicker.OnColorChangedListener {

  private ColorPicker picker;
  private OmniBar saturationBar;
  private OpacityBar opacityBar;
  private OmniBar valueBar;

  private ColorPreference mPreference;

  static ColorPickerDialog newInstance(String key) {
    ColorPickerDialog dialog = new ColorPickerDialog();
    Bundle args = new Bundle(1);
    args.putString(ARG_KEY, key);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  protected void onBindDialogView(View view) {
    super.onBindDialogView(view);

    picker = view.findViewById(R.id.picker);
    saturationBar = view.findViewById(R.id.saturationBar);
    opacityBar = view.findViewById(R.id.opacitybar);
    valueBar = view.findViewById(R.id.valueBar);

    mPreference = (ColorPreference) getPreference();
    int color = mPreference.getColor();
    picker.addSaturationBar(saturationBar);
    picker.addOpacityBar(opacityBar);
    picker.addValueBar(valueBar);
    picker.setShowOldCenterColor(true);
    picker.setShowCenter(true);
    picker.setOldCenterColor(color);
    picker.setNewCenterColor(color);
    picker.setOnColorChangedListener(this);
  }

  @Override
  public void onDialogClosed(boolean positiveResult) {
    if (positiveResult) {
      int color = picker.getColor();
      if (mPreference.callChangeListener(color)) {
        mPreference.setColor(color);
      }
    }
  }

  @Override
  public void onColorChanged(int newColor) {
  }
}
