package fr.ralala.bitsedit;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Radix enum
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */
public enum Radix {

  DEC(10),
  HEX(16);

  private final int mRadix;

  Radix(int radix) {
    mRadix = radix;
  }

  int getRadix() {
    return mRadix;
  }
}
