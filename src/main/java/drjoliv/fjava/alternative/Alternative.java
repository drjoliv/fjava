package drjoliv.fjava.alternative;

import drjoliv.fjava.applicative.Applicative;
import drjoliv.fjava.hkt.Witness;

/**
 * A monoid for applicative functors.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public interface Alternative<M extends Witness, A, Fa extends Applicative<M,A>> {

  /**
   * Returns the identity of this applicative.
   * @return the identity of this applicative.
   */
  public  Fa empty();

  /**
   * An associative binary function.
   * @param a2 an applicative functor.
   * @param a2 an applicative functor.
   * @return an applicative functor.
   */
  public Fa alt( Fa a1, Fa a2);
}