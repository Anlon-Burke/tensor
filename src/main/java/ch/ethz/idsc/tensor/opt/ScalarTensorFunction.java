// code by jph
package ch.ethz.idsc.tensor.opt;

import java.io.Serializable;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataFunction;

/** serializable interface for functions that map a {@link Scalar} to a {@link Tensor}
 * 
 * Examples: {@link ColorDataFunction}, {@link BSplineFunction} */
public interface ScalarTensorFunction extends Function<Scalar, Tensor>, Serializable {
  // ---
}