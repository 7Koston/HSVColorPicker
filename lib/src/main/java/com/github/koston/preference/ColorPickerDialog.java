package com.github.koston.preference;

import android.os.Bundle;
import android.view.View;
import androidx.preference.PreferenceDialogFragmentCompat;
import com.github.koston.preference.view.ColorPicker;
import com.github.koston.preference.view.OpacityBar;
import com.github.koston.preference.view.SaturationValueBar;

public class ColorPickerDialog extends PreferenceDialogFragmentCompat
    implements ColorPicker.OnColorChangedListener {

  private ColorPicker picker;

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

    mPreference = (ColorPreference) getPreference();
    int color = mPreference.getColor();

    picker = view.findViewById(R.id.hue);
    SaturationValueBar saturationBar = view.findViewById(R.id.saturationBar);
    OpacityBar opacityBar = view.findViewById(R.id.opacityBar);
    SaturationValueBar valueBar = view.findViewById(R.id.valueBar);

    picker.addSaturationBar(saturationBar);
    picker.addValueBar(valueBar);
    picker.addOpacityBar(opacityBar);
    picker.setShowCenter(true);
    picker.setOldCenterColor(color);
    picker.setOnColorChangedListener(this);
    picker.initializeColor(color, ColorPicker.SOURCE_OUTSIDE);
  }

  private void initParams() {

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
