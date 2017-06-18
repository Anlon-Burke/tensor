// code by jph
package ch.ethz.idsc.tensor;

import junit.framework.TestCase;

public class MachineNumberQTest extends TestCase {
  public void testSimple() {
    assertTrue(MachineNumberQ.of(RealScalar.of(0.)));
    assertFalse(MachineNumberQ.of(RealScalar.ZERO));
  }

  public void testComplex() {
    assertTrue(MachineNumberQ.of(ComplexScalar.of(0., .3)));
    assertFalse(MachineNumberQ.of(ComplexScalar.of(0., 2)));
  }

  public void testGauss() {
    assertFalse(MachineNumberQ.of(GaussScalar.of(3, 7)));
    assertFalse(MachineNumberQ.of(GaussScalar.of(0, 7)));
  }

  public void testTensor() {
    assertFalse(MachineNumberQ.of(Tensors.vector(1.)));
  }

  public void testCorner() {
    assertFalse(MachineNumberQ.of(RealScalar.POSITIVE_INFINITY));
    assertFalse(MachineNumberQ.of(RealScalar.NEGATIVE_INFINITY));
    assertFalse(MachineNumberQ.of(RealScalar.of(Double.NaN)));
  }
}
