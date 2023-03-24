package fr.ralala.bitsedit.ui.utils;

import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import fr.ralala.bitsedit.ApplicationCtx;
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
  private final ColorStateList mBackgroundFocus;
  private final ColorStateList mBackgroundUnFocus;
  private final @ColorInt int mForegroundFocus;
  private final @ColorInt int mForegroundUnFocus;

  private final List<AppCompatButton> mButtons = new ArrayList<>();
  private AppCompatButton mBtUnFocus;
  private final List<Integer> mIds;
  private final ButtonGroupListener mListener;

  public interface ButtonGroupListener {
    void onButtonClick(@IdRes int id);
  }

  public ButtonGroup(AppCompatActivity activity, List<Integer> ids, ButtonGroupListener listener) {
    mIds = ids;
    mListener = listener;
    ApplicationCtx app = (ApplicationCtx) activity.getApplication();
    if (UiHelper.isLightColor(app.getAppColors().getAccentColor()))
      mForegroundFocus = ContextCompat.getColor(activity, R.color.system_neutral1_900);
    else
      mForegroundFocus = ContextCompat.getColor(activity, R.color.system_neutral1_50);
    mForegroundUnFocus = app.getAppColors().getTextColor();
    for (Integer id : mIds) {
      AppCompatButton bt = (AppCompatButton) activity.findViewById(id);
      bt.setOnClickListener(this);
      mButtons.add(bt);
    }

    mBackgroundUnFocus = getColorStateList(app.getAppColors().getButtonBackgroundColor());
    mBackgroundFocus = getColorStateList(app.getAppColors().getAccentColor());
    setFocus(mButtons.get(0), mButtons.get(0));
  }

  private ColorStateList getColorStateList(int background) {
    return new ColorStateList(new int[][]{
        new int[]{}
    }, new int[]{
        background
    });
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
    unFocus.setBackgroundTintList(mBackgroundUnFocus);
    unFocus.setTextColor(mForegroundUnFocus);
    focus.setBackgroundTintList(mBackgroundFocus);
    focus.setTextColor(mForegroundFocus);
    mBtUnFocus = focus;
  }

}
