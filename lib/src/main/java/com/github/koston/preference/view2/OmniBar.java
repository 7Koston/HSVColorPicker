/*
 * Copyright 2012 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.koston.preference.view2;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.github.koston.preference.R;

public class OmniBar extends View {

  private static final String TAG = "omniBar";

  /*
   * Constants used to save/restore the instance state.
   */
  private static final String STATE_PARENT = "parent";
  private static final String STATE_COLOR = "color";
  private static final String STATE_ALPHA = "alpha";
  /**
   * Constants used to identify orientation.
   */
  private static final boolean ORIENTATION_HORIZONTAL = true;
  private static final boolean ORIENTATION_VERTICAL = false;
  /**
   * Default orientation of the bar.
   */
  private static final boolean ORIENTATION_DEFAULT = ORIENTATION_HORIZONTAL;
  /*
   * Constant used to identify the type of bar
   */
  private int mType;
  /**
   * The thickness of the bar.
   */
  private int mBarThickness;
  /**
   * The length of the bar.
   */
  private int mBarLength;
  private int mPreferredBarLength;
  /**
   * The radius of the pointer.
   */
  private int mBarPointerRadius;
  /**
   * The radius of the halo of the pointer.
   */
  private int mBarPointerHaloRadius;
  /**
   * The position of the pointer on the bar.
   */
  private int mBarPointerPosition;
  /**
   * {@code Paint} instance used to draw the bar.
   */
  private Paint mBarPaint;
  /**
   * {@code Paint} instance used to draw the pointer.
   */
  private Paint mBarPointerPaint;
  /**
   * {@code Paint} instance used to draw the halo of the pointer.
   */
  private Paint mBarPointerHaloPaint;
  /**
   * The rectangle enclosing the bar.
   */
  private RectF mBarRect = new RectF();
  /**
   * {@code Shader} instance used to fill the shader of the paint.
   */
  private Shader shader;
  /**
   * {@code true} if the user clicked on the pointer to start the move mode. <br> {@code false} once
   * the user stops touching the screen.
   *
   * @see #onTouchEvent(android.view.MotionEvent)
   */
  private boolean mIsMovingPointer;
  /**
   * The alpha value of the currently selected color.
   */
  private int mAlpha;
  /**
   * An array of floats that can be built into a {@code Color} <br> Where we can extract the color
   * from.
   */
  private float[] mHSVColor = new float[3];
  /**
   * Factor used to calculate the position to the Omni-Value on the bar.
   */
  private float mPosToOmniFactor;
  /**
   * Factor used to calculate the Omni-Value to the postion on the bar.
   */
  private float mOmniToPosFactor;
  /**
   * {@code ColorPicker} instance used to control the ColorPicker.
   */
  private ColorPicker mPicker = null;
  /**
   * Used to toggle orientation between vertical and horizontal.
   */
  private boolean mOrientation;
  /**
   * Interface and listener so that changes in OmniBar are sent to the host activity/fragment
   */
  private OnOmniChangedListener onOmniChangedListener;
  /**
   * Omni-Value of the latest entry of the onOmniChangedListener.
   */
  private int oldChangedListenerColor;

  public OmniBar(Context context) {
    super(context);
    init(null, 0);
  }

  public OmniBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(attrs, 0);
  }

  public OmniBar(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(attrs, defStyle);
  }

  public int getType() {
    return mType;
  }

  public OnOmniChangedListener getOnOmniChangedListener() {
    return this.onOmniChangedListener;
  }

  public void setOnOmniChangedListener(OnOmniChangedListener listener) {
    this.onOmniChangedListener = listener;
  }

  private void init(AttributeSet attrs, int defStyle) {
    final TypedArray a =
        getContext().obtainStyledAttributes(attrs, R.styleable.ColorBars, defStyle, 0);
    final TypedArray b =
        getContext().obtainStyledAttributes(attrs, R.styleable.OmniBar, defStyle, 0);
    final Resources c = getContext().getResources();

    int type = b.getInt(R.styleable.OmniBar_bar_type, ColorPicker.SOURCE_OUTSIDE);
    if (type == ColorPicker.TYPE_SATURATION || type == ColorPicker.TYPE_VALUE) {
      mType = type;
    } else {
      Log.w(TAG, "assign 'bar_type' in XML Layout, OmniBar otherwise inoperable");
    }

    b.recycle();

    mBarThickness =
        a.getDimensionPixelSize(
            R.styleable.ColorBars_bar_thickness, c.getDimensionPixelSize(R.dimen.bar_thickness));
    mBarLength =
        a.getDimensionPixelSize(
            R.styleable.ColorBars_bar_length, c.getDimensionPixelSize(R.dimen.bar_length));
    mPreferredBarLength = mBarLength;
    mBarPointerRadius =
        a.getDimensionPixelSize(
            R.styleable.ColorBars_bar_pointer_radius,
            c.getDimensionPixelSize(R.dimen.bar_pointer_radius));
    mBarPointerHaloRadius =
        a.getDimensionPixelSize(
            R.styleable.ColorBars_bar_pointer_halo_radius,
            c.getDimensionPixelSize(R.dimen.bar_pointer_halo_radius));
    mOrientation =
        a.getBoolean(R.styleable.ColorBars_bar_orientation_horizontal, ORIENTATION_DEFAULT);

    a.recycle();

    mBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mBarPaint.setShader(shader);

    mBarPointerPosition = mBarLength + mBarPointerHaloRadius;

    mBarPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mBarPointerHaloPaint.setColor(Color.BLACK);
    mBarPointerHaloPaint.setAlpha(0x50);

    mBarPointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mBarPointerPaint.setColor(0xff81ff00);

    mPosToOmniFactor = 1 / ((float) mBarLength);
    mOmniToPosFactor = ((float) mBarLength) / 1;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int intrinsicSize = mPreferredBarLength + (mBarPointerHaloRadius * 2);

    // Variable orientation
    int measureSpec;
    if (mOrientation == ORIENTATION_HORIZONTAL) {
      measureSpec = widthMeasureSpec;
    } else {
      measureSpec = heightMeasureSpec;
    }
    int lengthMode = MeasureSpec.getMode(measureSpec);
    int lengthSize = MeasureSpec.getSize(measureSpec);

    int length;
    if (lengthMode == MeasureSpec.EXACTLY) {
      length = lengthSize;
    } else if (lengthMode == MeasureSpec.AT_MOST) {
      length = Math.min(intrinsicSize, lengthSize);
    } else {
      length = intrinsicSize;
    }

    int barPointerHaloRadiusx2 = mBarPointerHaloRadius * 2;
    mBarLength = length - barPointerHaloRadiusx2;
    if (mOrientation == ORIENTATION_VERTICAL) {
      setMeasuredDimension(barPointerHaloRadiusx2, (mBarLength + barPointerHaloRadiusx2));
    } else {
      setMeasuredDimension((mBarLength + barPointerHaloRadiusx2), barPointerHaloRadiusx2);
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);

    // Fill the rectangle instance based on orientation
    int x1, y1;
    if (mOrientation == ORIENTATION_HORIZONTAL) {
      x1 = (mBarLength + mBarPointerHaloRadius);
      y1 = mBarThickness;
      mBarLength = w - (mBarPointerHaloRadius * 2);
      mBarRect.set(
          mBarPointerHaloRadius,
          (mBarPointerHaloRadius - (mBarThickness / 2)),
          (mBarLength + (mBarPointerHaloRadius)),
          (mBarPointerHaloRadius + (mBarThickness / 2)));
    } else {
      x1 = mBarThickness;
      y1 = (mBarLength + mBarPointerHaloRadius);
      mBarLength = h - (mBarPointerHaloRadius * 2);
      mBarRect.set(
          (mBarPointerHaloRadius - (mBarThickness / 2)),
          mBarPointerHaloRadius,
          (mBarPointerHaloRadius + (mBarThickness / 2)),
          (mBarLength + (mBarPointerHaloRadius)));
    }

    // Update variables that depend of mBarLength.
    if (!isInEditMode()) {
      shader =
          new LinearGradient(
              mBarPointerHaloRadius,
              0,
              x1,
              y1,
              new int[]{Color.WHITE, Color.HSVToColor(0xFF, mHSVColor)},
              null,
              Shader.TileMode.CLAMP);
    } else {
      shader =
          new LinearGradient(
              mBarPointerHaloRadius,
              0,
              x1,
              y1,
              new int[]{Color.WHITE, 0xff81ff00},
              null,
              Shader.TileMode.CLAMP);
      Color.colorToHSV(0xff81ff00, mHSVColor);
    }

    mBarPaint.setShader(shader);
    mPosToOmniFactor = 1 / ((float) mBarLength);
    mOmniToPosFactor = ((float) mBarLength) / 1;

    if (!isInEditMode()) {
      mBarPointerPosition =
          Math.round((mOmniToPosFactor * mHSVColor[mType]) + mBarPointerHaloRadius);
    } else {
      mBarPointerPosition = mBarLength + mBarPointerHaloRadius;
    }
  }

  @Override
  protected void onDraw(Canvas canvas) {
    // Draw the bar.
    canvas.drawRect(mBarRect, mBarPaint);

    // Calculate the center of the pointer.
    int cX, cY;
    if (mOrientation == ORIENTATION_HORIZONTAL) {
      cX = mBarPointerPosition;
      cY = mBarPointerHaloRadius;
    } else {
      cX = mBarPointerHaloRadius;
      cY = mBarPointerPosition;
    }

    // Draw the pointer halo.
    canvas.drawCircle(cX, cY, mBarPointerHaloRadius, mBarPointerHaloPaint);
    // Draw the pointer.
    canvas.drawCircle(cX, cY, mBarPointerRadius, mBarPointerPaint);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    getParent().requestDisallowInterceptTouchEvent(true);

    // Convert coordinates to our internal coordinate system
    float dimen;
    if (mOrientation == ORIENTATION_HORIZONTAL) {
      dimen = event.getX();
    } else {
      dimen = event.getY();
    }

    switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
        mIsMovingPointer = true;
        // Check whether the user pressed on (or near) the pointer
        if (dimen >= (mBarPointerHaloRadius) && dimen <= (mBarPointerHaloRadius + mBarLength)) {
          mBarPointerPosition = Math.round(dimen);
          setOmniValueFromCoord(dimen);
          invalidate();
        }
        break;
      case MotionEvent.ACTION_MOVE:
        if (mIsMovingPointer) {
          // Move the the pointer on the bar.
          // Touch Event happens on the bar inside the end points
          if (dimen >= mBarPointerHaloRadius && dimen <= (mBarPointerHaloRadius + mBarLength)) {
            mBarPointerPosition = Math.round(dimen);
            setOmniValueFromCoord(dimen);
            setColor(mHSVColor);
            invalidate();

            // Touch event happens on the start point or to the left of it.
          } else if (dimen < mBarPointerHaloRadius) {
            mBarPointerPosition = mBarPointerHaloRadius;
            setOmniValue(0);
            setColor(mHSVColor);
            invalidate();

            // Touch event happens to the right of the end point
          } else if (dimen > (mBarPointerHaloRadius - mBarLength)) {
            mBarPointerPosition = mBarPointerHaloRadius + mBarLength;
            setOmniValue(1);
            setColor(mHSVColor);
            invalidate();
          }
        }
        int rgbCol = getDisplayColor(mHSVColor);
        if (onOmniChangedListener != null && oldChangedListenerColor != rgbCol) {
          onOmniChangedListener.onOmniChanged(rgbCol);
          oldChangedListenerColor = rgbCol;
        }

        break;
      case MotionEvent.ACTION_UP:
        mIsMovingPointer = false;
        break;
    }
    return true;
  }

  public void initializeColor(int alpha, float[] color) {
    mAlpha = alpha;
    mBarPointerPosition = Math.round(((mOmniToPosFactor * color[mType])) + mBarPointerHaloRadius);
    setColor(color, true);
  }

  private void setColor(float[] color) {
    setColor(color, false);
  }

  private void setColor(float[] color, boolean initialize) {
    int x1, y1;
    if (mOrientation == ORIENTATION_HORIZONTAL) {
      x1 = (mBarLength + mBarPointerHaloRadius);
      y1 = mBarThickness;
    } else {
      x1 = mBarThickness;
      y1 = (mBarLength + mBarPointerHaloRadius);
    }
    System.arraycopy(color, 0, mHSVColor, 0, 3);

    shader =
        new LinearGradient(
            mBarPointerHaloRadius,
            0,
            x1,
            y1,
            new int[]{getDisplayColor(color, 0), getDisplayColor(color, 1)},
            null,
            Shader.TileMode.CLAMP);
    mBarPaint.setShader(shader);

    mBarPointerPaint.setColor(getDisplayColor(color));
    if (!initialize) {
      if (mPicker != null) {
        mPicker.setColor(mAlpha, mHSVColor, mType);
      }
    }
    invalidate();
  }

  private void setOmniValue(float omni) {
    mHSVColor[mType] = omni;
  }

  private int getDisplayColor(float[] color) {
    return getDisplayColor(color, color[mType]);
  }

  private int getDisplayColor(float[] color, float omni) {
    float[] col = new float[3];
    System.arraycopy(color, 0, col, 0, 3);
    col[mType] = omni;
    int rgbColor = Color.HSVToColor(mAlpha, col);
    return rgbColor;
  }

  /**
   * Calculate the color selected by the pointer on the bar.
   *
   * @param coord Coordinate of the pointer.
   */
  private void setOmniValueFromCoord(float coord) {
    coord = coord - mBarPointerHaloRadius;
    if (coord < 0) {
      coord = 0;
    } else if (coord > mBarLength) {
      coord = mBarLength;
    }
    float omni = mPosToOmniFactor * coord;
    setOmniValue(omni);
  }

  /**
   * Adds a {@code ColorPicker} instance to the bar. <br>
   * <br>
   * WARNING: Don't change the color picker. it is done already when the bar is added to the
   * ColorPicker
   */
  public void setColorPicker(ColorPicker picker) {
    mPicker = picker;
  }

  @Override
  protected Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();

    Bundle state = new Bundle();
    state.putParcelable(STATE_PARENT, superState);
    state.putInt(STATE_ALPHA, mAlpha);
    state.putFloatArray(STATE_COLOR, mHSVColor);

    return state;
  }

  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    Bundle savedState = (Bundle) state;

    Parcelable superState = savedState.getParcelable(STATE_PARENT);
    super.onRestoreInstanceState(superState);

    initializeColor(savedState.getInt(STATE_ALPHA), savedState.getFloatArray(STATE_COLOR));
  }

  private void logHSV(String source, float[] mHSVColor) {
    Log.d(TAG, source + ": " + mHSVColor[0] + "/" + mHSVColor[1] + "/" + mHSVColor[2]);
  }

  public interface OnOmniChangedListener {

    void onOmniChanged(int omni);
  }
}
