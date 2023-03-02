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
  static final int MAX_BIT = 63;
  static final int NB_SHIFT = 1;
  private static final String DEF_STR_0 = "0";
  private static final String DEF_STR_1 = "1";
  private static final String DEF_STR_MAX = "10000000000000000";
  private static final BigInteger MAX_VALUE = new BigInteger(DEF_STR_MAX, Radix.HEX.getRadix());
  private BigInteger mValue = new BigInteger(DEF_STR_0, Radix.DEC.getRadix());

  Bits() {

  }

  private Bits(BigInteger bi) {
    mValue = bi;
  }

  /**
   * Tests if the bit of the specified position is not set.
   *
   * @param position The position.
   * @return true unset, false set.
   */
  boolean isNotBit(int position) {
    return (mValue.longValue() & (1L << position)) == 0;
  }

  /**
   * Sets the bit of the specified position to 1 or 0.
   *
   * @param position The position.
   * @param val      The value (true set, false unset).
   */
  void setBit(int position, boolean val) {
    if (val)
      mValue = mValue.setBit(position);
    else
      mValue = mValue.clearBit(position);
  }

  /**
   * Shifts to left.
   */
  void shiftLeft() {
    if (mValue.longValue() == 0L)
      mValue = new BigInteger(DEF_STR_1, Radix.DEC.getRadix());
    else {
      BigInteger bi = mValue.shiftLeft(NB_SHIFT);
      if (bi.compareTo(MAX_VALUE) < 0) {
        mValue = bi;
      } else {
        mValue = mValue.clearBit(MAX_BIT);
        mValue = mValue.shiftLeft(NB_SHIFT);
      }
    }
  }

  /**
   * Shifts to right.
   */
  void shiftRight() {
    mValue = mValue.shiftRight(NB_SHIFT);
  }

  /**
   * Returns the value associated with the base.
   *
   * @param base The base.
   * @return The value.
   */
  String getValueFromBase(Radix base) {
    return base == Radix.DEC ? getDecValue() : getHexValue();
  }

  /**
   * Returns the decimal value.
   *
   * @return Decimal string.
   */
  String getDecValue() {
    return mValue.toString();
  }

  /**
   * Returns the hex value.
   *
   * @return Hex string.
   */
  String getHexValue() {
    return String.format("%X", mValue);
  }

  /**
   * Sets the value from string.
   *
   * @param val  The value.
   * @param base The base.
   */
  void setValue(String val, Radix base) {
    mValue = val.isEmpty() ? new BigInteger(DEF_STR_1, Radix.DEC.getRadix()) : new BigInteger(val, base.getRadix());
  }

  /**
   * AND operation.
   */
  Bits and(Bits bits) {
    return new Bits(mValue.and(bits.mValue));
  }

  /**
   * AND NOT operation.
   */
  Bits andNot(Bits bits) {
    return new Bits(mValue.andNot(bits.mValue));
  }

  /**
   * OR operation.
   */
  Bits or(Bits bits) {
    return new Bits(mValue.or(bits.mValue));
  }

  /**
   * XOR operation.
   */
  Bits xor(Bits bits) {
    return new Bits(mValue.xor(bits.mValue));
  }

  int getBitLength() {
    return mValue.bitLength();
  }
  /**
   * Returns the bits in binary format
   *
   * @return String
   */
  String toBinary(int maxBits) {
    if(maxBits == 0) {
      return "0";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = maxBits - 1; i >= 0; i--) {
      sb.append(isNotBit(i) ? "0" : "1");
      if (i > 0)
        sb.append(" ");
    }
    return sb.toString();
  }
}
