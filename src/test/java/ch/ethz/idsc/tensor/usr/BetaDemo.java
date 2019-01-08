// code by jph
package ch.ethz.idsc.tensor.usr;

import ch.ethz.idsc.tensor.ComplexScalar;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Parallelize;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Arg;
import ch.ethz.idsc.tensor.sca.Beta;

/** inspired by Mathematica's documentation of Beta */
enum BetaDemo {
  ;
  private static final int RES = StaticHelper.GALLERY_RES;
  private static final int DEPTH = 2;
  private static final Tensor RE = Subdivide.of(-2, +2, RES - 1);
  private static final Tensor IM = Subdivide.of(-2, +2, RES - 1);

  private static Scalar function(int y, int x) {
    Scalar seed = ComplexScalar.of(RE.Get(x), IM.Get(y));
    try {
      return Arg.of(Nest.of(z -> Beta.of(z, z), seed, DEPTH));
    } catch (Exception exception) {
      // System.out.println("fail=" + seed);
    }
    return DoubleScalar.INDETERMINATE;
  }

  public static void main(String[] args) throws Exception {
    Tensor matrix = Parallelize.matrix(BetaDemo::function, RES, RES);
    Export.of(HomeDirectory.Pictures(BetaDemo.class.getSimpleName() + ".png"), //
        ArrayPlot.of(matrix, ColorDataGradients.HUE));
  }
}
