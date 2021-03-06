// code by jph
package ch.ethz.idsc.tensor.alg;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class InsertTest extends TestCase {
  public void testAIndex0() {
    Tensor tensor = Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}}");
    Tensor result = Insert.of(tensor, Tensors.fromString("{{{9}}}"), 0);
    assertEquals(result, Tensors.fromString("{{{{9}}}, {1}, {2}, {3, 4}, 5, {}}"));
  }

  public void testAIndex1() {
    Tensor tensor = Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}}");
    Tensor result = Insert.of(tensor, Tensors.fromString("{{{9}}}"), 1);
    assertEquals(result, Tensors.fromString("{{1}, {{{9}}}, {2}, {3, 4}, 5, {}}"));
  }

  public void testAIndexLast() {
    Tensor tensor = Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}}");
    Tensor result = Insert.of(tensor.unmodifiable(), Tensors.fromString("{{{9}}}"), 5);
    assertEquals(result, Tensors.fromString("{{1}, {2}, {3, 4}, 5, {}, {{{9}}}}"));
  }

  public void testExample() {
    assertEquals(Insert.of(Tensors.vector(1, 2, 3).unmodifiable(), RealScalar.of(0), 0), Range.of(0, 4));
    assertEquals(Insert.of(Tensors.vector(0, 2, 3).unmodifiable(), RealScalar.of(1), 1), Range.of(0, 4));
    assertEquals(Insert.of(Tensors.vector(0, 1, 2).unmodifiable(), RealScalar.of(3), 3), Range.of(0, 4));
  }

  public void testAFailSmall() {
    Insert.of(Tensors.vector(1, 2, 3), RealScalar.ZERO, 0);
    try {
      Insert.of(Tensors.vector(1, 2, 3), RealScalar.ZERO, -1);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testAFailLarge() {
    Insert.of(Tensors.vector(1, 2, 3), RealScalar.ZERO, 3);
    try {
      Insert.of(Tensors.vector(1, 2, 3), RealScalar.ZERO, 4);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNullFail() {
    try {
      Insert.of(null, RealScalar.ZERO, 0);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      Insert.of(Tensors.vector(1, 2, 3), null, 0);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
