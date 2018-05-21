// code by jph
package ch.ethz.idsc.tensor.opt;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Increment;
import junit.framework.TestCase;

public class LinearInterpolationTest extends TestCase {
  public void testEmpty() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.empty());
    assertEquals(interpolation.get(Tensors.empty()), Tensors.empty());
  }

  public void testEmpty1() {
    Tensor tensor = Tensors.vector(10, 20, 30, 40);
    Interpolation interpolation = LinearInterpolation.of(tensor);
    Tensor res = interpolation.get(Tensors.empty());
    assertEquals(res, tensor);
  }

  public void testEmpty2() {
    Tensor tensor = Tensors.vector(10, 20, 30, 40);
    Tensor ori = tensor.copy();
    Interpolation interpolation = LinearInterpolation.of(tensor);
    Tensor res = interpolation.get(Tensors.empty());
    res.set(Increment.ONE, Tensor.ALL);
    assertEquals(tensor, ori);
    assertFalse(tensor.equals(res));
    assertEquals(interpolation.get(Tensors.empty()), ori);
  }

  public void testSimple() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.vector(10, 20, 30, 40));
    assertEquals(interpolation.get(Tensors.vector(0)), RealScalar.of(10));
    assertEquals(interpolation.get(Tensors.vector(2)), RealScalar.of(30));
    assertEquals(interpolation.get(Tensors.vector(2.5)), RealScalar.of(35));
    assertEquals(interpolation.get(Tensors.vector(3)), RealScalar.of(40));
  }

  public void testMatrix1() {
    Tensor tensor = Tensors.matrix(new Number[][] { //
        { 5, 5, 5 }, //
        { 1, 10, 100 } //
    });
    Interpolation interpolation = LinearInterpolation.of(tensor);
    {
      Tensor res = interpolation.get(Tensors.vector(1, 3).multiply(RationalScalar.of(1, 2)));
      assertEquals(res, RealScalar.of(30)); // 5+5+10+100==120 -> 120 / 4 == 30
    }
    {
      Tensor res = interpolation.get(Tensors.of(RationalScalar.of(1, 2)));
      Tensor from = Tensors.fromString("{3, 15/2, 105/2}");
      assertEquals(res, from);
    }
  }

  public void testMatrix2() {
    Tensor tensor = Tensors.matrix(new Number[][] { //
        { 5, 5, 5 }, //
        { 1, 10, 100 } //
    });
    Interpolation interpolation = LinearInterpolation.of(tensor);
    assertEquals(interpolation.get(Tensors.vector(1)), Tensors.vector(1, 10, 100));
    assertEquals(interpolation.get(Tensors.vector(1, 2)), RealScalar.of(100));
    assertEquals(interpolation.get(Tensors.vector(0)), Tensors.vector(5, 5, 5));
    assertEquals(interpolation.get(Tensors.vector(1, 0)), RealScalar.of(1));
    assertEquals(interpolation.get(Tensors.vector(0, 0)), RealScalar.of(5));
  }

  public void testRank3() {
    Tensor arr = Array.of(Tensors::vector, 2, 3);
    Interpolation interpolation = LinearInterpolation.of(arr);
    // System.out.println(Dimensions.of(arr));
    // Tensor res =
    interpolation.get(Tensors.vector(0.3, 1.8, 0.3));
    // System.out.println(res);
    // res.append(null);
  }

  public void testQuantity() {
    Scalar qs1 = Quantity.of(1, "m");
    Scalar qs2 = Quantity.of(4, "m");
    Scalar qs3 = Quantity.of(2, "m");
    Tensor vector = Tensors.of(qs1, qs2, qs3);
    Interpolation interpolation = LinearInterpolation.of(vector);
    Scalar r = Quantity.of((1 + 4) * 0.5, "m");
    Scalar s = interpolation.Get(Tensors.vector(0.5));
    assertEquals(s, r);
  }

  public void testQuantity2() {
    Tensor v1;
    {
      Scalar qs1 = Quantity.of(1, "m");
      Scalar qs2 = Quantity.of(4, "m");
      Scalar qs3 = Quantity.of(2, "m");
      v1 = Tensors.of(qs1, qs2, qs3);
    }
    Tensor v2;
    {
      Scalar qs1 = Quantity.of(9, "s");
      Scalar qs2 = Quantity.of(6, "s");
      Scalar qs3 = Quantity.of(-3, "s");
      v2 = Tensors.of(qs1, qs2, qs3);
    }
    Tensor matrix = Transpose.of(Tensors.of(v1, v2));
    Interpolation interpolation = LinearInterpolation.of(matrix);
    Scalar r1 = Quantity.of((1 + 4) * 0.5, "m");
    Scalar r2 = Quantity.of((9 + 6) * 0.5, "s");
    Tensor vec = interpolation.get(Tensors.vector(0.5));
    assertEquals(vec, Tensors.of(r1, r2));
  }

  public void testFailScalar() {
    try {
      LinearInterpolation.of(RealScalar.ONE);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void test1D() {
    Interpolation interpolation = LinearInterpolation.of(Tensors.vector(10, 20, 30, 40));
    StaticHelper.checkMatch(interpolation);
    StaticHelper.checkMatchExact(interpolation);
    StaticHelper.getScalarFail(interpolation);
  }

  public void test2D() {
    Distribution distribution = UniformDistribution.unit();
    Interpolation interpolation = LinearInterpolation.of(RandomVariate.of(distribution, 3, 5));
    StaticHelper.checkMatch(interpolation);
    StaticHelper.checkMatchExact(interpolation);
    StaticHelper.getScalarFail(interpolation);
  }

  public void testFailNull() {
    try {
      LinearInterpolation.of(null);
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
