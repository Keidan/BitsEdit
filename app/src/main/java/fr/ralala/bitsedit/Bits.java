package fr.ralala.bitsedit;


import java.math.BigInteger;

/**
 * ******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Bits edit
 * </p>
 *
 * @author Keidan
 * <p>
 * License: GPLv3
 * </p>
 * ******************************************************************************
 */

public class Bits {

  private long mValue = 0L;

  /**
   * Tests if the bit of the specified position is not set.
   * @param position The position.
   * @return true unset, false set.
   */
  boolean isNotBit(int position) {
    return (mValue & (1L << position)) == 0;
  }

  /**
   * Sets the bit of the specified position to 1 or 0.
   * @param position The position.
   * @param val The value (true set, false unset).
   */
  void setBit(int position, boolean val) {
    mValue = !val ? (mValue & ~(1 << position)) : (mValue | (1 << position));
  }

  /**
   * Returns the value associated with the base.
   * @param base The base.
   * @return The value.
   */
  String getValueFromBase(int base) {
    return base == 10 ? getDecValue() : getHexValue();
  }

  /**
   * Returns the decimal value.
   * @return Decimal string.
   */
  String getDecValue() {
    /* force unsigned long */
    BigInteger b = BigInteger.valueOf(mValue);
    if (b.signum() < 0)
      b = b.add(BigInteger.ONE.shiftLeft(64));
    return b.toString();
  }

  /**
   * Returns the hex value.
   * @return Hex string.
   */
  String getHexValue() {
    return String.format("%X", mValue);
  }

  /**
   * Sets the value from string.
   * @param val The value.
   * @param base The base.
   */
  void setValue(String val, int base) {
    mValue = val.isEmpty() ? 0L : Long.valueOf(val, base);
  }
}
