// code by jph
package ch.ethz.idsc.tensor.red;

import java.util.function.BinaryOperator;
import java.util.function.Function;

public enum Min {
  ;
  /** function of(...) is a {@link BinaryOperator} that can be used in reduce()
   * 
   * @param a
   * @param b
   * @return the smaller one among a and b */
  public static <T> T of(T a, T b) {
    @SuppressWarnings("unchecked")
    Comparable<T> comparable = (Comparable<T>) a;
    return comparable.compareTo(b) > 0 ? b : a;
  }

  /** @param a
   * @return */
  public static <T> Function<T, T> function(T a) {
    return b -> of(a, b);
  }
}