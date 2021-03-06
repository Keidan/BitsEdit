package fr.ralala.bitsedit;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Locale;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

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
  private final SparseArray<TextView> mGrid = new SparseArray<>();
  private EditText mEtDec = null;
  private EditText mEtHex = null;

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

    ActionBar actionBar = getDelegate().getSupportActionBar();
    // add the custom view to the action bar
    if (actionBar != null) {
      actionBar.setCustomView(R.layout.actionbar_view);
      mEtDec = actionBar.getCustomView().findViewById(R.id.etDec);
      mEtHex = actionBar.getCustomView().findViewById(R.id.etHex);
      mEtDec.setOnEditorActionListener(this);
      mEtHex.setOnEditorActionListener(this);
      actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
    }

    createGrid();
  }

  /**
   * Creates the grid.
   */
  private void createGrid() {
    int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
    LinearLayout llBin = findViewById(R.id.llBin);
    Locale locale = Locale.getDefault();
    for (int i = 0, lbl = 63, id = 63; i < 16; ++i) {
      LinearLayout llBinContent = new LinearLayout(this);
      llBinContent.setOrientation(LinearLayout.HORIZONTAL);
      LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
          LinearLayout.LayoutParams.MATCH_PARENT,
          LinearLayout.LayoutParams.WRAP_CONTENT);
      llBinContent.setLayoutParams(llp);
      if (i % 2 == 0) {
        llBinContent.setBackgroundColor(getColor(R.color.colorBackground));
        for (int j = 7; j >= 0; --j, --lbl) {
          TextView tv = createTextView(true);
          tv.setId(-(id + 1));
          tv.setText(String.format(locale, "%02d", lbl));
          tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
          llBinContent.addView(tv);
        }
      } else {
        for (int j = 7; j >= 0; --j, --id) {
          TextView tv = createTextView(false);
          tv.setText(mDotString);
          tv.setId(id);
          tv.setMinHeight(px);
          tv.setOnClickListener(this);
          llBinContent.addView(tv);
          mGrid.put(id, tv);

        }
      }
      if (llBin != null)
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
    bits.setValue(mGlobalValue.getDecValue(), 10);
    for (int i = 0; i < mGrid.size(); ++i) {
      TextView tv = mGrid.get(i);
      tv.setText(mGlobalValue.isNotBit(i) ? mDotString : mOneString);
    }
  }

  /**
   * Creates the text view.
   *
   * @param label Is label?
   * @return The text view.
   */
  private TextView createTextView(boolean label) {
    TextView tv = new TextView(this);
    tv.setGravity(Gravity.CENTER);
    LinearLayout.LayoutParams tvLinearLayoutParams = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);
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
    TextView tv = (TextView) v;
    boolean value = tv.getText().toString().equals(mOneString);
    tv.setText(!value ? mOneString : mDotString);
    mGlobalValue.setBit(v.getId(), !value);
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
    if (actionId == EditorInfo.IME_ACTION_DONE) {
      EditText et = (EditText) v;
      int base = et.equals(mEtDec) ? 10 : 16;
      mGlobalValue.setValue(et.getText().toString(), base);
      for (int i = 0; i < mGrid.size(); ++i) {
        TextView tv = mGrid.get(i);
        tv.setText(mGlobalValue.isNotBit(i) ? mDotString : mOneString);
      }
      EditText other = et.equals(mEtDec) ? mEtHex : mEtDec;
      other.setText(mGlobalValue.getValueFromBase(base == 10 ? 16 : 10));
      InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
      if (in != null)
        in.hideSoftInputFromWindow(v.getApplicationWindowToken(),
            InputMethodManager.HIDE_NOT_ALWAYS);
      return true;
    }
    return false;
  }
}
