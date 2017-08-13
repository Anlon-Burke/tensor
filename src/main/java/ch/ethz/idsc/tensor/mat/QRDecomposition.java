// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.MatrixQ;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/QRDecomposition.html">QRDecomposition</a> */
public interface QRDecomposition extends Serializable {
  /** @param matrix
   * @return qr-decomposition of matrix */
  static QRDecomposition of(Tensor matrix) {
    if (!MatrixQ.of(matrix))
      throw TensorRuntimeException.of(matrix);
    return new HouseholderQRDecomposition(matrix);
  }

  /** @return orthogonal matrix */
  Tensor getInverseQ();

  /** @return upper triangular matrix */
  Tensor getR();

  /** @return orthogonal matrix */
  Tensor getQ();

  /** @return determinant of matrix */
  @Deprecated
  Scalar det();
}
