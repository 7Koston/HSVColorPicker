package com.github.koston.preference;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import androidx.core.content.res.ResourcesCompat;
import androidx.preference.DialogPreference;
import androidx.preference.PreferenceViewHolder;

@SuppressWarnings("unused")
public class ColorPreference extends DialogPreference {

  private int mColor;
  private Drawable mDrawable;
  private boolean asIndicator;
  private ImageView ivIndicator;

  private int pointersHaloColor;

  private int colorWheelThickness;
  private int colorWheelRadius;
  private int colorCenterRadius;
  private int colorCenterHaloRadius;
  private int colorPointerRadius;
  private int colorPointerHaloRadius;

  private int barThickness;
  private int barLength;
  private int barPointerRadius;
  private int barPointerHaloRadius;

  public ColorPreference(Context context) {
    super(context);
    init(context, null);
  }

  private void init(Context context, AttributeSet attrs) {
    Resources r = context.getResources();
    Theme t = getContext().getTheme();
    mDrawable = ResourcesCompat.getDrawable(r, R.drawable.circle, t);

    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPreference);

      asIndicator = a.getBoolean(R.styleable.ColorPreference_indicatorColorPreview, true);

      colorWheelThickness = a.getDimensionPixelSize(R.styleable.ColorPreference_hueWheelThickness,
          r.getDimensionPixelSize(R.dimen.defaultWheelThickness));
      colorWheelRadius = a.getDimensionPixelSize(R.styleable.ColorPreference_hueWheelRadius,
          r.getDimensionPixelSize(R.dimen.defaultWheelRadius));
      colorCenterRadius = a.getDimensionPixelSize(R.styleable.ColorPreference_hueCenterCircleRadius,
          r.getDimensionPixelSize(R.dimen.defaultCenterRadius));
      colorCenterHaloRadius = a.getDimensionPixelSize(
          R.styleable.ColorPreference_hueCenterCircleHaloRadius,
          r.getDimensionPixelSize(R.dimen.defaultCenterHaloRadius));
      colorPointerRadius = a.getDimensionPixelSize(R.styleable.ColorPreference_huePointerRadius,
          r.getDimensionPixelSize(R.dimen.defaultPointerRadius));
      colorPointerHaloRadius = a.getDimensionPixelSize(
          R.styleable.ColorPreference_huePointerHaloRadius,
          r.getDimensionPixelSize(R.dimen.defaultPointerHaloRadius));

      barThickness = a.getDimensionPixelSize(R.styleable.ColorPreference_barsThickness,
          r.getDimensionPixelSize(R.dimen.defaultBarThickness));
      barLength = a.getDimensionPixelSize(R.styleable.ColorPreference_barsLength,
          r.getDimensionPixelSize(R.dimen.defaultBarLength));
      barPointerRadius = a.getDimensionPixelSize(R.styleable.ColorPreference_barsPointerRadius,
          r.getDimensionPixelSize(R.dimen.defaultBarPointerRadius));
      barPointerHaloRadius = a.getDimensionPixelSize(
          R.styleable.ColorPreference_barsPointerHaloRadius,
          r.getDimensionPixelSize(R.dimen.defaultBarPointerHaloRadius));
      pointersHaloColor = a.getColor(R.styleable.ColorPreference_pointersHaloColor,
          ResourcesCompat.getColor(r, R.color.defaultPointerHaloColor, t));

      a.recycle();
    }

    setWidgetLayoutResource(R.layout.preference_indicator);
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

  private void setIndicatorColor() {
    if (asIndicator && ivIndicator != null) {
      mDrawable.setColorFilter(new PorterDuffColorFilter(mColor, PorterDuff.Mode.SRC_IN));
      ivIndicator.setImageDrawable(mDrawable);
    }
  }

  @Override
  @SuppressWarnings("deprecation")
  protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
    setColor(restorePersistedValue ? getPersistedInt(mColor) : (int) defaultValue);
  }

  @Override
  public int getDialogLayoutResource() {
    return R.layout.dialog_color_picker;
  }

  @Override
  protected void onClick() {
    getPreferenceManager().showDialog(this);
  }

  public int getColor() {
    return mColor;
  }

  public void setColor(int color) {
    mColor = color;
    setIndicatorColor();
    persistInt(mColor);
  }

  public int getColorWheelThickness() {
    return colorWheelThickness;
  }

  public int getColorWheelRadius() {
    return colorWheelRadius;
  }

  public int getColorCenterRadius() {
    return colorCenterRadius;
  }

  public int getColorCenterHaloRadius() {
    return colorCenterHaloRadius;
  }

  public int getColorPointerRadius() {
    return colorPointerRadius;
  }

  public int getColorPointerHaloRadius() {
    return colorPointerHaloRadius;
  }

  public int getBarThickness() {
    return barThickness;
  }

  public int getBarLength() {
    return barLength;
  }

  public int getBarPointerRadius() {
    return barPointerRadius;
  }

  public int getBarPointerHaloRadius() {
    return barPointerHaloRadius;
  }

  public int getPointersHaloColor() {
    return pointersHaloColor;
  }
}
