// code by jph
package ch.ethz.idsc.tensor.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import ch.ethz.idsc.tensor.Tensor;

/** import tensor expression that was created by {@link Put}.
 * The format is similar to Tensor::toString and readable in any text editor.
 * 
 * <p>example content
 * <pre>
 * {{2 + 9*I, 3 - I, 3 - 2.423*I},
 * {(23 + I) / 4, 3.1415}, {}}
 * </pre>
 * 
 * <p>files generated by Tensor::Put, and Mathematica::Put
 * are suitable for import using Get.
 * 
 * <p>the format does not specify or require any particular
 * file extension. Mathematica also does not define an extension
 * for this format.
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Get.html">Get</a> */
public enum Get {
  ;
  /** @param file source
   * @return
   * @throws IOException */
  public static Tensor of(File file) throws IOException {
    return of(file.toPath());
  }

  /** @param path source
   * @return
   * @throws IOException */
  public static Tensor of(Path path) throws IOException {
    return MathematicaFormat.parse(Files.lines(path));
  }
}
