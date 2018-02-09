package fr.ralala.bitsedit;


import java.math.BigInteger;

/**
 *******************************************************************************
 * <p><b>Project BitsEdit</b><br/>
 * Bits edit
 * </p>
 * @author Keidan
 *
 *******************************************************************************
 */

public class Bits {

  private long value = 0L;

  boolean isBit(int position) {
    return (value & (1L << position)) != 0;
  }

  void setBit(int position, boolean val) {
    value = !val ? (value & ~(1 << position)) : (value | (1 << position));
  }

  String getValueFromBase(int base) {
    return base == 10 ? getDecValue() : getHexValue();
  }

  String getDecValue() {
    /* force unsigned long */
    BigInteger b = BigInteger.valueOf(value);
    if(b.signum() < 0)
      b = b.add(BigInteger.ONE.shiftLeft(64));
    return b.toString();
  }

  String getHexValue() {
    return String.format("%X", value);
  }

  void setValue(String val, int base) {
    value = val.isEmpty() ? 0L : Long.valueOf(val, base);
  }
}
