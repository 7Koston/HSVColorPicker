package com.pref.color.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.preference.DialogPreference;

public class ColorPreference extends DialogPreference {

  private int mColor;

  public ColorPreference(Context context) {
    super(context);
    init(context, null);
  }

  public ColorPreference(Context context, AttributeSet attrs) {
    super(context, attrs, R.attr.dialogPreferenceStyle);
    init(context, attrs);
  }

  @Override
  protected Object onGetDefaultValue(TypedArray a, int index) {
    return a.getInt(index, 0);
  }

  @Override
  public int getDialogLayoutResource() {
    return R.layout.dialog_color_picker;
  }

  public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    setPersistent(true);
  }

  public int getColor() {
    return mColor;
  }

  public void setColor(int color) {
    mColor = color;
    persistInt(mColor);
  }

  @Override
  protected void onClick() {
    getPreferenceManager().showDialog(this);
  }

  @Override
  @SuppressWarnings("deprecation")
  protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
    setColor(restorePersistedValue ? getPersistedInt(mColor) : (int) defaultValue);
  }
}
