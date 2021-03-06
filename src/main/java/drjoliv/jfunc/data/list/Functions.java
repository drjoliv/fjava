package drjoliv.jfunc.data.list;
import static drjoliv.jfunc.contorl.eval.Eval.later;
import static drjoliv.jfunc.contorl.tramp.Trampoline.done;
import static drjoliv.jfunc.contorl.tramp.Trampoline.more;
import static drjoliv.jfunc.data.list.FList.flist;
import static drjoliv.jfunc.data.list.FList.flist$;
import static drjoliv.jfunc.data.list.FList.lazy;
import static drjoliv.jfunc.hlist.T2.t2;

import java.util.HashMap;
import java.util.Iterator;

import drjoliv.jfunc.contorl.CaseOf;
import drjoliv.jfunc.contorl.maybe.Maybe;
import drjoliv.jfunc.contorl.tramp.Trampoline;
import drjoliv.jfunc.data.Unit;
import drjoliv.jfunc.data.list.FList.EmptyListException;
import drjoliv.jfunc.data.set.UnbalancedSet;
import drjoliv.jfunc.eq.Ord;
import drjoliv.jfunc.function.F0;
import drjoliv.jfunc.function.F1;
import drjoliv.jfunc.function.F2;
import drjoliv.jfunc.function.P1;
import drjoliv.jfunc.hlist.T2;

public final class Functions {

  public static <A> FList<A> init(FList<A> l) {
    return l.visit(n    -> {throw new EmptyListException();}
               ,(d,t) -> {
                 if(t.size() == 1)
                   return flist(d);
                 else
                   return flist(d, () -> init(t));
               });
  }

  public static <A> F1<FList<A>,FList<A>> init() {
    return Functions::init;
  }

  public static <A> Maybe<A> safeLast(FList<A> l) {
    if(l.isEmpty())
      return Maybe.nothing();
    else {
      FList<A> list = l;
      FList<A> tail = list.tail();
      while(!tail.isEmpty()) {
        list = list.tail();
        tail = list.tail();
      }
      return Maybe.maybe(list.head());
    }
  }

    /**
     * Returns true if <code> p </code> is true for all elements within <code> list </code>.
     *
     * @param list an FList whose elements will be tested with predicate <code> p </code>.
     * @param p a predicate that will test the elements of <code> list </code>.
     * @return True if <code> p </code> is true for all elements within <code> list </code> and falst otherwise.
     */
      public static <A> boolean allTrue(FList<A> list, P1<A> p) {
        return CaseOf.caseOf(list)
          .of(isEmpty(), () -> true)
          .of(headIs(p), l -> allTrue(l.tail(), p))
          .otherwise(() -> false);
    }

  public static  <A> FList<A> append(FList<A> a, FList<A> a1) {
   return a.visit$(n    -> a1
               ,(d,t) -> flist$(d, t.map(f -> f.append(a1))));
  }

  public static  <A> F2<FList<A>,FList<A>,FList<A>> append() {
   return Functions::append;
  }

  public static <A> P1<FList<A>> isEmpty() {
    return l -> l.match(n -> true, c -> false);
  }

  public static <A> P1<FList<A>> headEq(A a) {
    return l -> l.head() == a;
  }

  public static <A> FList<A> nub( Ord<A> order, FList<A> list ) {
    return UnbalancedSet.fromFoldable(list, order).toList();
  }

  public static <A extends Comparable<A>> FList<A> nub( FList<A> list ) {
    return UnbalancedSet.fromFoldable(list, Ord.<A>orderable()).toList();
  }

  public static <A> F2<Ord<A>, FList<A>, FList<A>> nub() {
    return Functions::nub;
  }

  public static <A> P1<FList<A>> headIs(P1<A> p) {
    return l -> p.test(l.head());
  }

  public static <A,B> F1<FList<A>,FList<B>> map(F1<? super A, ? extends B> fn) {
    return l -> l.map(fn);
  }

