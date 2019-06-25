// code by jph
package ch.ethz.idsc.tensor.img;

import java.util.Arrays;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Increment;
import junit.framework.TestCase;

public class ColorDataGradientsTest extends TestCase {
  public void testDimensions() {
    for (ScalarTensorFunction cdf : ColorDataGradients.values()) {
      assertEquals(Dimensions.of(cdf.apply(RealScalar.ZERO)), Arrays.asList(4));
      assertEquals(Dimensions.of(cdf.apply(RealScalar.ONE)), Arrays.asList(4));
    }
  }

  public void testQuantity() {
    Scalar scalar = Quantity.of(Double.POSITIVE_INFINITY, "s");
    assertTrue(Chop.NONE.allZero(ColorDataGradients.COPPER.apply(scalar)));
    assertTrue(Chop.NONE.allZero(ColorDataGradients.HUE.apply(scalar)));
    assertTrue(Chop.NONE.allZero(ColorDataGradients.GRAYSCALE.apply(scalar)));
  }

  public void testUnmodified() {
    Scalar nan = DoubleScalar.INDETERMINATE;
    Tensor copy = ColorDataGradients.CLASSIC.apply(nan);
    ColorDataGradients.CLASSIC.apply(nan).set(Increment.ONE, 1);
    assertEquals(copy, ColorDataGradients.CLASSIC.apply(nan));
    assertTrue(Chop.NONE.allZero(ColorDataGradients.CLASSIC.apply(nan)));
  }

  public void testDeriveWithAlpha() {
    ColorDataGradient cdg1 = ColorDataGradients.CLASSIC.deriveWithFactor(RealScalar.ONE);
    ColorDataGradient cdg2 = ColorDataGradients.CLASSIC.deriveWithFactor(RationalScalar.HALF);
    Tensor rgba1 = cdg1.apply(RealScalar.of(.5));
    Tensor rgba2 = cdg2.apply(RealScalar.of(.5));
    assertEquals(rgba1, Tensors.vector(47.5, 195, 33.5, 255));
    assertEquals(rgba1.get(3), RealScalar.of(255));
    assertEquals(rgba2.get(3), RealScalar.of(127.5));
  }

  public void testStrict() {
    for (ColorDataGradients colorDataGradients : ColorDataGradients.values()) {
      Tensor tensor = ResourceData.of("/colorscheme/" + colorDataGradients.name().toLowerCase() + ".csv");
      LinearColorDataGradient.of(tensor);
    }
  }

  public void testFail() {
    for (ScalarTensorFunction cdf : ColorDataGradients.values()) {
      ColorDataGradients cdg = (ColorDataGradients) cdf;
      cdf.apply(RealScalar.of(0.5));
      cdf.apply(RealScalar.of(0.99));
      if (cdg.equals(ColorDataGradients.HUE)) {
        // hue is implemented periodically [0,1) == [1,2) == ...
      } else {
        try {
          cdf.apply(RealScalar.of(-0.1));
          fail();
        } catch (Exception exception) {
          // ---
        }
        try {
          cdf.apply(RealScalar.of(1.1));
          fail();
        } catch (Exception exception) {
          // ---
        }
      }
      try {
        cdf.apply(ComplexScalar.of(0.5, 0.5));
        fail();
      } catch (Exception exception) {
        // ---
      }
    }
  }
}
