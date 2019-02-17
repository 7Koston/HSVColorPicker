package com.pref.color.picker;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceDialogFragmentCompat;

public class ColorPickerDialog extends PreferenceDialogFragmentCompat
    implements ColorPickerView.OnColorChangedListener, TextWatcher {

  private Context mContext;

  private ColorPickerView mColorPicker;
  private ColorPanelView mNewColorPanel;
  private EditText mHexEdit;

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

    mColorPicker = view.findViewById(R.id.color_picker);
    ColorPanelView oldColorPanel = view.findViewById(R.id.old_color_panel);
    mNewColorPanel = view.findViewById(R.id.new_color_panel);
    mHexEdit = view.findViewById(R.id.edit_hex);

    mPreference = (ColorPreference) getPreference();
    int color = mPreference.getColor();
    mColorPicker.setColor(color, true);
    oldColorPanel.setColor(color);
    mNewColorPanel.setColor(color);
    mHexEdit.setText(String.format("%08X", color));

    mColorPicker.setAlphaSliderVisible(true);
    mColorPicker.setOnColorChangedListener(this);
    mHexEdit.addTextChangedListener(this);
  }

  @Override
  public void onDialogClosed(boolean positiveResult) {
    if (positiveResult) {
      int color = mColorPicker.getColor();
      if (mPreference.callChangeListener(color)) {
        mPreference.setColor(color);
      }
    }
  }

  @Override
  public void onColorChanged(int newColor) {
    mNewColorPanel.setColor(newColor);
    mHexEdit.setText(String.format("%08X", newColor));

    if (mHexEdit.hasFocus()) {
      InputMethodManager imm =
          (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
      imm.hideSoftInputFromWindow(mHexEdit.getWindowToken(), 0);
      mHexEdit.clearFocus();
    }
  }

  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {}

  @Override
  public void afterTextChanged(Editable s) {
    if (mHexEdit.isFocused()) {
      int color = Util.convertToColorInt(s.toString());
      if (color != mColorPicker.getColor()) {
        mColorPicker.setColor(color, false);
        mNewColorPanel.setColor(color);
      }
    }
  }
}
