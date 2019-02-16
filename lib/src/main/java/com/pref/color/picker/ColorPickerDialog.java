package com.pref.color.picker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.preference.DialogPreference;

/**
 * Called from {@link ColorPreferenceFragmentCompat}.
 *
 * @author u1aryz
 * @author 7Koston
 */
public class ColorPickerDialog extends DialogFragment
    implements DialogInterface.OnClickListener,
        ColorPickerView.OnColorChangedListener,
        TextWatcher {

  protected static final String ARG_KEY = "key";
  private static final String SAVE_STATE_COLOR = "ColorPickerDialog.color";
  private static final String SAVE_STATE_SHOW_ALPHA_SLIDER = "ColorPickerDialog.showAlphaSlider";

  private ColorPreference mPreference;

  private Context mContext;
  private Bundle mArgs;
  private DialogPreference.TargetFragment mFragment;

  private int mColor;
  private boolean mShowAlphaSlider;

  private ColorPickerView mColorPicker;
  private ColorPanelView mNewColorPanel;
  private EditText mHexEdit;

  public static ColorPickerDialog newInstance(String key) {
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
    mArgs = getArguments();
    mFragment = (DialogPreference.TargetFragment) getTargetFragment();

    if (savedInstanceState == null) {
      final String key = mArgs.getString(ARG_KEY);
      mPreference = mFragment.findPreference(key);
      mColor = mPreference.getColor();
      mShowAlphaSlider = mPreference.isShowAlphaSlider();
    } else {
      mColor = savedInstanceState.getInt(SAVE_STATE_COLOR, Color.BLACK);
      mShowAlphaSlider = savedInstanceState.getBoolean(SAVE_STATE_SHOW_ALPHA_SLIDER, false);
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = onCreateDialogView(mContext);

    mColorPicker = view.findViewById(R.id.color_picker);
    ColorPanelView mOldColorPanel = view.findViewById(R.id.old_color_panel);
    mNewColorPanel = view.findViewById(R.id.new_color_panel);
    mHexEdit = view.findViewById(R.id.edit_hex);

    mColorPicker.setOnColorChangedListener(this);
    mHexEdit.addTextChangedListener(this);

    mOldColorPanel.setColor(mColor);
    mColorPicker.setAlphaSliderVisible(mShowAlphaSlider);
    mColorPicker.setColor(mColor, true);

    mHexEdit.setFilters(new InputFilter[] {new InputFilter.LengthFilter(mShowAlphaSlider ? 8 : 6)});

    return new AlertDialog.Builder(mContext)
        .setView(view)
        .setPositiveButton(R.string.ok, this)
        .setNegativeButton(R.string.cancel, null)
        .create();
  }

  protected View onCreateDialogView(Context context) {
    return View.inflate(context, R.layout.dialog_color_picker, null);
  }

  @Override
  public void onClick(DialogInterface dialog, int which) {
    if (which == DialogInterface.BUTTON_POSITIVE) {
      int value = mColorPicker.getColor();
      getPreference().callChangeListener(value);
      getPreference().setColor(value);
    }
  }

  @Override
  public void onColorChanged(int newColor) {
    mNewColorPanel.setColor(newColor);

    if (mShowAlphaSlider) {
      mHexEdit.setText(String.format("%08X", newColor));
    } else {
      mHexEdit.setText(String.format("%06X", (0xFFFFFF & newColor)));
    }

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

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);

    outState.putInt(SAVE_STATE_COLOR, mColor);
    outState.putBoolean(SAVE_STATE_SHOW_ALPHA_SLIDER, mShowAlphaSlider);
  }

  public ColorPreference getPreference() {
    if (mPreference == null) {
      final String key = mArgs.getString(ARG_KEY);
      mPreference = mFragment.findPreference(key);
    }
    return mPreference;
  }
}
