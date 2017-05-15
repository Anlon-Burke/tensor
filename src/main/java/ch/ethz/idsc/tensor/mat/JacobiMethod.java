// code by guedelmi
package ch.ethz.idsc.tensor.mat;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ZeroScalar;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.Hypot;

/** The Jacobi transformations of a real symmetric matrix establishes the
 * diagonal matrix D
 * 
 * D == V* . A . V,
 * 
 * where the matrix V,
 * 
 * V == P1 * P2 * P3 * ...,
 * 
 * is the product of the successive Jacobi rotation matrices Pi. The diagonal
 * entries of D are the eigenvalues of A and the columns of V are the
 * eigenvectors of A. */
class JacobiMethod implements Eigensystem {
  private static final int MAXITERATIONS = 50;
  private static final Scalar HALF = RationalScalar.of(1, 2);
  private static final Scalar HUNDRED = RealScalar.of(100);
  private static final Scalar EPS = RealScalar.of(Math.ulp(1));
  // ---
  private final int n;
  private final Tensor V;
  private Tensor d;

  JacobiMethod(Tensor matrix) {
    Tensor A = matrix.copy();
    n = Dimensions.of(A).get(0);
    V = IdentityMatrix.of(n);
    Tensor z = Array.zeros(n);
    Tensor b = Tensors.vector(i -> matrix.get(i, i), n);
    d = b.copy();
    Scalar factor = RealScalar.of(0.2).divide(RealScalar.of(n * n));
    for (int i = 0; i < MAXITERATIONS; ++i) {
      Scalar sum = IntStream.range(0, n - 1).boxed() //
          .flatMap(ip -> A.get(ip).extract(ip + 1, n).flatten(0)) //
          .map(Scalar.class::cast).map(Scalar::abs).reduce(Scalar::add) //
          .orElse(ZeroScalar.get());
      if (sum instanceof ZeroScalar) {
        eigsrt();
        return;
      }
      Scalar tresh = (i < 4) ? sum.multiply(factor) : ZeroScalar.get();
      for (int ip = 0; ip < n - 1; ++ip) {
        for (int iq = ip + 1; iq < n; ++iq) {
          Scalar g = HUNDRED.multiply(A.Get(ip, iq).abs());
          if (i > 4 && Scalars.lessEquals(g, EPS.multiply(d.Get(ip).abs())) && Scalars.lessEquals(g, EPS.multiply(d.Get(ip).abs()))) {
            A.set(ZeroScalar.get(), ip, iq);
          } else if (!Scalars.lessEquals(A.Get(ip, iq).abs(), tresh)) {
            Scalar h = d.Get(iq).subtract(d.Get(ip));
            Scalar t;
            if (Scalars.lessEquals(g, EPS.multiply(h.abs()))) {
              t = A.Get(ip, iq).divide(h);
            } else {
              Scalar theta = HALF.multiply(h).divide(A.Get(ip, iq));
              t = theta.abs().add(Hypot.bifunction.apply(theta, RealScalar.ONE)).invert();
              if (theta.number().doubleValue() < 0)
                t = t.negate();
            }
            Scalar c = Hypot.bifunction.apply(t, RealScalar.ONE).Get().invert();
            Scalar s = t.multiply(c);
            Scalar tau = s.divide(c.add(RealScalar.ONE));
            final Scalar fh = t.multiply(A.Get(ip, iq));
            z.set(v -> v.subtract(fh), ip);
            z.set(v -> v.add(fh), iq);
            d.set(v -> v.subtract(fh), ip);
            d.set(v -> v.add(fh), iq);
            A.set(ZeroScalar.get(), ip, iq);
            final int fip = ip;
            final int fiq = iq;
            IntStream.range(0, ip).parallel() //
                .forEach(j -> rotate(A, s, tau, j, fip, j, fiq));
            IntStream.range(ip + 1, iq).parallel() //
                .forEach(j -> rotate(A, s, tau, fip, j, j, fiq));
            IntStream.range(iq + 1, n).parallel() //
                .forEach(j -> rotate(A, s, tau, fip, j, fiq, j));
            IntStream.range(0, n).parallel() //
                .forEach(j -> rotate(V, s, tau, fip, j, fiq, j));
          }
        }
      }
      b = b.add(z);
      z = Array.zeros(n);
      d = b.copy();
    }
    throw TensorRuntimeException.of(A);
  }

  private static void rotate(Tensor A, Scalar s, Scalar tau, int i, int j, int k, int l) {
    Scalar g = A.Get(i, j);
    Scalar h = A.Get(k, l);
    A.set(g.subtract(s.multiply(h.add(g.multiply(tau)))), i, j);
    A.set(h.add(s.multiply(g.subtract(h.multiply(tau)))), k, l);
  }

  private void eigsrt() {
    for (int i = 0; i < n - 1; ++i) {
      final int k = i + ArgMax.of(d.extract(i, n));
      if (k != i) {
        Scalar max = d.Get(i);
        d.set(d.get(k), i);
        d.set(max, k);
        Tensor vgi = V.get(i);
        V.set(V.get(k), i);
        V.set(vgi, k);
      }
    }
  }

  @Override
  public Tensor vectors() {
    return V;
  }

  @Override
  public Tensor values() {
    return d;
  }
}