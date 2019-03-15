package com.github.koston.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceViewHolder;

@SuppressWarnings("unused")
public class ColorPreference extends DialogPreference {

  private int mColor;
  private Drawable mDrawable;
  private boolean asIndicator;
  private ImageView ivIndicator;

  private int colorWheelThickness;
  private int colorWheelRadius;
  private int colorCenterRadius;
  private int colorCenterHaloRadius;
  private int colorPointerRadius;

  public ColorPreference(Context context) {
    super(context);
    init(context, null);
  }

  public ColorPreference(Context context, AttributeSet attrs) {
    super(context, attrs, R.attr.dialogPreferenceStyle);
    init(context, attrs);
  }

  public ColorPreference(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @Override
  protected Object onGetDefaultValue(TypedArray a, int index) {
    return a.getInt(index, 0);
  }

  @Override
  public void onBindViewHolder(PreferenceViewHolder holder) {
    ivIndicator = (ImageView) holder.findViewById(R.id.colorIndicator);
    super.onBindViewHolder(holder);
    setColor(mColor);
  }

  @Override
  public int getDialogLayoutResource() {
    return R.layout.dialog_color_picker;
  }

  private void init(Context context, AttributeSet attrs) {
    mDrawable = getContext().getResources().getDrawable(R.drawable.circle);

    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPreference);

      asIndicator = a.getBoolean(R.styleable.ColorPreference_indicatorColorPreview, true);

      a.recycle();
    }

    setWidgetLayoutResource(R.layout.preference_indicator);
  }

  public int getColor() {
    return mColor;
  }

  public void setColor(int color) {
    mColor = color;
    setIndicatorColor();
    persistInt(mColor);
  }

  private void setIndicatorColor() {
    if (asIndicator && ivIndicator != null) {
      mDrawable.setColorFilter(new PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_IN));
      ivIndicator.setImageDrawable(mDrawable);
    }
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
