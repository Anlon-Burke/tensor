// code by jph
package ch.ethz.idsc.tensor.pdf;

import java.io.Serializable;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.QuantityTensor;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class BinningMethodTest extends TestCase {
  public void testSimple() {
    Scalar width = BinningMethod.SQRT.apply(Tensors.vector(2, 4, 3, 6));
    assertEquals(width, RealScalar.of(2));
    assertTrue(BinningMethod.SQRT instanceof Serializable);
  }

  public void testQuantity() {
    Tensor samples = QuantityTensor.of(Tensors.vector(1, 2, 3, 1, 2, 3, 7, 2, 9, 3, 3), "Apples");
    for (BinningMethod bm : BinningMethod.values()) {
      Scalar width = bm.apply(samples);
      assertTrue(width instanceof Quantity);
      Scalar value = QuantityMagnitude.singleton("Apples").apply(width);
      assertTrue(Sign.isPositive(value));
    }
  }

  public void testFail() {
    for (BinningMethod bm : BinningMethod.values())
      try {
        bm.apply(Tensors.empty());
        assertTrue(false);
      } catch (Exception exception) {
        // ---
      }
  }
}
