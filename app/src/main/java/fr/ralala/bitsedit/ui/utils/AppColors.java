package fr.ralala.bitsedit.ui.utils;

import android.content.Context;

import androidx.appcompat.widget.AppCompatTextView;

import fr.ralala.bitsedit.R;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Application colors.
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public class AppColors {
  private int mAccentColor = 0;
  private int mTextColor = 0;
  private int mButtonBackgroundColor = 0;
  private int mDarkGrayColor = 0;
  private int mLightGrayColor = 0;

  public void load(Context context) {
    mDarkGrayColor = context.getColor(R.color.colorDarkGray);
    mLightGrayColor = context.getColor(R.color.colorLightGray);
    AppCompatTextView tv = new AppCompatTextView(context);
    mTextColor = tv.getCurrentTextColor();
    mAccentColor = UiHelper.getSystemAccentColor(context);
    if(UiHelper.isNightMode(context))
      mButtonBackgroundColor = context.getColor(R.color.button_dark);
    else
      mButtonBackgroundColor = context.getColor(R.color.button_light);
  }

  /**
   * Returns the gray color, if the text color
   * is light the method will return a light gray otherwise a dark gray.
   *
   * @param context The Android context.
   * @return Gray color.
   */
  public int getGrayColor(Context context) {
    if (!UiHelper.isNightMode(context)) {
      return mLightGrayColor;
    } else {
      return mDarkGrayColor;
    }
  }

  /**
   * Returns the accent color.
   *
   * @return Accent color.
   */
  public int getAccentColor() {
    return mAccentColor;
  }

  /**
   * Returns the text color.
   *
   * @return Text color.
   */
  public int getTextColor() {
    return mTextColor;
  }


  /**
   * Returns the button background color.
   *
   * @return Button background color.
   */
  public int getButtonBackgroundColor() {
    return mButtonBackgroundColor;
  }
}
