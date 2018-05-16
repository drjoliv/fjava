package drjoliv.fjava.data;

import drjoliv.fjava.functions.F8;

/**
 * The product of {@code A} x {@code B} x {@code C} x {@code D} x {@code E} x {@code F} x {@code G} x {@code H}.
 * @author Desonte 'drjoliv' Jolivet : drjoliv@gmail.com
 */
public class T8<A,B,C,D,E,F,G,H> {
  public final A _1;
  public final B _2;
  public final C _3;
  public final D _4;
  public final E _5;
  public final F _6;
  public final G _7;
  public final H _8;

  private T8(A _1, B _2, C _3, D _4, E _5, F _6, G _7, H _8) {
    this._1 = _1;
    this._2 = _2;
    this._3 = _3;
    this._4 = _4;
    this._5 = _5;
    this._6 = _6;
    this._7 = _7;
    this._8 = _8;
  }

 /**
   * Creates the product of {@code a} x {@code b} x {@code c} x {@code d} x {@code e} x {@code f}.
   *
   * @param a the first element of the tuple.
   * @param b the second element of this product.
   * @param c the third element of this product.
   * @param d the fourth element of this product.
   * @param e the fifth element of this product.
   * @param e the fifth element of this product.
   * @param f the sixth element of this product.
   * @param g the seventh element of this product.
   * @param h the eighth element of this product.
   * @return the product of {@code a} x {@code b} x {@code c} x {@code d} x {@code e} x {@code f} x {@code g} x {@code h}.
   */
  public static <A,B,C,D,E,F,G,H> T8<A,B,C,D,E,F,G,H> t8(A a, B b, C c, D d, E e, F f, G g, H h) {
    return new T8<>(a, b, c, d, e, f, g, h);
  }

  /**
   * Returns a first class fucntion that creates the 
   * product of {@code A} x {@code A} x {@code C} x {@code D} x {@code E} x {@code F} x {@code  G} x {@code  H}.
   *
   * @return The first class fucntion that creates the product
   * of {@code A} x {@code A} x {@code C} x {@code D} x {@code E} x {@code F} x {@code G} x {@code H}.
   */
  public static <A,B,C,D,E,F,G,H> F8<A,B,C,D,E,F,G,H,T8<A,B,C,D,E,F,G,H>> t8() {
    return T8::t8;
  }

  /**
   * Returns the first element of this product.
   * @return the first element of this product.
   */
  public A _1() {
    return _1;
  }

  /**
   * Returns the second element of this product.
   * @return the second element of this product.
   */
  public B _2() {
    return _2;
  }

  /**
   * Returns the third element of this product.
   * @return the third element of this product.
   */
  public C _3() {
    return _3;
  }

  /**
   * Returns the fourth element of this product.
   * @return the fourth element of this product.
   */
  public D _4() {
    return _4;
  }


  /**
   * Returns the fifth element of this product.
   * @return the fifth element of this product.
   */
  public E _5() {
    return _5;
  }

  /**
   * Returns the sixth element of this product.
   * @return the sixth element of this product.
   */
  public F _6() {
    return _6;
  }

  /**
   * Returns the seventh element of this product.
   * @return the seventh element of this product.
   */
  public G _7() {
    return _7;
  }

  /**
   * Returns the eighth element of this product.
   * @return the eighth element of this product.
   */
  public H _8() {
    return _8;
  }

}
