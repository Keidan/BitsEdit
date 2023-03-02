package fr.ralala.bitsedit;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * More activity
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public class MoreActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher {
  private static final int IDX_DECIMAL = 0;
  private static final int IDX_HEX = 1;
  private static final int IDX_AND = 0;
  private static final int IDX_AND_NOT = 1;
  private static final int IDX_OR = 2;
  private static final int IDX_XOR = 3;
  private final Bits mGlobalValue1 = new Bits();
  private final Bits mGlobalValue2 = new Bits();
  private AppCompatSpinner mSpInputType1;
  private AppCompatSpinner mSpInputType2;
  private AppCompatSpinner mSpOperation;
  private TextInputEditText mTextValue1;
  private TextInputEditText mTextValue2;
  private TextView mTvResult;
  private boolean mIgnore = false;
  private InputFilter[] mDefaultInputFiler1 = new InputFilter[0];
  private InputFilter[] mDefaultInputFiler2 = new InputFilter[0];
  /* https://stackoverflow.com/questions/10648449/how-do-i-set-a-edittext-to-the-input-of-only-hexadecimal-numbers/17355026 */
  private final InputFilter mInputFilterTextHex = (source, start, end, dest, dStart, dEnd) -> {
    Pattern pattern = Pattern.compile("^\\p{XDigit}+$");
    StringBuilder sb = new StringBuilder();
    for (int i = start; i < end; i++) {
      if (!Character.isLetterOrDigit(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
        return "";
      }
      Matcher matcher = pattern.matcher(String.valueOf(source.charAt(i)));
      if (!matcher.matches()) {
        return "";
      }
      sb.append(source.charAt(i));
    }
    return sb.toString();
  };

  /**
   * Called when the activity is created.
   *
   * @param savedInstanceState Bundle
   */
  @Override
  protected void onCreate(final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_more);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    mTextValue1 = findViewById(R.id.tietValue1);
    mTextValue2 = findViewById(R.id.tietValue2);
    mSpInputType1 = findViewById(R.id.spInputType1);
    mSpInputType2 = findViewById(R.id.spInputType2);
    mSpOperation = findViewById(R.id.spOperation);
    mTvResult = findViewById(R.id.tvResult);

    buildSpinnerInputType(mSpInputType1);
    buildSpinnerInputType(mSpInputType2);

    List<String> base = new ArrayList<>();
    base.add(getString(R.string.and));
    base.add(getString(R.string.and_not));
    base.add(getString(R.string.or));
    base.add(getString(R.string.xor));
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
        android.R.layout.simple_spinner_item,
        base);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    mSpOperation.setAdapter(adapter);
    mSpOperation.setOnItemSelectedListener(this);

    mDefaultInputFiler1 = mTextValue1.getFilters();
    mDefaultInputFiler2 = mTextValue2.getFilters();

    mTextValue1.setText("");
    mTextValue2.setText("");
    mTextValue1.addTextChangedListener(this);
    mTextValue2.addTextChangedListener(this);
    mSpInputType1.setSelection(IDX_AND);
    mSpInputType1.setSelection(IDX_DECIMAL);
    mSpInputType2.setSelection(IDX_DECIMAL);
  }

  private void buildSpinnerInputType(AppCompatSpinner spinner) {
    List<String> base = new ArrayList<>();
    base.add(getString(R.string.base10));
    base.add(getString(R.string.base16));
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
        android.R.layout.simple_spinner_item,
        base);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(this);
  }

  /**
   * Called when the options item is clicked (home).
   *
   * @param item The selected menu.
   * @return boolean
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      setResult(RESULT_CANCELED);
      hideKeyboard();
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  private void hideKeyboard() {
    /* hide keyboard */
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm.isAcceptingText()) {
      //Find the currently focused view, so we can grab the correct window token from it.
      View view = getCurrentFocus();
      //If no view currently has focus, create a new one, just so we can grab a window token from it
      if (view == null) {
        view = new View(this);
      }
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  /**
   * Callback method to be invoked when the selection disappears from this
   * view.
   *
   * @param parent The AdapterView that now contains no selected item.
   */
  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    /* nothing */
  }

  /**
   * <p>Callback method to be invoked when an item in this view has been
   * selected.</p>
   *
   * @param parent   The AdapterView where the selection happened
   * @param view     The view within the AdapterView that was clicked
   * @param position The position of the view in the adapter
   * @param id       The row id of the item that is selected
   */
  @Override
  public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    if (parent.equals(mSpInputType1)) {
      updateText(mSpInputType1, mTextValue1, mGlobalValue1);
      changeInputType(mSpInputType1, mTextValue1, mDefaultInputFiler1);
    } else if (parent.equals(mSpInputType2)) {
      updateText(mSpInputType2, mTextValue2, mGlobalValue2);
      changeInputType(mSpInputType2, mTextValue2, mDefaultInputFiler2);
    } else if (parent.equals(mSpOperation)) {
      evaluateOperation();
    }
  }

  /**
   * Sets the text according to the converted value.
   *
   * @param output TextInputEditText
   */
  private void updateText(AppCompatSpinner spinner, TextInputEditText output, Bits bits) {
    String text = Objects.requireNonNull(output.getText()).toString();
    if(text.isEmpty())
      return;
    if (spinner.getSelectedItemId() == IDX_HEX) {
      bits.setValue(text, Radix.DEC);
      output.setText(bits.getHexValue());
    } else {
      bits.setValue(text, Radix.HEX);
      output.setText(bits.getDecValue());
    }
  }

  private void changeInputType(AppCompatSpinner spinner, TextInputEditText textValue, InputFilter[] defaultInputFiler) {
    if (spinner.getSelectedItemId() == IDX_DECIMAL) {
      textValue.setFilters(defaultInputFiler);
      textValue.setInputType(InputType.TYPE_CLASS_NUMBER);
      textValue.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
    } else {
      textValue.setFilters(new InputFilter[]{mInputFilterTextHex});
      textValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    }
    textValue.setTypeface(Typeface.MONOSPACE);
    textValue.setSelection(Objects.requireNonNull(textValue.getText()).length());
  }

  /**
   * This method is called to notify you that, within s, the count characters beginning at start are about to be replaced
   * by new text with length after. It is an error to attempt to make changes to s from this callback.
   *
   * @param s     CharSequence
   * @param start int
   * @param count int
   * @param after int
   */
  @Override
  public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    /* nothing */
  }

  /**
   * This method is called to notify you that, within s, the count characters beginning at start have just replaced old text
   * that had length before. It is an error to attempt to make changes to s from this callback.
   *
   * @param s      CharSequence
   * @param start  int
   * @param before int
   * @param count  int
   */
  @Override
  public void onTextChanged(CharSequence s, int start, int before, int count) {
    /* nothing */
  }

  /**
   * This method is called to notify you that, somewhere within s, the text has been changed.
   * It is legitimate to make further changes to s from this callback, but be careful not to get
   * yourself into an infinite loop, because any changes you make will cause this method to be
   * called again recursively. (You are not told where the change took place because other
   * afterTextChanged() methods may already have made other changes and invalidated the offsets.
   * But if you need to know here, you can use Spannable#setSpan in
   * onTextChanged(CharSequence, int, int, int) to mark your place and then look
   * up from here where the span ended up.
   *
   * @param s Editable
   */
  @Override
  public void afterTextChanged(Editable s) {
    if (mIgnore)
      return;
    mIgnore = true; // prevent infinite loop
    evaluateOperation();
    mIgnore = false; // release, so the TextWatcher start to listen again.
  }

  private void loadGlobalValue(TextInputEditText editText, AppCompatSpinner spinner, Bits bits) {
    String text = Objects.requireNonNull(editText.getText()).toString();
    if(text.isEmpty())
      bits.setValue("0", Radix.DEC);
    else
      bits.setValue(text, spinner.getSelectedItemId() == IDX_HEX ? Radix.HEX : Radix.DEC);
  }

  private void evaluateOperation() {
    loadGlobalValue(mTextValue1, mSpInputType1, mGlobalValue1);
    loadGlobalValue(mTextValue2, mSpInputType2, mGlobalValue2);

    Bits bits;
    String operation;
    switch ((int) mSpOperation.getSelectedItemId()) {
      case IDX_AND:
        operation = getString(R.string.and);
        bits = mGlobalValue1.and(mGlobalValue2);
        break;
      case IDX_AND_NOT:
        operation = getString(R.string.and_not);
        bits = mGlobalValue1.andNot(mGlobalValue2);
        break;
      case IDX_OR:
        operation = getString(R.string.or);
        bits = mGlobalValue1.or(mGlobalValue2);
        break;
      case IDX_XOR:
        operation = getString(R.string.xor);
        bits = mGlobalValue1.xor(mGlobalValue2);
        break;
      default:
        mTvResult.setText(R.string.error);
        return;
    }
    int max = Math.max(mGlobalValue1.getBitLength(), Math.max(mGlobalValue2.getBitLength(), bits.getBitLength()));
    final String padding = "  ";
    StringBuilder sb = new StringBuilder();
    sb.append(padding).append(padding).append(padding);
    sb.append(mGlobalValue1.toBinary(max));
    sb.append("\n").append(padding);
    sb.append(operation);
    sb.append("\n").append(padding).append(padding).append(padding);
    sb.append(mGlobalValue2.toBinary(max));
    sb.append("\n\n").append(padding).append(padding).append(padding);
    sb.append(bits.toBinary(max));
    sb.append("\n\n");
    sb.append(getString(R.string.base10));
    sb.append(": ");
    sb.append(bits.getDecValue());
    sb.append("\n");
    sb.append(getString(R.string.base16));
    sb.append(": ");
    sb.append(bits.getHexValue());
    mTvResult.setText(sb);
  }
}
