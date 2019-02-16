package com.pref.color.picker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * Displays a color picker to the user and allow them
 * to select a color. A slider for the alpha channel is
 * also available. Enable it by setting
 * setAlphaSliderVisible(boolean) to true.
 *
 * @author Daniel Nilsson
 * @author u1aryz
 * @author 7Koston
 */
public class ColorPickerView extends View {

  private final static int DEFAULT_BORDER_COLOR = 0xFF6E6E6E;
  private final static int DEFAULT_SLIDER_COLOR = 0xFFBDBDBD;

  private final static int HUE_PANEL_WIDTH_DP = 30;
  private final static int ALPHA_PANEL_HEIGHT_DP = 20;
  private final static int PANEL_SPACING_DP = 10;
  private final static int CIRCLE_TRACKER_RADIUS_DP = 5;
  private final static int SLIDER_TRACKER_SIZE_DP = 4;
  private final static int SLIDER_TRACKER_OFFSET_DP = 2;

  /**
   * The width in pixels of the border
   * surrounding all color panels.
   */
  private final static int BORDER_WIDTH_PX = 1;

  /**
   * The width in px of the hue panel.
   */
  private int mHuePanelWidthPx;
  /**
   * The height in px of the alpha panel
   */
  private int mAlphaPanelHeightPx;
  /**
   * The distance in px between the different
   * color panels.
   */
  private int mPanelSpacingPx;
  /**
   * The radius in px of the color palette tracker circle.
   */
  private int mCircleTrackerRadiusPx;
  /**
   * The px which the tracker of the hue or alpha panel
   * will extend outside of its bounds.
   */
  private int mSliderTrackerOffsetPx;
  /**
   * Height of slider tracker on hue panel,
   * width of slider on alpha panel.
   */
  private int mSliderTrackerSizePx;

  private Paint mSatValPaint;
  private Paint mSatValTrackerPaint;

  private Paint mAlphaPaint;
  private Paint mAlphaTextPaint;
  private Paint mHueAlphaTrackerPaint;

  private Paint mBorderPaint;

  private Shader mValShader;
  private Shader mSatShader;
  private Shader mAlphaShader;

  /**
   * We cache a bitmap of the sat/val panel which is expensive to draw each time.
   * We can reuse it when the user is sliding the circle picker as long as the hue isn't changed.
   */
  private BitmapCache mSatValBackgroundCache;
  /**
   * We cache the hue background to since its also very expensive now.
   */
  private BitmapCache mHueBackgroundCache;

  /** Current values */
  private int mAlpha = 0xff;
  private float mHue = 360f;
  private float mSat = 0f;
  private float mVal = 0f;

  private boolean mShowAlphaPanel = false;
  private String mAlphaSliderText = null;
  private int mSliderTrackerColor = DEFAULT_SLIDER_COLOR;
  private int mBorderColor = DEFAULT_BORDER_COLOR;

  /**
   * Minimum required padding. The offset from the
   * edge we must have or else the finger tracker will
   * get clipped when it's drawn outside of the view.
   */
  private int mMinimumPadding;

  /**
   * The Rect in which we are allowed to draw.
   * Trackers can extend outside slightly,
   * due to the required padding we have set.
   */
  private Rect mDrawingRect;

  private Rect mSatValRect;
  private Rect mHueRect;
  private Rect mAlphaRect;

  private Point mStartTouchPoint = null;

  private AlphaPatternDrawable mAlphaPattern;
  private OnColorChangedListener mListener;

  public ColorPickerView(Context context) {
    this(context, null);
  }

  public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  @Override public Parcelable onSaveInstanceState() {
    Bundle state = new Bundle();
    state.putParcelable("instanceState", super.onSaveInstanceState());
    state.putInt("alpha", mAlpha);
    state.putFloat("hue", mHue);
    state.putFloat("sat", mSat);
    state.putFloat("val", mVal);
    state.putBoolean("show_alpha", mShowAlphaPanel);
    state.putString("alpha_text", mAlphaSliderText);
    return state;
  }

