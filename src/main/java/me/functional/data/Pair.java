package me.functional.data;

public class Pair<A,B> {
  public final A a;
  public final B b;

  private Pair(A a, B b) {
    this.a = a;
    this.b = b;
  }

  public static <A,B> Pair<A,B> of(A a, B b) {
    return new Pair<A,B>(a,b);
  }

  public String toString() {
    return "<" + a.toString() + "," + b.toString() + ">";
  }
}
