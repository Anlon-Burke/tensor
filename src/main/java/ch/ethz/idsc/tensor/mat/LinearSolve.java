// code by jph
package ch.ethz.idsc.tensor.mat;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Unprotect;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Partition;
import ch.ethz.idsc.tensor.alg.TensorRank;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LinearSolve.html">LinearSolve</a> */
public enum LinearSolve {
  ;
  /** gives solution to linear system of equations.
   * scalar entries are required to implement
   * Comparable<Scalar> for pivoting.
   * 
   * @param m square matrix of {@link Scalar}s that implement absolute value abs()
   * @param b tensor with first dimension identical to size of matrix
   * @return x with m.dot(x) == b
   * @throws TensorRuntimeException if matrix m is singular */
  public static Tensor of(Tensor m, Tensor b) {
    return new GaussianElimination(m, b, Pivot.argMaxAbs).solution();
  }

  /** method only checks for non-zero
   * and doesn't use Scalar::abs().
   * 
   * @param m square matrix
   * @param b tensor with first dimension identical to size of matrix
   * @return x with m.dot(x) == b
   * @throws TensorRuntimeException if matrix m is singular */
  public static Tensor withoutAbs(Tensor m, Tensor b) { // function name is not final
    return new GaussianElimination(m, b, Pivot.firstNonZero).solution();
  }

  /** function for m not necessarily invertible
   * 
   * @param m
   * @param b vector
   * @return x with m.x == b
   * @throws TensorRuntimeException if such an x does not exist */
  public static Tensor any(Tensor m, Tensor b) {
    if (StaticHelper.anyMachineNumberQ(m))
      throw TensorRuntimeException.of("use LinearSolve::of for matrices with numeric precision", m, b);
    switch (TensorRank.of(b)) {
    case 1:
      return _vector(m, b);
    default:
      break;
    }
    throw TensorRuntimeException.of(m, b);
  }

  private static Tensor _vector(Tensor m, Tensor b) {
    int cols = Unprotect.length0(m);
    Tensor r = RowReduce.of(Join.of(1, m, Partition.of(b, 1)));
    Tensor x = Array.zeros(cols);
    int j = 0;
    int c0 = 0;
    while (c0 < cols) {
      if (Scalars.nonZero(r.Get(j, c0))) {
        x.set(r.Get(j, cols), c0);
        ++j;
      }
      ++c0;
    }
    for (; j < m.length(); ++j)
      if (Scalars.nonZero(r.Get(j, cols)))
        throw TensorRuntimeException.of(m, b);
    return x;
  }
}
