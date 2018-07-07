package drjoliv.jfunc.nums;

import static drjoliv.jfunc.collection.FList.*;

import java.math.BigInteger;
import java.util.Comparator;

import drjoliv.jfunc.collection.FList;
import drjoliv.jfunc.function.F1;

public class Numbers {

  public static boolean isPrime(Long i) {
   final int sqrtOfi = (int)Math.sqrt(i); 
   return allTrueWhile(primes, p -> p <= sqrtOfi
       , p -> i % p != 0);
  }

  public static <A> A min(Comparator<A> comparator, A a1, A a2) {
    int x = comparator.compare(a1, a2);
      if(x == -1)
        return a1;
      else
        return a2;
  }

  public static <A> A max(Comparator<A> comparator, A a1, A a2) {
    int x = comparator.compare(a1, a2);
      if(x == 1)
        return a1;
      else
        return a2;
  }

  public static FList<Long> primeFactors(Long i) {
    if(i == 0)
      return FList.empty();
    else if(i == 1)
      return FList.empty();
    else {
      return primeFactors$(i, primes);
    }
  }

  private static FList<Long> primeFactors$(Long i, FList<Long> list) {
    Long x = list.head();
    if(i == x) {
      return flist(i);
    } else if (x * x > i) {
      return flist(i);
    } else if (i % x == 0) {
      return flist(x , () -> primeFactors$( i / x , list.tail()));
    } else {
      return primeFactors$(i, list.tail());
    }
  }

  public static FList<Long> primes = flist(2L, 3L, () -> start(4L).filter(Numbers::isPrime));

  public static FList<Long> fibonacci = sequence(1L, 1L, (l1, l2) -> l1 + l2);

  public static FList<Long> start(Long i) {
    return flist(i, () -> start(i + 1));
  }

  public static FList<Integer> start(Integer i) {
    return flist(i, () -> start(i + 1));
  }

  public static boolean even(long i) {
    return i % 2 == 0;
  }

  public static boolean odd(long i) {
    return !even(i);
  }

  public static F1<Long,Long> increment(Long l) {
    return ll -> ll + l;
  }

  public static F1<Integer,Integer> increment(Integer i) {
    return ii -> ii + i;
  }

  public static F1<Long,Long> decrement(Long l) {
    return ll -> ll - l;
  }

  public static F1<Integer,Integer> decrement(Integer i) {
    return ii -> ii - i;
  }

  public static F1<Long,Long> multiply(Long l) {
    return ll -> ll * l;
  }

  public static F1<Integer,Integer> multiply(Integer i) {
    return ii -> ii * i;
  }

  public static FList<Long>range(Long from, Long to, F1<Long,Long> fn ) {
    if(from == to)
      return flist(from);
    else if(from > to)
      return dec(from, to, fn);
    else
      return inc(from, to, fn);
  }

  public static FList<Integer>range(Integer from, Integer to, F1<Integer,Integer> fn ) {
    if(from == to)
      return flist(from);
    else if(from > to)
      return dec(from, to, fn);
    else
      return inc(from, to, fn);
  }

  public static FList<Integer> naturals() {
    return start(1);
  }

  public static FList<Integer> triangles() {
      return triangle_prime(1,2,1);
  }

  private static FList<Integer> triangle_prime(int n, int next, int sum) {
    return flist(n, () -> triangle_prime(next + sum, next + 1, next + sum));
  }


  private static FList<Long> inc(Long from, Long to, F1<Long,Long> fn) {
    if(from > to)
      return FList.empty();
    else
      return flist(from, () -> inc(fn.call(from), to, fn));
  }

  private static FList<Long> dec(Long from, Long to, F1<Long,Long> fn) {
    if(from < to)
      return FList.empty();
    else
      return flist(from, () -> dec(fn.call(from), to, fn));
  }

  private static FList<Integer> inc(Integer from, Integer to, F1<Integer,Integer> fn) {
    if(from > to)
      return FList.empty();
    else
      return flist(from, () -> inc(fn.call(from), to, fn));
  }

  private static FList<Integer> dec(Integer from, Integer to, F1<Integer,Integer> fn) {
    if(from < to)
      return FList.empty();
    else
      return flist(from, () -> dec(fn.call(from), to, fn));
  }





  public static FList<Integer> range(Integer from, Integer to) {
    if(from > to)
      return FList.empty();
    else
      return flist(from, () -> range(from + 1, to));
  }

   public static FList<Long> range(Long from, Long to) {
    if(from > to)
      return FList.empty();
    else
      return flist(from, () -> range(from + 1, to));
  }
 
   public static FList<BigInteger> range(BigInteger from, BigInteger to) {
    if(from.compareTo(to) == -1)
      return FList.empty();
    else
      return flist(from, () -> range(from.add(BigInteger.ONE), to));
  }

  public static Long sum(FList<Long> flist) {
    return flist.reduce(0L, (i1,i2) -> i1 + i2);
  }

  public static Integer add(Integer i1, Integer i2) {
    return i1 + i2;
  }

  public static Long add(Long l1, Long l2) {
    return l1 + l2;
  }

  public static Double add(Double d1, Double d2) {
    return d1 + d2;
  }
}
