// code by jph
package ch.ethz.idsc.tensor;

import java.util.Arrays;

import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.LieAlgebras;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class TensorGetTest extends TestCase {
  public void testGetEmpty() {
    assertEquals(Tensors.empty().get(), Tensors.empty());
    assertEquals(Tensors.empty().get(Arrays.asList()), Tensors.empty());
    assertEquals(Array.zeros(2, 3).get(), Array.zeros(2, 3));
    assertEquals(Array.zeros(2, 3).get(Arrays.asList()), Array.zeros(2, 3));
  }

  public void testGetScalar() {
    assertTrue(IdentityMatrix.of(10).Get(3, 4) instanceof RealScalar);
  }

  public void testGet() {
    Tensor matrix = Tensors.matrixInt( //
        new int[][] { { 3, 4 }, { 1, 2 }, { 9, 8 } });
    assertEquals(matrix.get(0, 0), Tensors.fromString("3"));
    assertEquals(matrix.get(1, 1), Tensors.fromString("2"));
    assertEquals(matrix.get(0), Tensors.fromString("{3, 4}"));
    assertEquals(matrix.get(1), Tensors.fromString("{1, 2}"));
    assertEquals(matrix.get(2), Tensors.fromString("{9, 8}"));
    assertEquals(matrix.get(Tensor.ALL, 0), Tensors.fromString("{3, 1, 9}"));
    assertEquals(matrix.get(Tensor.ALL, 1), Tensors.fromString("{4, 2, 8}"));
  }

  public void testGetAllSimple() {
    Tensor a = Array.zeros(3, 4);
    Tensor c = a.get(Tensor.ALL, 2);
    c.set(RealScalar.ONE, 1);
    assertEquals(a, Array.zeros(3, 4));
  }

  public void testGetAll() {
    Tensor matrix = RandomVariate.of(UniformDistribution.unit(), 2, 3, 4);
    assertEquals(matrix.get(), matrix);
    assertEquals(matrix.get(Tensor.ALL), matrix);
    assertEquals(matrix.get(Tensor.ALL, Tensor.ALL), matrix);
    assertEquals(matrix.get(Tensor.ALL, Tensor.ALL, Tensor.ALL), matrix);
  }

  public void testGetAll1() {
    Tensor a = Array.zeros(3).unmodifiable();
    a.get().set(RealScalar.ONE, 1);
    // a.get(1).set(RealScalar.ONE, 1); // scalar is immutable
    Tensor b = a.get(Tensor.ALL);
    b.set(Tensors.vector(1, 2, 3), Tensor.ALL);
    assertEquals(b, Tensors.vector(1, 2, 3));
    assertEquals(a, Array.zeros(3));
  }

  public void testGetAll2() {
    Tensor matrix = Array.zeros(3, 4).unmodifiable();
    matrix.get().set(RealScalar.ONE, 1);
    matrix.get(1).set(RealScalar.ONE, 1);
    matrix.get(Tensor.ALL).set(Tensors.vector(1, 2, 3), Tensor.ALL);
    matrix.get(Tensor.ALL, 2).set(RealScalar.ONE, 1);
    matrix.get(2, Tensor.ALL).set(Tensors.vector(1, 2, 3, 4), Tensor.ALL);
    matrix.get(Tensor.ALL, Tensor.ALL).set(Array.of(l -> RealScalar.ONE, 3, 4), Tensor.ALL, Tensor.ALL);
    assertEquals(matrix, Array.zeros(3, 4));
  }

  public void testGetAll3() {
    Tensor tensor = Array.zeros(3, 4, 3).unmodifiable();
    tensor.get().set(RealScalar.ONE, 1);
    tensor.get(1).set(RealScalar.ONE, 1);
    tensor.get(Tensor.ALL).set(Tensors.vector(1, 2, 3), Tensor.ALL);
    tensor.get(Tensor.ALL, 2).set(RealScalar.ONE, 1);
    tensor.get(2, Tensor.ALL).set(Tensors.vector(1, 2, 3, 4), Tensor.ALL);
  }

  public void testGetAllFail() {
    Tensor matrix = LieAlgebras.so3();
    try {
      matrix.Get(Tensor.ALL);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
