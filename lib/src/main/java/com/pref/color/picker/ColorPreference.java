package com.pref.color.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceViewHolder;

@SuppressWarnings("unused")
public class ColorPreference extends DialogPreference {

  private int mColor;
  private Drawable mDrawable;

  private boolean asIndicator, asIcon;

  private ImageView ivIndicator;

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
  public void onBindViewHolder(PreferenceViewHolder holder) {
    super.onBindViewHolder(holder);

    ivIndicator = (ImageView) holder.findViewById(R.id.colorIndicator);

    if (ivIndicator != null && asIndicator) {
      ivIndicator.setVisibility(View.VISIBLE);
      setColor(mColor);
    }
    if (ivIndicator != null && !asIndicator) {
      ivIndicator.setVisibility(View.INVISIBLE);
    }
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
    mDrawable = getContext().getResources().getDrawable(R.drawable.circle);

    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPreference);

      asIcon = a.getBoolean(R.styleable.ColorPreference_iconColorPreview, false);
      asIndicator = a.getBoolean(R.styleable.ColorPreference_indicatorColorPreview, false);

      a.recycle();
    }

    setWidgetLayoutResource(R.layout.preference_layout);
  }

  public int getColor() {
    return mColor;
  }

  public void setColor(int color) {
    mColor = color;
    persistInt(mColor);
    setIndicatorColor();
  }

  private void setIndicatorColor() {
    mDrawable.setColorFilter(new PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_IN));
    if (asIcon) {
      setIcon(mDrawable);
    }
    if (asIndicator && ivIndicator != null) {
      ivIndicator.setImageDrawable(mDrawable);
    }
    notifyChanged();
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
