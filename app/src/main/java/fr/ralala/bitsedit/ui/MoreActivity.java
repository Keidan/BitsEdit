package fr.ralala.bitsedit.ui;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.view.ViewCompat;

import java.util.Arrays;
import java.util.Objects;

import static fr.ralala.bitsedit.ui.ButtonGroup.ButtonGroupListener;

import fr.ralala.bitsedit.R;
import fr.ralala.bitsedit.utils.Bits;
import fr.ralala.bitsedit.utils.Operation;
import fr.ralala.bitsedit.utils.Radix;

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
public class MoreActivity extends AppCompatActivity implements TextWatcher, TextView.OnEditorActionListener {
  private static final String COLON = ":";
  private static final String SP = " ";
  private static final String NL = "\n";
  private static final String PADDING = "  ";
  private String mTextBase10 = "";
  private String mTextBase16 = "";
  private final Bits mGlobalValue1 = new Bits();
  private final Bits mGlobalValue2 = new Bits();
  private AppCompatEditText mTextValue1;
  private AppCompatEditText mTextValue2;
  private AppCompatTextView mTvResult;
  private boolean mIgnore = false;
  private boolean mBaseHex1 = false;
  private boolean mBaseHex2 = false;
  private Operation mOperation = Operation.AND;

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
    if (null != actionBar) {
      actionBar.setDisplayShowHomeEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    mTextBase10 = getString(R.string.base10);
    mTextBase16 = getString(R.string.base16);

    mTextValue1 = findViewById(R.id.etValue1);
    mTextValue2 = findViewById(R.id.etValue2);
    mTvResult = findViewById(R.id.tvResult);

    @ColorInt int accent = UiHelper.getSystemAccentColor(this);
    ColorStateList csl = ColorStateList.valueOf(accent);
    ViewCompat.setBackgroundTintList(mTextValue1, csl);
    ViewCompat.setBackgroundTintList(mTextValue2, csl);


    final InputFilter[] defaultInputFiler1 = mTextValue1.getFilters();
    final InputFilter[] defaultInputFiler2 = mTextValue2.getFilters();

    ButtonGroupListener liBase1 = id -> {
      clearsStates();
      mBaseHex1 = id == R.id.btBaseHex1;
      updateText(mBaseHex1, mTextValue1, mGlobalValue1);
      changeInputType(mBaseHex1, mTextValue1, defaultInputFiler1);
    };
    ButtonGroupListener liBase2 = id -> {
      clearsStates();
      mBaseHex2 = id == R.id.btBaseHex2;
      updateText(mBaseHex2, mTextValue2, mGlobalValue1);
      changeInputType(mBaseHex2, mTextValue2, defaultInputFiler2);
    };
    ButtonGroupListener liOperation = id -> {
      clearsStates();
      if (R.id.btAnd == id)
        mOperation = Operation.AND;
      else if (R.id.btAndNot == id)
        mOperation = Operation.AND_NOT;
      else if (R.id.btNot == id)
        mOperation = Operation.NOT;
      else if (R.id.btOr == id)
        mOperation = Operation.OR;
      else if (R.id.btXor == id)
        mOperation = Operation.XOR;
      else
        mOperation = null;
      evaluateOperation();
    };
    new ButtonGroup(this,
        Arrays.asList(R.id.btBaseDec1, R.id.btBaseHex1),
        liBase1);
    new ButtonGroup(this,
        Arrays.asList(R.id.btBaseDec2, R.id.btBaseHex2),
        liBase2);

    new ButtonGroup(this,
        Arrays.asList(R.id.btAnd, R.id.btAndNot, R.id.btNot, R.id.btOr, R.id.btXor),
        liOperation);


    mTextValue1.setText("");
    mTextValue2.setText("");
    mTextValue1.addTextChangedListener(this);
    mTextValue1.setOnEditorActionListener(this);
    mTextValue2.addTextChangedListener(this);
    mTextValue2.setOnEditorActionListener(this);
    evaluateOperation(); /* force refresh */
  }

  /**
   * Clears the texts focus and hide the keyboad.
   */
  private void clearsStates() {
    UiHelper.hideKeyboard(this);
    mTextValue1.clearFocus();
    mTextValue2.clearFocus();
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
  @Override
  public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
    if (EditorInfo.IME_ACTION_DONE == actionId) {
      clearsStates();
      return true;
    }
    return false;
  }

