// code by jph
package ch.ethz.idsc.tensor.mat;

import java.util.List;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/LowerTriangularize.html">LowerTriangularize</a> */
public enum LowerTriangularize {
  ;
  /** retains the entries on the diagonal and below
   * 
   * @param matrix
   * @return */
  public static Tensor of(Tensor matrix) {
    return of(matrix, 0);
  }

  /** Example:
   * LowerTriangularize.of(matrix, -1)
   * retains the entries strictly lower than the diagonal
   * 
   * @param matrix
   * @param k
   * @return */
  public static Tensor of(Tensor matrix, int k) {
    List<Integer> dims = Dimensions.of(matrix);
    return Tensors.matrix((i, j) -> j - i <= k ? matrix.get(i, j) : RealScalar.ZERO, dims.get(0), dims.get(1));
  }
}
