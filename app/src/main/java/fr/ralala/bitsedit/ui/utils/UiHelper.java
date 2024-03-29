package fr.ralala.bitsedit.ui.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.text.InputFilter;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * UI Helper
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public class UiHelper {
  private UiHelper() {
  }

  /* https://stackoverflow.com/questions/10648449/how-do-i-set-a-edittext-to-the-input-of-only-hexadecimal-numbers/17355026 */
  private static final InputFilter mInputFilterTextHex = (source, start, end, dest, dStart, dEnd) -> {
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
   * Returns an InputFilter to force the input in hexadecimal.
   *
   * @return InputFilter
   */
  public static InputFilter getInputFilterTextHex() {
    return mInputFilterTextHex;
  }

  /**
   * Hides the keyboard.
   *
   * @param activity The activity.
   */
  public static void hideKeyboard(AppCompatActivity activity) {
    /* hide keyboard */
    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
    if (imm.isAcceptingText()) {
      //Find the currently focused view, so we can grab the correct window token from it.
      View view = activity.getCurrentFocus();
      //If no view currently has focus, create a new one, just so we can grab a window token from it
      if (null == view) {
        view = new View(activity);
      }
      imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
  }

  /**
   * Returns the system accent color.
   *
   * @param context The Android context.
   * @return color
   */
  public static @ColorInt int getSystemAccentColor(Context context) {
    TypedValue typedValue = new TypedValue();
    ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(context,
        android.R.style.Theme_DeviceDefault);
    contextThemeWrapper.getTheme().resolveAttribute(android.R.attr.colorAccent,
        typedValue, true);
    return typedValue.data;
  }

  /**
   * Checking if title text color will be black
   *
   * @return boolean
   */
  public static boolean isLightColor(@ColorInt int color) {
    int rgb = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;
    return rgb > 170;
  }

  /**
   * Tests if the night mode is activated or not
   *
   * @param context The Android context.
   * @return boolean
   */
  public static boolean isNightMode(Context context) {
    int nmFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
    return nmFlags == Configuration.UI_MODE_NIGHT_YES;
  }
}