  /**
   * Called when the options item is clicked (home).
   *
   * @param item The selected menu.
   * @return boolean
   */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (android.R.id.home == item.getItemId()) {
      setResult(RESULT_CANCELED);
      UiHelper.hideKeyboard(this);
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Sets the text according to the converted value.
   *
   * @param hex    The base.
   * @param output AppCompatEditText
   * @param bits   The associated bits.
   */
  private void updateText(boolean hex, AppCompatEditText output, Bits bits) {
    String text = Objects.requireNonNull(output.getText()).toString();
    if (text.isEmpty())
      return;
    if (hex) {
      bits.setValue(text, Radix.DEC);
      output.setText(bits.getHexValue());
    } else {
      bits.setValue(text, Radix.HEX);
      output.setText(bits.getDecValue());
    }
  }

  /**
   * Reconfigures the associated input according to the chosen base
   *
   * @param hex               The base.
   * @param textValue         The input to reconfigure.
   * @param defaultInputFiler The DefaultInputFiler
   */
  private void changeInputType(boolean hex, AppCompatEditText textValue, InputFilter[] defaultInputFiler) {
    if (hex) {
      textValue.setFilters(new InputFilter[]{UiHelper.getInputFilterTextHex()});
      textValue.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
    } else {
      textValue.setFilters(defaultInputFiler);
      textValue.setInputType(InputType.TYPE_CLASS_NUMBER);
      textValue.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
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

  /**
   * Loads the bits according to the value of the base and the text entered.
   *
   * @param editText The reference text.
   * @param hex      The base.
   * @param bits     The loaded bits.
   */
  private void loadBitsValue(AppCompatEditText editText, boolean hex, Bits bits) {
    String text = Objects.requireNonNull(editText.getText()).toString();
    if (text.isEmpty())
      bits.setValue("0", Radix.DEC);
    else
      bits.setValue(text, hex ? Radix.HEX : Radix.DEC);
  }

  /**
   * Returns multiple dashes based on the specified size.
   *
   * @param max The maximum number of dashes to return.
   * @return The string containing the dashes.
   */
  private String getDashes(int max) {
    StringBuilder sb = new StringBuilder(max);
    for (int i = 0; i < max; i++)
      sb.append("-");
    return sb.toString();
  }

  /**
   * Adds the values base10 & base16.
   *
   * @param sb   The working string.
   * @param bits The reference bits.
   */
  private void addBases(StringBuilder sb, Bits bits) {
    sb.append(mTextBase10);
    sb.append(COLON).append(SP);
    sb.append(bits.getDecValue());
    sb.append(NL);
    sb.append(mTextBase16);
    sb.append(COLON).append(SP);
    sb.append(bits.getHexValue());
    sb.append(NL);
  }

  /**
   * Formats the result of the NOT operations on the two reference values.
   *
   * @return The formatted string.
   */
  private String formatNOT() {
    final Operation op = Operation.NOT;
    Bits bits1 = mGlobalValue1.not();
    Bits bits2 = mGlobalValue2.not();
    int max = Math.max(mGlobalValue1.getBitLength(),
        Math.max(mGlobalValue2.getBitLength(),
            Math.max(bits1.getBitLength(), bits2.getBitLength())));
    int maxDashes = evalDashesMax(max, op);
    StringBuilder sb = new StringBuilder();
    sb.append(PADDING).append(PADDING).append(PADDING);
    sb.append(op.getSymbol()).append(mGlobalValue1.toBinary(max));
    sb.append(NL).append(PADDING).append(PADDING).append(PADDING);
    sb.append(getDashes(maxDashes));
    sb.append(NL).append(PADDING).append(PADDING).append(PADDING);
    sb.append(SP).append(bits1.toBinary(max)).append(NL);
    addBases(sb, bits1);
    sb.append(NL);
    sb.append(PADDING).append(PADDING).append(PADDING);
    sb.append(op.getSymbol()).append(mGlobalValue2.toBinary(max));
    sb.append(NL).append(PADDING).append(PADDING).append(PADDING);
    sb.append(getDashes(maxDashes));
    sb.append(NL).append(PADDING).append(PADDING).append(PADDING);
    sb.append(SP).append(bits2.toBinary(max)).append(NL).append(NL);
    addBases(sb, bits2);
    sb.append(NL);
    return sb.toString();
  }

  /**
   * Evaluation of the maximum number of dashes to place according to the operation.
   *
   * @param max The maximum number of dashes.
   * @param op  The operation.
   * @return The real number.
   */
  int evalDashesMax(int max, Operation op) {
    int maxDashes = max;
    maxDashes = maxDashes * 2 - 1;
    if (0 >= maxDashes)
      maxDashes = 1;
    if (op.hasSubSymbol() || Operation.NOT.equals(op))
      maxDashes += 1;
    return maxDashes;
  }

  /**
   * Formats the result of the AND/NOT AND/OR/XOR operations on the specified values.
   *
   * @param bits The calculated value.
   * @param op   The operation.
   * @return The formatted string.
   */
  private String formatOperation(Bits bits, Operation op) {
    int max = Math.max(mGlobalValue1.getBitLength(),
        Math.max(mGlobalValue2.getBitLength(), bits.getBitLength()));
    int maxDashes = evalDashesMax(max, op);
    StringBuilder sb = new StringBuilder();
    sb.append(PADDING).append(PADDING).append(PADDING);
    if (op.hasSubSymbol())
      sb.append(SP);

    sb.append(mGlobalValue1.toBinary(max));
    sb.append(NL).append(PADDING);
    sb.append(op.getSymbol());
    sb.append(NL).append(PADDING).append(PADDING).append(PADDING);
    if (op.hasSubSymbol())
      sb.append(op.getSubSymbol());
    sb.append(mGlobalValue2.toBinary(max));
    sb.append(NL).append(PADDING).append(PADDING).append(PADDING);
    sb.append(getDashes(maxDashes));
    sb.append(NL).append(PADDING).append(PADDING).append(PADDING);
    if (op.hasSubSymbol())
      sb.append(SP);
    sb.append(bits.toBinary(max)).append(NL).append(NL);
    addBases(sb, bits);
    return sb.toString();
  }

  private void evaluateOperation() {
    loadBitsValue(mTextValue1, mBaseHex1, mGlobalValue1);
    loadBitsValue(mTextValue2, mBaseHex2, mGlobalValue2);

    if (null == mOperation)
      mTvResult.setText(getString(R.string.error));
    else {
      switch (mOperation) {
        case NOT:
          mTvResult.setText(formatNOT());
          break;
        case AND:
          mTvResult.setText(formatOperation(mGlobalValue1.and(mGlobalValue2), mOperation));
          break;
        case AND_NOT:
          mTvResult.setText(formatOperation(mGlobalValue1.andNot(mGlobalValue2), mOperation));
          break;
        case OR:
          mTvResult.setText(formatOperation(mGlobalValue1.or(mGlobalValue2), mOperation));
          break;
        case XOR:
          mTvResult.setText(formatOperation(mGlobalValue1.xor(mGlobalValue2), mOperation));
          break;
      }
    }
  }
}
