// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.lie.LieAlgebras;
import junit.framework.TestCase;

public class SquareMatrixQTest extends TestCase {
  public void testMatrix() {
    assertTrue(SquareMatrixQ.of(IdentityMatrix.of(10)));
    assertTrue(SquareMatrixQ.of(IdentityMatrix.of(10).unmodifiable()));
    assertFalse(SquareMatrixQ.of(Array.zeros(3, 4)));
  }

  public void testOthers() {
    assertFalse(SquareMatrixQ.of(UnitVector.of(10, 3)));
    assertFalse(SquareMatrixQ.of(LieAlgebras.so3()));
    assertFalse(SquareMatrixQ.of(RealScalar.ONE));
  }

  public void testEmpty() {
    assertFalse(SquareMatrixQ.of(Tensors.empty()));
  }
}
