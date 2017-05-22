// code by jph
package ch.ethz.idsc.tensor.mat;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Eigensystem.html">Eigensystem</a> */
public interface Eigensystem extends Serializable {
  /** @param matrix symmetric and real valued
   * @return */
  static Eigensystem ofSymmetric(Tensor matrix) {
    if (!SymmetricMatrixQ.of(matrix))
      throw TensorRuntimeException.of(matrix);
    return new JacobiMethod(matrix);
  }

  /** @return vector of eigenvalues corresponding to the eigenvectors */
  Tensor values();

  /** @return matrix with rows as eigenvectors of given matrix
   * The eigenvectors are not necessarily scaled to unit length. */
  Tensor vectors();
}
