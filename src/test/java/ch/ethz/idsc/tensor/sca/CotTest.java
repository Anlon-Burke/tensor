// code by jph
package ch.ethz.idsc.tensor.sca;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class CotTest extends TestCase {
  public void testSimple() {
    Scalar scalar = RealScalar.of(1.23);
    Scalar res = Cot.FUNCTION.apply(scalar);
    assertTrue(Chop._12.close(res, RealScalar.of(0.3546331016766021)));
  }

  public void testComplex() {
    Scalar scalar = ComplexScalar.of(-1.23, 3);
    Scalar res = Cot.FUNCTION.apply(scalar);
    assertTrue(Chop._12.close(res, Scalars.fromString("-0.003111382117752743` - 0.9961526961973638` *I")));
  }

  public void testTensor() {
    Tensor vector = Tensors.vector(-1, 2, -3);
    Tensor expected = Tensors.fromString("{-0.6420926159343306`, -0.45765755436028577`, 7.015252551434534`}");
    assertTrue(Chop._12.close(Cot.of(vector), expected));
  }
}