  @Override public void onRestoreInstanceState(Parcelable state) {
    if (state instanceof Bundle) {
      Bundle bundle = (Bundle) state;
      mAlpha = bundle.getInt("alpha");
      mHue = bundle.getFloat("hue");
      mSat = bundle.getFloat("sat");
      mVal = bundle.getFloat("val");
      mShowAlphaPanel = bundle.getBoolean("show_alpha");
      mAlphaSliderText = bundle.getString("alpha_text");
      state = bundle.getParcelable("instanceState");
    }
    super.onRestoreInstanceState(state);
  }

  private void init(Context context, @Nullable AttributeSet attrs) {
    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView);

      try {
        mShowAlphaPanel = a.getBoolean(R.styleable.ColorPickerView_alphaChannelVisible, false);
        mAlphaSliderText = a.getString(R.styleable.ColorPickerView_alphaChannelText);
        mSliderTrackerColor =
            a.getColor(R.styleable.ColorPickerView_sliderColor, DEFAULT_SLIDER_COLOR);
        mBorderColor = a.getColor(R.styleable.ColorPickerView_borderColor, DEFAULT_BORDER_COLOR);
      } finally {
        a.recycle();
      }

      applyThemeColors(context);
    }

    mHuePanelWidthPx = Util.dpToPx(context, HUE_PANEL_WIDTH_DP);
    mAlphaPanelHeightPx = Util.dpToPx(context, ALPHA_PANEL_HEIGHT_DP);
    mPanelSpacingPx = Util.dpToPx(context, PANEL_SPACING_DP);
    mCircleTrackerRadiusPx = Util.dpToPx(context, CIRCLE_TRACKER_RADIUS_DP);
    mSliderTrackerSizePx = Util.dpToPx(context, SLIDER_TRACKER_SIZE_DP);
    mSliderTrackerOffsetPx = Util.dpToPx(context, SLIDER_TRACKER_OFFSET_DP);

    mMinimumPadding = getResources().getDimensionPixelSize(R.dimen.color_picker_view_min_padding);

    initPaintTools(context);

    //Needed for receiving trackball motion events.
    setFocusable(true);
    setFocusableInTouchMode(true);
  }

  private void applyThemeColors(Context context) {
    // If no specific border/slider color has been
    // set we take the default secondary text color
    // as border/slider color. Thus it will adopt
    // to theme changes automatically.

    final TypedValue value = new TypedValue();
    TypedArray a =
        context.obtainStyledAttributes(value.data, new int[] { android.R.attr.textColorSecondary });

    try {
      if (mBorderColor == DEFAULT_BORDER_COLOR) {
        mBorderColor = a.getColor(0, DEFAULT_BORDER_COLOR);
      }

      if (mSliderTrackerColor == DEFAULT_SLIDER_COLOR) {
        mSliderTrackerColor = a.getColor(0, DEFAULT_SLIDER_COLOR);
      }
    } finally {
      a.recycle();
    }
  }

  private void initPaintTools(Context context) {
    mSatValPaint = new Paint();
    mSatValTrackerPaint = new Paint();
    mHueAlphaTrackerPaint = new Paint();
    mAlphaPaint = new Paint();
    mAlphaTextPaint = new Paint();
    mBorderPaint = new Paint();

    mSatValTrackerPaint.setStyle(Paint.Style.STROKE);
    mSatValTrackerPaint.setStrokeWidth(Util.dpToPx(context, 2));
    mSatValTrackerPaint.setAntiAlias(true);

    mHueAlphaTrackerPaint.setColor(mSliderTrackerColor);
    mHueAlphaTrackerPaint.setStyle(Paint.Style.STROKE);
    mHueAlphaTrackerPaint.setStrokeWidth(Util.dpToPx(context, 2));
    mHueAlphaTrackerPaint.setAntiAlias(true);

    mAlphaTextPaint.setColor(0xff1c1c1c);
    mAlphaTextPaint.setTextSize(Util.dpToPx(context, 14));
    mAlphaTextPaint.setAntiAlias(true);
    mAlphaTextPaint.setTextAlign(Paint.Align.CENTER);
    mAlphaTextPaint.setFakeBoldText(true);
  }

  @Override public int getPaddingTop() {
    return Math.max(super.getPaddingTop(), mMinimumPadding);
  }

  @Override public int getPaddingBottom() {
    return Math.max(super.getPaddingBottom(), mMinimumPadding);
  }

  @Override public int getPaddingLeft() {
    return Math.max(super.getPaddingLeft(), mMinimumPadding);
  }

  @Override public int getPaddingRight() {
    return Math.max(super.getPaddingRight(), mMinimumPadding);
  }

  @Override protected void onDraw(Canvas canvas) {
    if (mDrawingRect.width() <= 0 || mDrawingRect.height() <= 0) {
      return;
    }

    drawSatValPanel(canvas);
    drawHuePanel(canvas);
    drawAlphaPanel(canvas);
  }

  private void drawSatValPanel(Canvas canvas) {
    final Rect rect = mSatValRect;

    mBorderPaint.setColor(mBorderColor);
    canvas.drawRect(mDrawingRect.left, mDrawingRect.top, rect.right + BORDER_WIDTH_PX,
        rect.bottom + BORDER_WIDTH_PX, mBorderPaint);

    if (mValShader == null) {
      //Black gradient has either not been created or the view has been resized.
      mValShader =
          new LinearGradient(rect.left, rect.top, rect.left, rect.bottom, 0xffffffff, 0xff000000,
              Shader.TileMode.CLAMP);
    }

    //If the hue has changed we need to recreate the cache.
    if (mSatValBackgroundCache == null || mSatValBackgroundCache.value != mHue) {

      if (mSatValBackgroundCache == null) {
        mSatValBackgroundCache = new BitmapCache();
      }

      //We create our bitmap in the cache if it doesn't exist.
      if (mSatValBackgroundCache.bitmap == null) {
        mSatValBackgroundCache.bitmap =
            Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
      }

      //We create the canvas once so we can draw on our bitmap and the hold on to it.
      if (mSatValBackgroundCache.canvas == null) {
        mSatValBackgroundCache.canvas = new Canvas(mSatValBackgroundCache.bitmap);
      }

      int rgb = Color.HSVToColor(new float[] { mHue, 1f, 1f });

      mSatShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, 0xffffffff, rgb,
          Shader.TileMode.CLAMP);

      ComposeShader mShader = new ComposeShader(mValShader, mSatShader, PorterDuff.Mode.MULTIPLY);
      mSatValPaint.setShader(mShader);

      // Finally we draw on our canvas, the result will be
      // stored in our bitmap which is already in the cache.
      // Since this is drawn on a canvas not rendered on
      // screen it will automatically not be using the
      // hardware acceleration. And this was the code that
      // wasn't supported by hardware acceleration which mean
      // there is no need to turn it of anymore. The rest of
      // the view will still be hw accelerated.
      mSatValBackgroundCache.canvas.drawRect(0, 0, mSatValBackgroundCache.bitmap.getWidth(),
          mSatValBackgroundCache.bitmap.getHeight(), mSatValPaint);

      //We set the hue value in our cache to which hue it was drawn with,
      //then we know that if it hasn't changed we can reuse our cached bitmap.
      mSatValBackgroundCache.value = mHue;
    }

    // We draw our bitmap from the cached, if the hue has changed
    // then it was just recreated otherwise the old one will be used.
    canvas.drawBitmap(mSatValBackgroundCache.bitmap, null, rect, null);

    Point p = satValToPoint(mSat, mVal);

    mSatValTrackerPaint.setColor(0xff000000);
    canvas.drawCircle(p.x, p.y, mCircleTrackerRadiusPx - Util.dpToPx(getContext(), 1),
        mSatValTrackerPaint);

    mSatValTrackerPaint.setColor(0xffdddddd);
    canvas.drawCircle(p.x, p.y, mCircleTrackerRadiusPx, mSatValTrackerPaint);
  }

  private void drawHuePanel(Canvas canvas) {
    final Rect rect = mHueRect;

    mBorderPaint.setColor(mBorderColor);

    canvas.drawRect(rect.left - BORDER_WIDTH_PX, rect.top - BORDER_WIDTH_PX,
        rect.right + BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX, mBorderPaint);

    if (mHueBackgroundCache == null) {
      mHueBackgroundCache = new BitmapCache();
      mHueBackgroundCache.bitmap =
          Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
      mHueBackgroundCache.canvas = new Canvas(mHueBackgroundCache.bitmap);

      int[] hueColors = new int[(int) (rect.height() + 0.5f)];

      // Generate array of all colors, will be drawn as individual lines.
      float h = 360f;
      for (int i = 0; i < hueColors.length; i++) {
        hueColors[i] = Color.HSVToColor(new float[] { h, 1f, 1f });
        h -= 360f / hueColors.length;
      }

      // Time to draw the hue color gradient,
      // its drawn as individual lines which
      // will be quite many when the resolution is high
      // and/or the panel is large.
      Paint linePaint = new Paint();
      linePaint.setStrokeWidth(0);
      for (int i = 0; i < hueColors.length; i++) {
        linePaint.setColor(hueColors[i]);
        mHueBackgroundCache.canvas.drawLine(0, i, mHueBackgroundCache.bitmap.getWidth(), i,
            linePaint);
      }
    }

    canvas.drawBitmap(mHueBackgroundCache.bitmap, null, rect, null);

    Point p = hueToPoint(mHue);

    RectF r = new RectF();
    r.left = rect.left - mSliderTrackerOffsetPx;
    r.right = rect.right + mSliderTrackerOffsetPx;
    r.top = p.y - (mSliderTrackerSizePx / 2f);
    r.bottom = p.y + (mSliderTrackerSizePx / 2f);

    canvas.drawRoundRect(r, 2, 2, mHueAlphaTrackerPaint);
  }

  private void drawAlphaPanel(Canvas canvas) {
    /*
     * Will be drawn with hw acceleration, very fast.
		 * Also the AlphaPatternDrawable is backed by a bitmap
		 * generated only once if the size does not change.
		 */

    if (!mShowAlphaPanel || mAlphaRect == null || mAlphaPattern == null) return;

    final Rect rect = mAlphaRect;

    mBorderPaint.setColor(mBorderColor);
    canvas.drawRect(rect.left - BORDER_WIDTH_PX, rect.top - BORDER_WIDTH_PX,
        rect.right + BORDER_WIDTH_PX, rect.bottom + BORDER_WIDTH_PX, mBorderPaint);

    mAlphaPattern.draw(canvas);

    float[] hsv = new float[] { mHue, mSat, mVal };
    int color = Color.HSVToColor(hsv);
    int acolor = Color.HSVToColor(0, hsv);

    mAlphaShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top, color, acolor,
        Shader.TileMode.CLAMP);

    mAlphaPaint.setShader(mAlphaShader);

    canvas.drawRect(rect, mAlphaPaint);

    if (mAlphaSliderText != null && !mAlphaSliderText.equals("")) {
      canvas.drawText(mAlphaSliderText, rect.centerX(),
          rect.centerY() + Util.dpToPx(getContext(), 4), mAlphaTextPaint);
    }

    Point p = alphaToPoint(mAlpha);

    RectF r = new RectF();
    r.left = p.x - (mSliderTrackerSizePx / 2f);
    r.right = p.x + (mSliderTrackerSizePx / 2f);
    r.top = rect.top - mSliderTrackerOffsetPx;
    r.bottom = rect.bottom + mSliderTrackerOffsetPx;

    canvas.drawRoundRect(r, 2, 2, mHueAlphaTrackerPaint);
  }

  private Point hueToPoint(float hue) {

    final Rect rect = mHueRect;
    final float height = rect.height();

    Point p = new Point();

    p.y = (int) (height - (hue * height / 360f) + rect.top);
    p.x = rect.left;

    return p;
  }

  private Point satValToPoint(float sat, float val) {

    final Rect rect = mSatValRect;
    final float height = rect.height();
    final float width = rect.width();

    Point p = new Point();

    p.x = (int) (sat * width + rect.left);
    p.y = (int) ((1f - val) * height + rect.top);

    return p;
  }

  private Point alphaToPoint(int alpha) {

    final Rect rect = mAlphaRect;
    final float width = rect.width();

    Point p = new Point();

    p.x = (int) (width - (alpha * width / 0xff) + rect.left);
    p.y = rect.top;

    return p;
  }

  private float[] pointToSatVal(float x, float y) {

    final Rect rect = mSatValRect;
    float[] result = new float[2];

    float width = rect.width();
    float height = rect.height();

    if (x < rect.left) {
      x = 0f;
    } else if (x > rect.right) {
      x = width;
    } else {
      x = x - rect.left;
    }

    if (y < rect.top) {
      y = 0f;
    } else if (y > rect.bottom) {
      y = height;
    } else {
      y = y - rect.top;
    }

    result[0] = 1.f / width * x;
    result[1] = 1.f - (1.f / height * y);

    return result;
  }

  private float pointToHue(float y) {

    final Rect rect = mHueRect;

    float height = rect.height();

    if (y < rect.top) {
      y = 0f;
    } else if (y > rect.bottom) {
      y = height;
    } else {
      y = y - rect.top;
    }

    return 360f - (y * 360f / height);
  }

  private int pointToAlpha(int x) {

    final Rect rect = mAlphaRect;
    final int width = rect.width();

    if (x < rect.left) {
      x = 0;
    } else if (x > rect.right) {
      x = width;
    } else {
      x = x - rect.left;
    }

    return 0xff - (x * 0xff / width);
  }

  @Override public boolean onTouchEvent(MotionEvent event) {
    boolean update = false;

    switch (event.getAction()) {

      case MotionEvent.ACTION_DOWN:
        mStartTouchPoint = new Point((int) event.getX(), (int) event.getY());
        update = moveTrackersIfNeeded(event);
        break;
      case MotionEvent.ACTION_MOVE:
        update = moveTrackersIfNeeded(event);
        break;
      case MotionEvent.ACTION_UP:
        performClick();
        mStartTouchPoint = null;
        update = moveTrackersIfNeeded(event);
        break;
    }

    if (update) {
      if (mListener != null) {
        mListener.onColorChanged(Color.HSVToColor(mAlpha, new float[] { mHue, mSat, mVal }));
      }
      invalidate();
      return true;
    }

    return super.onTouchEvent(event);
  }

  @Override public boolean performClick() {
    super.performClick();
    return true;
  }

  private boolean moveTrackersIfNeeded(MotionEvent event) {
    if (mStartTouchPoint == null) {
      return false;
    }

    boolean update = false;

    int startX = mStartTouchPoint.x;
    int startY = mStartTouchPoint.y;

    if (mHueRect.contains(startX, startY)) {
      mHue = pointToHue(event.getY());

      update = true;
    } else if (mSatValRect.contains(startX, startY)) {
      float[] result = pointToSatVal(event.getX(), event.getY());

      mSat = result[0];
      mVal = result[1];

      update = true;
    } else if (mAlphaRect != null && mAlphaRect.contains(startX, startY)) {
      mAlpha = pointToAlpha((int) event.getX());

      update = true;
    }

    return update;
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int finalWidth;
    int finalHeight;

    int widthMode = MeasureSpec.getMode(widthMeasureSpec);
    int heightMode = MeasureSpec.getMode(heightMeasureSpec);

    int widthAllowed = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
    int heightAllowed =
        MeasureSpec.getSize(heightMeasureSpec) - getPaddingBottom() - getPaddingTop();

    //Log.d("color-picker-view", "widthMode: " + modeToString(widthMode) + " heightMode: " + modeToString(heightMode) + " widthAllowed: " + widthAllowed + " heightAllowed: " + heightAllowed);

    if (widthMode == MeasureSpec.EXACTLY || heightMode == MeasureSpec.EXACTLY) {
      //A exact value has been set in either direction, we need to stay within this size.

      if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY) {
        //The with has been specified exactly, we need to adopt the height to fit.
        int h = widthAllowed - mPanelSpacingPx - mHuePanelWidthPx;

        if (mShowAlphaPanel) {
          h += mPanelSpacingPx + mAlphaPanelHeightPx;
        }

        if (h > heightAllowed) {
          //We can't fit the view in this container, set the size to whatever was allowed.
          finalHeight = heightAllowed;
        } else {
          finalHeight = h;
        }

        finalWidth = widthAllowed;
      } else if (widthMode != MeasureSpec.EXACTLY) {
        //The height has been specified exactly, we need to stay within this height and adopt the width.

        int w = heightAllowed + mPanelSpacingPx + mHuePanelWidthPx;

        if (mShowAlphaPanel) {
          w -= (mPanelSpacingPx + mAlphaPanelHeightPx);
        }

        if (w > widthAllowed) {
          //we can't fit within this container, set the size to whatever was allowed.
          finalWidth = widthAllowed;
        } else {
          finalWidth = w;
        }

        finalHeight = heightAllowed;
      } else {
        //If we get here the dev has set the width and height to exact sizes. For example match_parent or 300dp.
        //This will mean that the sat/val panel will not be square but it doesn't matter. It will work anyway.
        //In all other scenarios our goal is to make that panel square.

        //We set the sizes to exactly what we were told.
        finalWidth = widthAllowed;
        finalHeight = heightAllowed;
      }
    } else {
      //If no exact size has been set we try to make our view as big as possible
      //within the allowed space.

      //Calculate the needed width to layout using max allowed height.
      int widthNeeded = heightAllowed + mPanelSpacingPx + mHuePanelWidthPx;

      //Calculate the needed height to layout using max allowed width.
      int heightNeeded = widthAllowed - mPanelSpacingPx - mHuePanelWidthPx;

      if (mShowAlphaPanel) {
        widthNeeded -= (mPanelSpacingPx + mAlphaPanelHeightPx);
        heightNeeded += mPanelSpacingPx + mAlphaPanelHeightPx;
      }

      boolean widthOk = false;
      boolean heightOk = false;

      if (widthNeeded <= widthAllowed) {
        widthOk = true;
      }

      if (heightNeeded <= heightAllowed) {
        heightOk = true;
      }

      if (widthOk && heightOk) {
        finalWidth = widthAllowed;
        finalHeight = heightNeeded;
      } else if (!heightOk && widthOk) {
        finalHeight = heightAllowed;
        finalWidth = widthNeeded;
      } else if (heightOk) {
        finalHeight = heightNeeded;
        finalWidth = widthAllowed;
      } else {
        finalHeight = heightAllowed;
        finalWidth = widthAllowed;
      }
    }

    setMeasuredDimension(finalWidth + getPaddingLeft() + getPaddingRight(),
        finalHeight + getPaddingTop() + getPaddingBottom());
  }

  private int getPreferredWidth() {
    //Our preferred width and height is 200dp for the square sat / val rectangle.
    int width = Util.dpToPx(getContext(), 200);
    return width + mHuePanelWidthPx + mPanelSpacingPx;
  }

  private int getPreferredHeight() {
    int height = Util.dpToPx(getContext(), 200);

    if (mShowAlphaPanel) {
      height += mPanelSpacingPx + mAlphaPanelHeightPx;
    }
    return height;
  }

  @Override protected void onSizeChanged(int w, int h, int oldW, int oldH) {
    super.onSizeChanged(w, h, oldW, oldH);

    mDrawingRect = new Rect();
    mDrawingRect.left = getPaddingLeft();
    mDrawingRect.right = w - getPaddingRight();
    mDrawingRect.top = getPaddingTop();
    mDrawingRect.bottom = h - getPaddingBottom();

    //The need to be recreated because they depend on the size of the view.
    mValShader = null;
    mSatShader = null;
    mAlphaShader = null;

    // Clear those bitmap caches since the size may have changed.
    mSatValBackgroundCache = null;
    mHueBackgroundCache = null;

    setUpSatValRect();
    setUpHueRect();
    setUpAlphaRect();
  }

  private void setUpSatValRect() {
    //Calculate the size for the big color rectangle.
    final Rect dRect = mDrawingRect;

    int left = dRect.left + BORDER_WIDTH_PX;
    int top = dRect.top + BORDER_WIDTH_PX;
    int bottom = dRect.bottom - BORDER_WIDTH_PX;
    int right = dRect.right - BORDER_WIDTH_PX - mPanelSpacingPx - mHuePanelWidthPx;

    if (mShowAlphaPanel) {
      bottom -= (mAlphaPanelHeightPx + mPanelSpacingPx);
    }

    mSatValRect = new Rect(left, top, right, bottom);
  }

  private void setUpHueRect() {
    //Calculate the size for the hue slider on the left.
    final Rect dRect = mDrawingRect;

    int left = dRect.right - mHuePanelWidthPx + BORDER_WIDTH_PX;
    int top = dRect.top + BORDER_WIDTH_PX;
    int bottom =
        dRect.bottom - BORDER_WIDTH_PX - (mShowAlphaPanel ? (mPanelSpacingPx + mAlphaPanelHeightPx)
            : 0);
    int right = dRect.right - BORDER_WIDTH_PX;

    mHueRect = new Rect(left, top, right, bottom);
  }

  private void setUpAlphaRect() {

    if (!mShowAlphaPanel) return;

    final Rect dRect = mDrawingRect;

    int left = dRect.left + BORDER_WIDTH_PX;
    int top = dRect.bottom - mAlphaPanelHeightPx + BORDER_WIDTH_PX;
    int bottom = dRect.bottom - BORDER_WIDTH_PX;
    int right = dRect.right - BORDER_WIDTH_PX;

    mAlphaRect = new Rect(left, top, right, bottom);

    mAlphaPattern = new AlphaPatternDrawable(Util.dpToPx(getContext(), 5));
    mAlphaPattern.setBounds(Math.round(mAlphaRect.left), Math.round(mAlphaRect.top),
        Math.round(mAlphaRect.right), Math.round(mAlphaRect.bottom));
  }

  /**
   * Set a OnColorChangedListener to get notified when the color
   * selected by the user has changed.
   */
  public void setOnColorChangedListener(OnColorChangedListener listener) {
    mListener = listener;
  }

  /**
   * Get the current color this view is showing.
   *
   * @return the current color.
   */
  public int getColor() {
    return Color.HSVToColor(mAlpha, new float[] { mHue, mSat, mVal });
  }

  /**
   * Set the color the view should show.
   *
   * @param color The color that should be selected. #argb
   */
  public void setColor(int color) {
    setColor(color, false);
  }

  /**
   * Set the color this view should show.
   *
   * @param color The color that should be selected. #argb
   * @param callback If you want to get a callback to
   * your OnColorChangedListener.
   */
  @SuppressWarnings("SameParameterValue") public void setColor(int color, boolean callback) {

    int alpha = Color.alpha(color);
    int red = Color.red(color);
    int blue = Color.blue(color);
    int green = Color.green(color);

    float[] hsv = new float[3];

    Color.RGBToHSV(red, green, blue, hsv);

    mAlpha = alpha;
    mHue = hsv[0];
    mSat = hsv[1];
    mVal = hsv[2];

    if (callback && mListener != null) {
      mListener.onColorChanged(Color.HSVToColor(mAlpha, new float[] { mHue, mSat, mVal }));
    }

    invalidate();
  }

  /**
   * Set if the user is allowed to adjust the alpha panel. Default is false.
   * If it is set to false no alpha will be set.
   */
  public void setAlphaSliderVisible(boolean visible) {
    if (mShowAlphaPanel != visible) {
      mShowAlphaPanel = visible;

			/*
       * Force recreation.
			 */
      mValShader = null;
      mSatShader = null;
      mAlphaShader = null;
      mHueBackgroundCache = null;
      mSatValBackgroundCache = null;

      requestLayout();
    }
  }

  /**
   * Set the color of the tracker slider on the hue and alpha panel.
   */
  public void setSliderTrackerColor(int color) {
    mSliderTrackerColor = color;
    mHueAlphaTrackerPaint.setColor(mSliderTrackerColor);
    invalidate();
  }

  /**
   * Get color of the tracker slider on the hue and alpha panel.
   */
  public int getSliderTrackerColor() {
    return mSliderTrackerColor;
  }

  /**
   * Set the color of the border surrounding all panels.
   */
  public void setBorderColor(int color) {
    mBorderColor = color;
    invalidate();
  }

  /**
   * Get the color of the border surrounding all panels.
   */
  public int getBorderColor() {
    return mBorderColor;
  }

  /**
   * Set the text that should be shown in the
   * alpha slider. Set to null to disable text.
   *
   * @param res string resource id.
   */
  public void setAlphaSliderText(int res) {
    String text = getContext().getString(res);
    setAlphaSliderText(text);
  }

  /**
   * Set the text that should be shown in the
   * alpha slider. Set to null to disable text.
   *
   * @param text Text that should be shown.
   */
  public void setAlphaSliderText(String text) {
    mAlphaSliderText = text;
    invalidate();
  }

  /**
   * Get the current value of the text
   * that will be shown in the alpha
   * slider.
   */
  public String getAlphaSliderText() {
    return mAlphaSliderText;
  }

  private class BitmapCache {
    Canvas canvas;
    Bitmap bitmap;
    float value;
  }

  public interface OnColorChangedListener {
    void onColorChanged(int newColor);
  }
}
