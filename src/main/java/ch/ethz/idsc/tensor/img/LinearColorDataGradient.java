// code by jph
package ch.ethz.idsc.tensor.img;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.MachineNumberQ;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.opt.LinearInterpolation;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.N;

/** ColorDataGradient maps a {@link Scalar} from the interval [0, 1] to a 4-vector
 * {r, g, b, a} with rgba entries using linear interpolation on a given table of rgba values.
 * 
 * <p>Each color component in the output tensor is an integer or double value in the
 * semi-open interval [0, 256). Because {@link ColorFormat} uses Number::intValue to
 * obtain the int color component, the value 256 is not allowed and results in an Exception.
 *
 * <p>In case NumberQ.of(scalar) == false then a transparent color is assigned.
 * The result is {0, 0, 0, 0}, which corresponds to a transparent color.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/ColorData.html">ColorData</a> */
public class LinearColorDataGradient implements ColorDataGradient {
  private static final Clip CLIP = Clips.interval(0, 256);

  /** colors are generated using {@link LinearInterpolation} of given tensor
   * 
   * @param tensor n x 4 where each row contains {r, g, b, a} with values ranging in [0, 256)
   * @return */
  public static ColorDataGradient of(Tensor tensor) {
    if (Unprotect.dimension1(tensor) != 4)
      throw TensorRuntimeException.of(tensor);
    return new LinearColorDataGradient(tensor.map(CLIP::requireInside));
  }

  // ---
  private final Tensor tensor;
  private final Interpolation interpolation;
  private final Scalar scale;

  /* package */ LinearColorDataGradient(Tensor tensor) {
    this.tensor = tensor;
    interpolation = LinearInterpolation.of(N.DOUBLE.of(tensor));
    scale = DoubleScalar.of(tensor.length() - 1);
  }

  @Override // from ColorDataGradient
  public Tensor apply(Scalar scalar) {
    Scalar value = scalar.multiply(scale); // throws Exception for GaussScalar
    return MachineNumberQ.of(value) //
        ? interpolation.at(value)
        : StaticHelper.transparent();
  }

  @Override // from ColorDataGradient
  public LinearColorDataGradient deriveWithFactor(Scalar factor) {
    return new LinearColorDataGradient(StaticHelper.withFactor(tensor, Clips.unit().requireInside(factor)));
  }
}