    /**
     * Takes elements from <code> list </code> while the predicate <code> p </code> is true.
     *
     * @param list the FList elements will be taken from.
     * @param p A predcate that maps over elements of <code> list </code>.
     * @return an FList.
     */
      public static <A> FList<A> takeWhile(FList<A> list, P1<A> p) {
        return CaseOf.caseOf(list)
          .of(isEmpty(), () -> FList.<A>empty())
          .of(headIs(p), l  -> flist(l.head(), () -> takeWhile(l.tail(), p)))
          .otherwise(() -> FList.empty());
    }

  public static <A> A last(FList<A> list) {
    return list.match(
        nil -> {throw new EmptyListException();}
      , cons -> {
        A ret = null;
        for(A a : cons)
          ret = a;
        return ret;
    });
  }

  public static <A> P1<FList<A>> sizeOf(int i) {
    return l -> l.size() == i;
  }

  public static <A> P1<FList<A>> lastEq(A a) {
    return l -> l.last() == a;
  }

  public static <A> P1<FList<A>> lastIs(P1<A> p) {
    return l -> p.test(l.last());
  }

  public static <A> FList<A> intersperse(A a, FList<A> list) {
    return list.visit(
        n     -> FList.empty()
      ,(d,t)  -> flist(d, () -> intersperse$(a, t)));
  }

  private static <A> FList<A> intersperse$(A a, FList<A> list) {
    return list.visit(
        n     -> FList.empty()
      ,(d,t)  -> flist(a,d, () -> intersperse$(a, t)));
  }

    public static <A> FList<FList<A>> window(FList<A> list, int i) {
      return list.isEmpty() || list.size() < i
        ? FList.empty()
        : flist(list.take(i), () -> list.drop(1).window(i));
    }

  public static <A> F2<FList<A>, Integer, FList<FList<A>>> window() {
    return Functions::window;
  }

    /**
     * Returns a new Flist generated by applying the binary function to pairs of elements from the two
     * given FLists.
     *
     * @param fn A binary function that will whose operand types match the given two list.
     * @param l1 The FList used in the first operand postion within the binary function.
     * @param l2 The FList used in the second operand position within the binary function.
     * @return A new Flist generated by applying the binary function to pairs of elements from the two
     * given FLists.
     */
      public static <A,B,C> FList<C> zipWith(F2<A,B,C> fn, FList<A> l1, FList<B> l2) {
     if(l1.isEmpty() ||  l2.isEmpty())
        return FList.empty();
      return flist(fn.call(l1.head(), l2.head()), () -> zipWith(fn, l1.tail(), l2.tail()));
    }

    //public static <A> F2<A,FList<A>,FList<A>> cons() {
    //  return  (a,as) -> as.add(a);
    //}

    public static <A> FList<A> reverse(FList<A> l) {
      return reverse_prime(l, FList.empty()).result();
    }

    private static <A> Trampoline<FList<A>> reverse_prime(FList<A> l, FList<A> acc) {
    if (l.isEmpty())
      return done(acc);
    else {
      return more(() -> reverse_prime(l.tail(), acc.cons(l.head())));
    }
  }

    public static <A,B,C> FList<T2<A,B>> zip(FList<A> l1, FList<B> l2) {
     if(l1.isEmpty() ||  l2.isEmpty())
        return FList.empty();
      return flist(t2(l1.head(), l2.head()), () -> zip(l1.tail(), l2.tail()));
    }

    /**
     *
     *
     * @param list
     * @param w
     * @param p
     * @return
     */
      public static <A> boolean allTrueWhile(FList<A> list, P1<A> w, P1<A> p) {
     if(list.isEmpty())
        return true;
      while(!list.isEmpty() && w.test(list.head())) {
        if(!p.test(list.head()))
          return false;
        list = list.tail();
      }
         return true;
    }

    public static <A> F2<FList<A>,FList<A>,FList<A>> concat() {
      return (a,b) -> a.append(b);
    }

