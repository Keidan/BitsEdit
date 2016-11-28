package fr.ralala.bitsedit;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

/**
 *******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Main activity
 * </p>
 * @author Keidan
 *
 *******************************************************************************
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  private String dotString = null;
  private String oneString = null;
  private Bits globalValue = new Bits();
  private SparseArray<TextView> bin = new SparseArray<>();
  private EditText etDec = null;
  private EditText etHex = null;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    dotString = getString(R.string.dot);
    oneString = getString(R.string.one);

    int px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());

    android.support.v7.app.ActionBar actionBar = getDelegate().getSupportActionBar();
    // add the custom view to the action bar
    if(actionBar != null) {
      actionBar.setCustomView(R.layout.actionbar_view);
      etDec = (EditText) actionBar.getCustomView().findViewById(R.id.etDec);
      etHex = (EditText) actionBar.getCustomView().findViewById(R.id.etHex);

      TextView.OnEditorActionListener li = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if (actionId == EditorInfo.IME_ACTION_DONE) {
            EditText et = (EditText)v;
            int base = et.equals(etDec) ? 10 : 16;
            globalValue.setValue(et.getText().toString(), base);
            for(int i = 0; i < bin.size(); ++i) {
              TextView tv = bin.get(i);
              tv.setText(!globalValue.isBit(i) ? dotString : oneString);
            }
            EditText other = et.equals(etDec) ? etHex : etDec;
            other.setText(globalValue.getValueFromBase(base == 10 ? 16 : 10));
            InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(v
                .getApplicationWindowToken(),
              InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
          }
          return false;
        }
      };
      etDec.setOnEditorActionListener(li);
      etHex.setOnEditorActionListener(li);


      actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
    }

    LinearLayout llBin = (LinearLayout)findViewById(R.id.llBin);
    Locale locale = getResources().getConfiguration().locale;
    for(int i = 0, lbl = 63, id = 63; i < 16; ++i) {
      LinearLayout llBinContent = new LinearLayout(this);
      llBinContent.setOrientation(LinearLayout.HORIZONTAL);
      LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);
      llBinContent.setLayoutParams(llp);
      if(i%2 == 0) {
        llBinContent.setBackgroundColor(getColor(android.R.color.darker_gray));
        for (int j = 7; j >= 0; --j, --lbl) {
          TextView tv = createTextView(true);
          tv.setText(String.format(locale, "%02d", lbl));
          tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
          llBinContent.addView(tv);
        }
      } else {
        for (int j = 7; j >= 0; --j, --id) {
          TextView tv = createTextView(false);
          tv.setText(dotString);
          tv.setId(id);
          tv.setMinHeight(px);
          tv.setOnClickListener(this);
          llBinContent.addView(tv);
          bin.put(id, tv);
        }
      }
      if(llBin != null) llBin.addView(llBinContent);
    }
  }

  private TextView createTextView(boolean label) {
    TextView tv = new TextView(this);
    tv.setGravity(Gravity.CENTER);
    LinearLayout.LayoutParams tvllp = new LinearLayout.LayoutParams(
      LinearLayout.LayoutParams.WRAP_CONTENT,
      LinearLayout.LayoutParams.WRAP_CONTENT);
    tvllp.width = 0;
    tvllp.weight = 1;
    tvllp.gravity = Gravity.FILL;
    if(label) {
      tvllp.topMargin = 5;
      tvllp.bottomMargin = 0;
    } else {
      tvllp.topMargin = 10;
      tvllp.bottomMargin = 0;
    }
    tv.setLayoutParams(tvllp);
    return tv;
  }

  public void onClick(View v) {
    TextView tv = (TextView)v;
    boolean value = tv.getText().toString().equals(oneString);
    tv.setText(!value ? oneString : dotString);
    globalValue.setBit(v.getId(), !value);
    etDec.setText(globalValue.getDecValue());
    etHex.setText(globalValue.getHexValue());
  }

}
