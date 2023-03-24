package fr.ralala.bitsedit;

import android.app.Application;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

import fr.ralala.bitsedit.ui.utils.AppColors;
import fr.ralala.bitsedit.utils.Bits;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Application context.
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public class ApplicationCtx extends Application {
  private final Bits mBits = new Bits();
  private final AppColors mAppColors = new AppColors();

  @Override
  public void onCreate() {
    super.onCreate();
   selectTheme();
  }

  /**
   * Returns the global bits value.
   *
   * @return Bits
   */
  public Bits getBits() {
    return mBits;
  }

  /**
   * Returns the SystemColors
   *
   * @return AppColors
   */
  public AppColors getAppColors() {
    return mAppColors;
  }

  /**
   * If the Android version is lower than Build.VERSION_CODES.Q
   * the night theme will be selected otherwise we follow the system theme.
   */
  private void selectTheme() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
    } else {
      AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }
  }
}
