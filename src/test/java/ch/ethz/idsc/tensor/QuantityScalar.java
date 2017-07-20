// code by jph
package ch.ethz.idsc.tensor;

import java.util.Objects;

import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.ArcTanInterface;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.ChopInterface;
import ch.ethz.idsc.tensor.sca.ComplexEmbedding;
import ch.ethz.idsc.tensor.sca.Conjugate;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Imag;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.NInterface;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.PowerInterface;
import ch.ethz.idsc.tensor.sca.Real;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.RoundingInterface;
import ch.ethz.idsc.tensor.sca.SignInterface;
import ch.ethz.idsc.tensor.sca.Sqrt;
import ch.ethz.idsc.tensor.sca.SqrtInterface;

/** the class is intended for testing and demonstration
 * 
 * implementation is consistent with Mathematica:
 * NumberQ[Quantity[3, "Meters"]] == False
 * ExactNumberQ[Quantity[3, "Meters"]] == False
 * MachineNumberQ[Quantity[3.123, "Meters"]] == False */
public class QuantityScalar extends AbstractScalar implements //
    ArcTanInterface, ChopInterface, ComplexEmbedding, NInterface, //
    PowerInterface, RoundingInterface, SignInterface, SqrtInterface, Comparable<Scalar> {
  private static final Scalar HALF = RationalScalar.of(1, 2);

  /** @param value
   * @param unit
   * @param exponent
   * @return */
  public static Scalar of(Scalar value, String unit, Scalar exponent) {
    return of(value, new UnitMap(unit, exponent));
  }

  public static Scalar of(Scalar value, UnitMap unitMap) {
    return unitMap.isEmpty() ? value : new QuantityScalar(value, unitMap);
  }

  private final Scalar value;
  private final UnitMap unitMap;

  private QuantityScalar(Scalar value, UnitMap unitMap) {
    this.value = value;
    this.unitMap = unitMap;
  }

  @Override
  public Scalar negate() {
    return of(value.negate(), unitMap);
  }

  @Override
  public Scalar invert() {
    return of(value.invert(), unitMap.negate());
  }

  @Override
  public Scalar abs() {
    return of(value.abs(), unitMap);
  }

  @Override
  public Number number() {
    return value.number();
  }

  @Override
  public Scalar zero() {
    return of(value.zero(), unitMap);
  }

  @Override
  protected Scalar plus(Scalar scalar) {
    if (Scalars.isZero(this) && Scalars.nonZero(scalar))
      return scalar; // 0[m] + X(X!=0) gives X(X!=0)
    if (Scalars.nonZero(this) && Scalars.isZero(scalar))
      return this; // X(X!=0) + 0[m] gives X(X!=0)
    if (scalar instanceof QuantityScalar) {
      QuantityScalar quantityScalar = (QuantityScalar) scalar;
      if (Scalars.isZero(this) && Scalars.isZero(scalar)) {
        // explicit addition of zeros to ensure symmetry
        // for instance when numeric precision is different:
        // 0[m] + 0.0[m] == 0.0[m]
        // 0[m] + 0.0[s] == 0.0
        final Scalar zero = value.add(quantityScalar.value);
        if (unitMap.equals(quantityScalar.unitMap))
          return of(zero, unitMap); // 0[m] + 0[m] gives 0[m]
        return zero; // 0[m] + 0[s] gives 0
      }
      if (unitMap.equals(quantityScalar.unitMap))
        return of(value.add(quantityScalar.value), unitMap);
    } else { // scalar is not an instance of QuantityScalar
      if (Scalars.isZero(this) && Scalars.isZero(scalar))
        // return of value.add(scalar) is not required for symmetry
        // precision of this.value prevails over given scalar
        return this; // 0[kg] + 0 gives 0[kg]
    }
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override
  public Scalar multiply(Scalar scalar) {
    if (scalar instanceof QuantityScalar) {
      QuantityScalar quantityScalar = (QuantityScalar) scalar;
      return of(value.multiply(quantityScalar.value), unitMap.add(quantityScalar.unitMap));
    }
    return of(value.multiply(scalar), unitMap);
  }

  @Override // from PowerInterface
  public Scalar power(Scalar exponent) {
    // Mathematica allows 2[m]^3[s], but the tensor library does not:
    if (exponent instanceof QuantityScalar)
      throw TensorRuntimeException.of(this, exponent);
    return of(Power.of(value, exponent), unitMap.multiply(exponent));
  }

  @Override // from ArcTanInterface
  public Scalar arcTan(Scalar x) {
    if (x instanceof QuantityScalar) {
      QuantityScalar quantityScalar = (QuantityScalar) x;
      if (unitMap.equals(quantityScalar.unitMap))
        return ArcTan.of(quantityScalar.value, value);
    }
    throw TensorRuntimeException.of(x, this);
  }

  @Override // from SqrtInterface
  public Scalar sqrt() {
    return of(Sqrt.of(value), unitMap.multiply(HALF));
  }

  @Override // from RoundingInterface
  public Scalar ceiling() {
    return of(Ceiling.of(value), unitMap);
  }

  @Override // from ChopInterface
  public Scalar chop(Chop chop) {
    return of(chop.apply(value), unitMap);
  }

  @Override // from ComplexEmbedding
  public Scalar conjugate() {
    return of(Conjugate.of(value), unitMap);
  }

  @Override // from RoundingInterface
  public Scalar floor() {
    return of(Floor.of(value), unitMap);
  }

  @Override // from ComplexEmbedding
  public Scalar imag() {
    return of(Imag.of(value), unitMap);
  }

  @Override // from NInterface
  public Scalar n() {
    return of(N.of(value), unitMap);
  }

  @Override // from SignInterface
  public int signInt() {
    RealScalar realScalar = (RealScalar) value;
    return realScalar.signInt();
  }

  @Override // from ComplexEmbedding
  public Scalar real() {
    return of(Real.of(value), unitMap);
  }

  @Override // from RoundingInterface
  public Scalar round() {
    return of(Round.of(value), unitMap);
  }

  @Override
  public int compareTo(Scalar scalar) {
    if (scalar instanceof QuantityScalar) {
      QuantityScalar quantityScalar = (QuantityScalar) scalar;
      if (unitMap.equals(quantityScalar.unitMap))
        return Scalars.compare(value, quantityScalar.value);
    }
    throw TensorRuntimeException.of(this, scalar);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, unitMap);
  }

  @Override
  public boolean equals(Object object) {
    if (object instanceof QuantityScalar) {
      QuantityScalar quantityScalar = (QuantityScalar) object;
      return value.equals(quantityScalar.value) && //
          unitMap.equals(quantityScalar.unitMap);
    }
    if (object instanceof Scalar) {
      // the implementation of plus(...) uses the convention
      // that 0[kg] == 0 evaluates to true
      Scalar scalar = (Scalar) object;
      return Scalars.isZero(this) && Scalars.isZero(scalar); // 0[kg] == 0
    }
    return false;
  }

  @Override
  public String toString() {
    return value + "[" + unitMap + "]";
  }
}
