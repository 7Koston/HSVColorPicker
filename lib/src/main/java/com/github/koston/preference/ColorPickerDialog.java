package com.github.koston.preference;

import android.os.Bundle;
import android.view.View;
import androidx.preference.PreferenceDialogFragmentCompat;
import com.github.koston.preference.view.ColorPicker;
import com.github.koston.preference.view.OpacityBar;
import com.github.koston.preference.view.SaturationBar;
import com.github.koston.preference.view.ValueBar;

public class ColorPickerDialog extends PreferenceDialogFragmentCompat
    implements ColorPicker.OnColorChangedListener {

  private ColorPicker picker;
  private SaturationBar saturationBar;
  private OpacityBar opacityBar;
  private ValueBar valueBar;

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
    picker.setOldCenterColor(color);
    picker.setColor(color);
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
