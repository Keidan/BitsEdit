package fr.ralala.bitsedit.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;

import fr.ralala.bitsedit.R;
import fr.ralala.bitsedit.utils.Bits;
import fr.ralala.bitsedit.utils.Radix;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Main activity
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
  private String mDotString = null;
  private String mOneString = null;
  private final Bits mGlobalValue = new Bits();
  private final SparseArray<AppCompatTextView> mGrid = new SparseArray<>();
  private TextInputEditText mEtDec = null;
  private TextInputEditText mEtHex = null;
  private @ColorInt int mColorDot;
  private @ColorInt int mColorOne;

  /**
   * Called when the activity is created.
   *
   * @param savedInstanceState Bundle
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mDotString = getString(R.string.dot);
    mOneString = getString(R.string.one);
    mColorOne = ContextCompat.getColor(this, R.color.colorAccent);
    mColorDot = ContextCompat.getColor(this, R.color.textColor);

    ActionBar actionBar = getDelegate().getSupportActionBar();
    // add the custom view to the action bar
    if (null != actionBar) {
      actionBar.setCustomView(R.layout.actionbar_view);
      mEtDec = actionBar.getCustomView().findViewById(R.id.etDec);
      mEtHex = actionBar.getCustomView().findViewById(R.id.etHex);
      mEtDec.setOnEditorActionListener(this);
      mEtHex.setOnEditorActionListener(this);
      actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
    }

    installShift(findViewById(R.id.btShiftLeft), true);
    installShift(findViewById(R.id.btShiftRight), false);
    findViewById(R.id.btMore).setOnClickListener(v -> startActivity(new Intent(this, MoreActivity.class)));
    createGrid();
  }

  /**
   * Shifts the bits.
   *
   * @param v    The view.
   * @param left Left shift?
   */
  private void installShift(View v, final boolean left) {
    v.setOnTouchListener(new View.OnTouchListener() {

      private Handler mHandler;
      private final Runnable mAction = new Runnable() {
        @Override
        public void run() {
          if (left)
            mGlobalValue.shiftLeft();
          else
            mGlobalValue.shiftRight();
          refreshBits();
          setTexts();
          mHandler.postDelayed(this, 250);
        }
      };

      @SuppressLint("ClickableViewAccessibility")
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
          case MotionEvent.ACTION_DOWN:
            if (null != mHandler) return true;
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(mAction);
            break;
          case MotionEvent.ACTION_UP:
            if (null == mHandler) return true;
            mHandler.removeCallbacks(mAction);
            mHandler = null;
            break;
          default:
            break;
        }
        return false;
      }

    });
  }

  /**
   * Creates the grid.
   */
  private void createGrid() {
    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
    LinearLayout llBin = findViewById(R.id.llBin);
    Locale locale = Locale.getDefault();
    int lbl = Bits.MAX_BIT;
    int id = Bits.MAX_BIT;
    for (int i = 0; i < 16; ++i) {
      LinearLayout llBinContent = new LinearLayout(this);
      llBinContent.setOrientation(LinearLayout.HORIZONTAL);
      LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
          ViewGroup.LayoutParams.MATCH_PARENT,
          ViewGroup.LayoutParams.WRAP_CONTENT);
      llBinContent.setLayoutParams(llp);
      if (0 == (i % 2)) {
        llBinContent.setBackgroundColor(getColor(R.color.colorBackground));
        for (int j = 7; j >= 0; --j, --lbl) {
          AppCompatTextView tv = createTextView(true);
          tv.setId(-(id + 1));
          tv.setText(String.format(locale, "%02d", lbl));
          tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
          llBinContent.addView(tv);
        }
      } else {
        for (int j = 7; j >= 0; --j, --id) {
          AppCompatTextView tv = createTextView(false);
          setText(tv, mDotString);
          tv.setId(id);
          tv.setMinHeight(px);
          tv.setOnClickListener(this);
          llBinContent.addView(tv);
          mGrid.put(id, tv);
        }
      }
      if (null != llBin)
        llBin.addView(llBinContent);
    }
  }

  /**
   * Called when the activity is resumed.
   */
  @Override
  public void onResume() {
    super.onResume();
    Bits bits = new Bits();
    bits.setValue(mGlobalValue.getDecValue(), Radix.DEC);
    refreshBits();
  }

  /**
   * Refreshes the bits textview's
   */
  private void refreshBits() {
    for (int i = 0; i < mGrid.size(); ++i) {
      AppCompatTextView tv = mGrid.get(i);
      setText(tv, mGlobalValue.isNotBit(i) ? mDotString : mOneString);
    }
  }

  /**
   * Creates the text view.
   *
   * @param label Is label?
   * @return The text view.
   */
  private AppCompatTextView createTextView(boolean label) {
    AppCompatTextView tv = new AppCompatTextView(this);
    tv.setGravity(Gravity.CENTER);
    LinearLayout.LayoutParams tvLinearLayoutParams = new LinearLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    tvLinearLayoutParams.width = 0;
    tvLinearLayoutParams.weight = 1;
    tvLinearLayoutParams.gravity = Gravity.FILL;
    tvLinearLayoutParams.topMargin = label ? 5 : 10;
    tvLinearLayoutParams.bottomMargin = 0;
    tv.setLayoutParams(tvLinearLayoutParams);
    return tv;
  }

  /**
   * Called when a click is generated.
   *
   * @param v The associated view.
   */
  public void onClick(View v) {
    AppCompatTextView tv = (AppCompatTextView) v;
    boolean value = tv.getText().toString().equals(mOneString);
    setText(tv, !value ? mOneString : mDotString);
    mGlobalValue.setBit(v.getId(), !value);
    setTexts();
  }

  /**
   * This function changes the text of an AppCompatTextView and also changes the color depending on the input
   * @param tv The view.
   * @param text The text
   */
  private void setText(AppCompatTextView tv, String text) {
    if(text.equals(mOneString))
      tv.setTextColor(mColorOne);
    else
      tv.setTextColor(mColorDot);
    tv.setText(text);
  }

  /**
   * Sets texts.
   */
  private void setTexts() {
    mEtDec.setText(mGlobalValue.getDecValue());
    mEtHex.setText(mGlobalValue.getHexValue());
  }

  /**
   * Called when an action is being performed.
   *
   * @param v        The view that was clicked.
   * @param actionId Identifier of the action.  This will be either the
   *                 identifier you supplied, or {@link EditorInfo#IME_NULL
   *                 EditorInfo.IME_NULL} if being called due to the enter key
   *                 being pressed.
   * @param event    If triggered by an enter key, this is the event;
   *                 otherwise, this is null.
   * @return Return true if you have consumed the action, else false.
   */
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (EditorInfo.IME_ACTION_DONE == actionId) {
      TextInputEditText et = (TextInputEditText) v;
      Radix base = et.equals(mEtDec) ? Radix.DEC : Radix.HEX;
      mGlobalValue.setValue(Objects.requireNonNull(et.getText()).toString(), base);
      for (int i = 0; i < mGrid.size(); ++i) {
        AppCompatTextView tv = mGrid.get(i);
        setText(tv, mGlobalValue.isNotBit(i) ? mDotString : mOneString);
      }
      TextInputEditText other = et.equals(mEtDec) ? mEtHex : mEtDec;
      other.setText(mGlobalValue.getValueFromBase(base == Radix.DEC ? Radix.HEX : Radix.DEC));
      InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      if (null != in)
        in.hideSoftInputFromWindow(v.getApplicationWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);
      return true;
    }
    return false;
  }
}
