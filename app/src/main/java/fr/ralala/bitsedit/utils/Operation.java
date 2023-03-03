package fr.ralala.bitsedit.utils;

import androidx.annotation.NonNull;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Bit operation enum
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public enum Operation {
  AND("&", ""),
  AND_NOT("&", "~"),
  NOT("~", ""),
  OR("|", ""),
  XOR("^", "");
  private final String mSymbol;
  private final String mSubSymbol;

  Operation(String symbol, String subSymbol) {
    mSymbol = symbol;
    mSubSymbol = subSymbol;
  }

  public String getSubSymbol() {
    return mSubSymbol;
  }

  public boolean hasSubSymbol() {
    return !mSubSymbol.isEmpty();
  }

  public String getSymbol() {
    return mSymbol;
  }

}
