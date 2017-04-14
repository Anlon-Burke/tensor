// code by jph
package ch.ethz.idsc.tensor;

import java.math.BigInteger;

// EXPERIMENTAL
// could be used to store pure integer rational scalars for memory efficiency
/* package */ class IntegerScalar {
  public static RealScalar of(BigInteger value) {
    return RationalScalar.of(value, BigInteger.ONE);
  }

  public static RealScalar of(long value) {
    return RationalScalar.of(value, 1);
  }

  private IntegerScalar() {
  }
}
