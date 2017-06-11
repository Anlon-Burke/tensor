// code by jph
package ch.ethz.idsc.tensor.pdf;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.sca.Exp;

/** consistent with Mathematica::PoissonDistribution
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/PoissonDistribution.html">PoissonDistribution</a> */
public class PoissonDistribution implements DiscreteDistribution {
  /** Example:
   * PDF[PoissonDistribution[Lambda], 2] == 1/(3!) Exp[-Lambda] Lambda^3
   * 
   * @param lambda
   * @return */
  public static DiscreteDistribution of(Scalar lambda) {
    return new PoissonDistribution(lambda);
  }

  /***************************************************/
  private final Scalar lambda;
  private final Tensor values = Tensors.empty();

  private PoissonDistribution(Scalar lambda) {
    this.lambda = lambda;
    values.append(Exp.of(lambda.negate()));
  }

  @Override // from DiscreteDistribution
  public int lowerBound() {
    return 0;
  }

  @Override // from DiscreteDistribution
  public Scalar p_equals(int n) {
    if (n < 0)
      return RealScalar.ZERO;
    while (values.length() <= n) {
      Scalar factor = lambda.divide(RealScalar.of(values.length()));
      values.append(Last.of(values).multiply(factor));
    }
    return values.Get(n);
  }
}
