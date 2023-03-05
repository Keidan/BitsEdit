package fr.ralala.bitsedit.ui;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import fr.ralala.bitsedit.R;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Simple button group
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public class ButtonGroup implements View.OnClickListener {
  private final @ColorInt int mBackgroundFocus;
  private final @ColorInt int mBackgroundUnFocus;
  private final @ColorInt int mForegroundFocus;
  private final @ColorInt int mForegroundUnFocus;

  private final List<AppCompatButton> mButtons = new ArrayList<>();
  private AppCompatButton mBtUnFocus;
  private final List<Integer> mIds;
  private final ButtonGroupListener mListener;

  interface ButtonGroupListener {
    void onButtonClick(@IdRes int id);
  }

  /**
   * Checking if title text color will be black
   *
   * @return boolean
   */
  private boolean isLightColor(@ColorInt int color) {
    int rgb = (Color.red(color) + Color.green(color) + Color.blue(color)) / 3;
    return rgb > 210;
  }

  ButtonGroup(AppCompatActivity activity, List<Integer> ids, ButtonGroupListener listener) {
    mIds = ids;
    mListener = listener;
    mBackgroundFocus = UiHelper.getSystemAccentColor(activity);
    if (isLightColor(mBackgroundFocus))
      mForegroundFocus = ContextCompat.getColor(activity, R.color.colorPrimaryDark);
    else
      mForegroundFocus = ContextCompat.getColor(activity, R.color.textColor);
    mBackgroundUnFocus = ContextCompat.getColor(activity, R.color.colorButtonNormal);
    mForegroundUnFocus = ContextCompat.getColor(activity, R.color.textColor);
    for (Integer id : mIds) {
      AppCompatButton bt = (AppCompatButton) activity.findViewById(id);
      bt.setBackgroundColor(mBackgroundUnFocus);
      bt.setOnClickListener(this);
      mButtons.add(bt);
    }
    setFocus(mButtons.get(0), mButtons.get(0));
  }

  /**
   * Called when a button is clicked.
   *
   * @param v The view that was clicked.
   */
  @Override
  public void onClick(View v) {
    for (int i = 0; i < mIds.size(); i++) {
      @IdRes int id = mIds.get(i);
      if (v.getId() == id) {
        setFocus(mBtUnFocus, mButtons.get(i));
        mListener.onButtonClick(id);
        break;
      }
    }
  }

  /**
   * Changes the focus effect.
   *
   * @param unFocus Button not focused.
   * @param focus   Focused button.
   */
  private void setFocus(AppCompatButton unFocus, AppCompatButton focus) {
    unFocus.setBackgroundColor(mBackgroundUnFocus);
    unFocus.setTextColor(mForegroundUnFocus);
    focus.setBackgroundColor(mBackgroundFocus);
    focus.setTextColor(mForegroundFocus);
    mBtUnFocus = focus;
  }
}
