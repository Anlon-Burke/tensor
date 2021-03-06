// code by jph
// implementation adapted from Ruby code of https://en.wikipedia.org/wiki/Exponentiation_by_squaring
package ch.ethz.idsc.tensor.alg;

import java.math.BigInteger;

/** exponentiation with integer exponents
 * 
 * implementation uses exponentiation by squaring
 * 
 * interface used by MatrixPower, RationalScalar, ComplexScalar, and GaussScalar
 * 
 * @param <T> may also be Integer etc. */
public abstract class BinaryPower<T> {
  /** @return value when exponent equals zero */
  protected abstract T zeroth();

  /** @param object to invert when the given exponent is negative
   * @return */
  protected abstract T invert(T object);

  /** @param factor1
   * @param factor2
   * @return product factor1 * factor2 */
  protected abstract T multiply(T factor1, T factor2);

  /** @param x
   * @param exponent
   * @return x to the power of the given exponent */
  public final T apply(T x, long exponent) {
    return apply(x, BigInteger.valueOf(exponent));
  }

  /** @param x
   * @param exponent
   * @return x to the power of the given exponent */
  public final T apply(T x, BigInteger exponent) {
    T result = zeroth();
    if (exponent.signum() == 0)
      return result;
    if (exponent.signum() == -1) { // convert problem to positive exponent
      exponent = exponent.negate();
      x = invert(x);
    }
    // the below implementation was adapted from
    // https://en.wikipedia.org/wiki/Exponentiation_by_squaring
    // Section: Computation by powers of 2
    // non-recursive implementation of the algorithm in Ruby
    while (true) { // iteration
      if (exponent.testBit(0))
        result = multiply(x, result);
      exponent = exponent.shiftRight(1);
      if (exponent.signum() == 0)
        return result;
      x = multiply(x, x);
    }
  }
}
