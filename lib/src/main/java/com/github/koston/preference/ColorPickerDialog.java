package com.github.koston.preference;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import androidx.preference.PreferenceDialogFragmentCompat;
import com.github.koston.preference.view.ColorPicker;
import com.github.koston.preference.view.OpacityBar;
import com.github.koston.preference.view.SaturationValueBar;

public class ColorPickerDialog extends PreferenceDialogFragmentCompat
    implements ColorPicker.OnColorChangedListener {

  private ColorPicker picker;
  private EditText hex;

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
    hex = view.findViewById(R.id.hex);
    SaturationValueBar saturationBar = view.findViewById(R.id.saturationBar);
    OpacityBar opacityBar = view.findViewById(R.id.opacityBar);
    SaturationValueBar valueBar = view.findViewById(R.id.valueBar);

    int barThickness = mPreference.getBarThickness();
    int barLength = mPreference.getBarLength();
    int barPointerRadius = mPreference.getBarPointerRadius();
    int barPointerHaloRadius = mPreference.getBarPointerHaloRadius();

    picker.setColorWheelRadius(mPreference.getColorWheelRadius());
    picker.setColorWheelThickness(mPreference.getColorWheelThickness());
    picker.setColorCenterRadius(mPreference.getColorCenterRadius());
    picker.setColorCenterHaloRadius(mPreference.getColorCenterHaloRadius());
    picker.setColorPointerRadius(mPreference.getColorPointerRadius());
    picker.setColorPointerHaloRadius(mPreference.getBarPointerHaloRadius());

    saturationBar.setBarThickness(barThickness);
    saturationBar.setBarLength(barLength);
    saturationBar.setBarPointerRadius(barPointerRadius);
    saturationBar.setBarPointerHaloRadius(barPointerHaloRadius);

    opacityBar.setBarThickness(barThickness);
    opacityBar.setBarLength(barLength);
    opacityBar.setBarPointerRadius(barPointerRadius);
    opacityBar.setBarPointerHaloRadius(barPointerHaloRadius);

    valueBar.setBarThickness(barThickness);
    valueBar.setBarLength(barLength);
    valueBar.setBarPointerRadius(barPointerRadius);
    valueBar.setBarPointerHaloRadius(barPointerHaloRadius);

    picker.addSaturationBar(saturationBar);
    picker.addValueBar(valueBar);
    picker.addOpacityBar(opacityBar);
    picker.setShowCenter(true);
    picker.setOldCenterColor(color);
    picker.setOnColorChangedListener(this);
    picker.initializeColor(color, ColorPicker.SOURCE_OUTSIDE);
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
    hex.setText("#" + Integer.toHexString(newColor).toUpperCase());
  }
}