    /**
     * Concats two of the given FLists together.
     *
     * @param l1 The FList that will be used as the beginning of the created FList.
     * @param l2 A supplier that generated the end of the created FList.
     * @return A new FList with l1 as the beginning of theo FList and l2 as the end of the FList.
     */
    public static final <A> FList<A> concat(final FList<A> l1, F0<FList<A>> l2) {
      if(l1.isEmpty())
        return lazy(later(l2));
      else {
        FList<A> tail = l1.tail();
        return flist$(l1.head$(), later(() -> concat(tail,l2)));
      }
    }

  /**
   * Obtains the element of this FList at the given index.
   *
   * @param index The index of this FList to return.
   * @return The element
   */
  public static <A> A get(FList<A> list, int index) {
    if(index < 0) throw new IndexOutOfBoundsException("index can not be lower that zero");
    if(index == 0)
      return list.head();
    else if(list.isEmpty())
      throw new IndexOutOfBoundsException("index can not be lower that zero");
    else {
      FList<A> l = list;
      for(int j = index; j > 0; j--)
        l = l.tail(); 
      return list.head();
    }
  }

  /**
   * Flattens an FList.
   *
   * @param listOfList a FList containing an FList.
   * @return a flattened FList.
   */
  public static final <A> FList<A> flatten(FList<FList<A>> listOfList) {
    if(listOfList.isEmpty())
      return FList.empty();
    else
      return concat(listOfList.head(), () -> flatten(listOfList.tail()));
  }

    @SafeVarargs
  public static final <A> FList<A> flatten(FList<A> ... lists) {
    FList<FList<A>> listOfList = flist(lists);
    return flatten(listOfList);
  }

  public static FList<Unit> guard(boolean bool) {
    return bool ? flist(Unit.unit) : FList.empty();
  }

  public static FList<String> words(String s) {
    return FList.flist(s.split("\\s+"));
  }

  final static F1<String,FList<String>> words() {
    return Functions::words;
  }

    /**
     * Creates a FList from an Iterator.
     *
     * @param it an iterator from which elements will be drawed from to create the FList.
     * @return a FList.
     */
    public static final <A> FList<A> fromIterable(final Iterator<A> it){
     if(it.hasNext())
       return flist(it.next(), () ->  fromIterable(it));
     return FList.empty();
   }

   public static final <A> FList<A> repeat(int i, A a) {
     if(i == 0)
       return FList.empty();
     else
       return flist(a, () -> repeat(i - 1, a));
   }

  /**
   * Returns a frequency map. 
   * @param list a list of elements to be counted.
   * @return a frequency hash map.
   */
    public static <A> HashMap<A, Integer> frequency ( FList<A> list ) {
      return frequency(new HashMap<>(), list).result();
    }

  /**
   * Returns a frequency map, mutating the given map by adding values to it.
   * @param map key-values to be added to.
   * @param list elements to be counted.
   * @return a frequency map.
   */
    public static <A> HashMap<A, Integer> frequencyWithMap ( HashMap<A, Integer> map
        , FList<A> list ) {

      return frequency(map, list).result();

    }

    public static <A> FList<A> random(F0<A> generator, int i) {
      return CaseOf.caseOf(i)
        .of(CaseOf.eq(0), () -> FList.<A>empty())
        .otherwise(()        -> flist(generator.call(), () -> random(generator, i - 1)));
    }

  /**
   * Returns a frequency map, mutating the given map by adding values to it.
   * @param map key-values to be added to.
   * @param list elements to be counted.
   * @return a trampoline containing a frequency map.
   */
    public static <A> Trampoline<HashMap<A, Integer>> frequency ( HashMap<A, Integer> map
        , FList<A> list ) {

      if(list.isEmpty())
        return Trampoline.done(map);
      else
        return Trampoline.more(() -> {
          A a = list.head();
          Integer i = map.getOrDefault(a , 0);
          map.put(a, i + 1);
         return frequency(map, list.tail());
        });
    }
}
