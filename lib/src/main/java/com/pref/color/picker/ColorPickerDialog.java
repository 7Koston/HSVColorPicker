package com.pref.color.picker;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDialogFragmentCompat;
import com.pref.color.picker.v2.ColorPicker;
import com.pref.color.picker.v2.OpacityBar;
import com.pref.color.picker.v2.SVBar;

public class ColorPickerDialog extends PreferenceDialogFragmentCompat
    implements ColorPicker.OnColorChangedListener {

  private Context mContext;

  private ColorPicker picker;
  private SVBar svBar;
  private OpacityBar opacityBar;

  private ColorPreference mPreference;

  static ColorPickerDialog newInstance(String key) {
    ColorPickerDialog dialog = new ColorPickerDialog();
    Bundle args = new Bundle(1);
    args.putString(ARG_KEY, key);
    dialog.setArguments(args);
    return dialog;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getContext();
  }

  @Override
  protected void onBindDialogView(View view) {
    super.onBindDialogView(view);

    picker = view.findViewById(R.id.picker);
    svBar = view.findViewById(R.id.svbar);
    opacityBar = view.findViewById(R.id.opacitybar);

    mPreference = (ColorPreference) getPreference();
    int color = mPreference.getColor();
    picker.addSVBar(svBar);
    picker.addOpacityBar(opacityBar);
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
