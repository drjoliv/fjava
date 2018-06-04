package drjoliv.fjava.data;

import static drjoliv.fjava.data.T2.t2;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import drjoliv.fjava.control.Bind;
import drjoliv.fjava.control.BindUnit;
import drjoliv.fjava.control.bind.Eval;

import drjoliv.fjava.control.bind.Trampoline;
import static drjoliv.fjava.control.bind.Trampoline.*;
import static drjoliv.fjava.control.bind.Eval.*;
import drjoliv.fjava.functions.F0;
import drjoliv.fjava.functions.F1;
import drjoliv.fjava.functions.F2;
import drjoliv.fjava.functions.F3;
import drjoliv.fjava.hkt.Hkt;

/**
 * 
 *
 * @author Desonte 'drjoliv' Jolivet eamil:drjoliv@gmail.com
 */
public abstract class FList<A> implements Hkt<FList.μ,A>, Bind<FList.μ,A>, Iterable<A> {

  /**
  * The witness type of FList.
  */
  public static class μ implements drjoliv.fjava.hkt.Witness{}

    public static BindUnit<FList.μ> unit = new BindUnit<FList.μ>() {
      @Override
      public <A> Bind<μ, A> unit(A a) {
        return FList.single(a);
      }
    };

  @Override
  public abstract <B> FList<B> map(F1<? super A, B> fn);

  public abstract FList<FList<A>> suffixes();


  @Override
  public Iterator<A> iterator() {
    final FList<A> self = this;
    return new Iterator<A>() {

      private FList<A> list = self;

      @Override
      public boolean hasNext() {
        return !list.isEmpty();
      }

      @Override
      public A next() {
        A a = list.head();
        list = list.tail();
        return a;
      }
    };
  }



  public abstract FList<A> update(int i, A a);

  @Override
  public <B> FList<B> bind(F1<? super A, ? extends Bind<FList.μ, B>> fn) {
     if(isEmpty()) {
      return Nil.instance();
    } else {
      return FList.flatten((FList<FList<B>>)map(fn));
    }
  }

  public <B> B foldr(F2<B,A,B> f2, B b) {
    return foldr_prime(f2, b, this).result();
  }

  private static <A,B> Trampoline<B> foldr_prime(F2<B,A,B> f2, B b, FList<A> list) {
    return list.isEmpty()
      ? done(b)
      : more(() -> foldr_prime(f2 , f2.call(b,list.head()), list.tail()));
  }

  public T2<FList<A>, FList<A>> split() {
    int i = size();
    return t2(take(i/2),drop(i/2));
  }

  public T2<FList<A>, FList<A>> splitAt(int i) {
    return t2(take(i),drop(i));
  }

  
  //public T2<FList<A>, FList<A>> splitWith(P1) {
  //  return t2(take(i),drop(i));
  //}

  public final FList<A> reverse() {
    return FList.reverse(this);
  } 

  @Override
  public <B> FList<B> semi(Bind<μ, B> mb) {
    return bind(a -> mb);
  }

  @Override
  public BindUnit<μ> yield() {
    return FList::single;
  }

  private FList(){}

  public final FList<FList<A>> window(int i) {
    return isEmpty()
      ? FList.empty()
      : flist(take(i), () -> take(i).window(i));
  }

  /**
   *
   *
   * @param predicate
   * @return
   */
  public FList<A> takeWhile(Predicate<A> predicate) {
    Objects.requireNonNull(predicate);
    return FList.takeWhile(this,predicate);
  }

  /**
   * Appends a value to this FList.
   *
   * @param a An element to append to this FLiist.
   * @return A new FList with the given value appended to it.
   */
  public FList<A> concat(A a) {
    return concat(flist(a));
  }

  /**
   * Appends the given FList to this FList.
   *
   * @param a The FList that will be appended to this FList.
   * @return A new FList with the given FList appended to it.
   */
  public abstract FList<A> concat(FList<A> a);

  /**
   * Creates a new FList in which the given element is the head of the new FList.
   * <pre>
   * {@code
   * FList<Integer> list = of(2,3);
   * print(list.addd(1));
   * //prints
   * // [1, 2, 3]
   * }
   * </pre>
   * @param a The element that will be prepend to the head of this list.
   * @return a new FList in which the given element is at the head.
   */
  public final FList<A> add(A a) {
    return new Cons<>(a, now(this));
  }

  /**
   * Return a new FList with the given FList appended to it.
    * <pre>
   * {@code
   * FList<Integer> list = of(2,3);
   * FList<Integer> list2 = of(-1,0);
   * print(list.addd(list2));
   * //prints
   * // [-1, 0, 2, 3]
   * }
   * </pre>
   * @param a The FList that will be appended the end of this FList.
   * @return A new FList with the given FList appended to it.
   */
  public final FList<A> add(FList<A> a) {
    return FList.concat(a, () -> this);  
  }


  /**
   * Returns the element at the given index. This method is unsafe and will return null if
   * the given index is not within this FList.
   * <pre>
   * {@code
   * FList<String> list = of("apple","banna");
   * String a = list.unsafeGet(0);
   * System.out.println(a);
   * // prints
   * // "apple"
   * }
   * </pre>
  *
   * @param index The index of the element to retrieve.
   * @return The element at the given index.
   */
  public A unsafeGet(int index) {
    if(index < 0) throw new IllegalArgumentException("index can not be lower that zero");
    if(index == 0)
      return head();
    else if(isEmpty())
      return null;
    else {
      FList<A> list = this;
      for(int j = index; j > 0; j--)
        list = list.tail(); 
      return list.head();
    }
  }

  /**
   * Obtains the element of this FList at the given index.
   *
   * @param index The index of this FList to return.
   * @return The element
   */
  public final Maybe<A> get(int index) {
    if(index < 0) throw new IllegalArgumentException("index can not be lower that zero");
    if(index == 0)
      return safeHead();
    else if(isEmpty())
      return Maybe.nothing();
    else {
      FList<A> list = this;
      for(int j = index; j > 0; j--)
        list = list.tail(); 
      return list.safeHead();
    }
  }

  /**
   * Removes <code> i </code> elements from this, returning an FList with those
   * elements droped.
   *
   * @param i The number of elements to drop from this FList.
   * @return an FList where <code> i </code> elements have been droped.
   */
  public FList<A> drop(int i) {
    if(i < 0)
      return take(size() - Math.abs(i)); 
    else if(i == 0)
      return this;
    else if(isEmpty())
      return this;
    else {
      FList<A> list = this;
      for(int j = i; j > 0; j--)
        list = list.tail(); 
      return list;
    }
  }

  /**
   * Returns the last value within this FList. This is unsafe because it may
   * return null if this list is empty.
   *<pre>
   * {@code
   * Integer a = of(2,3).unsafeLast();
   * system.out.println(a);
   * //prints
   * // 3
   *
   * Integer b = Flist.empty().unsafeLast();
   * System.out.println(b);
   * //prints
   * // null
   * }
   * </pre>
   * @return The last value within this FList. 
   */
  public A unsafeLast() {
    if(isEmpty())
      return null;
    else {
      FList<A> list = this;
      FList<A> tail = list.tail();
      while(!tail.isEmpty()) {
        list = list.tail();
        tail = list.tail();
      }
      return list.head();
    }
  }

  /**
   * Returns the last value within this FList, the value returned
   * is wrapped within a maybe and if this FList is empty the maybe will be 
   * emtpy.
   * <pre>
   * {@code
   * Maybe<Integer> a = of(2,3).last();
   * system.out.println(a);
   * //Maybe<3>
   *
   * Maybe<Integer> b = Flist.empty().last();
   * System.out.println(b);
   * //prints
   * // Nothing
   * }
   * </pre>
   * @return The last value within this FList. 
   */

  public Maybe<A> last() {
    if(isEmpty())
      return Maybe.nothing();
    else {
      FList<A> list = this;
      FList<A> tail = list.tail();
      while(!tail.isEmpty()) {
        list = list.tail();
        tail = list.tail();
      }
      return list.safeHead();
    }
  }

  /**
   * Return the Higher Kinded Type of FList
   *
   * @return The Higher Kinded version of this FList.
   */
  public final Hkt<FList.μ, A> widen() {
    return (Hkt<FList.μ, A>) this;
  }

  @Override
  public final String toString() {
    return FList.toString(this);
  }

  /**
   * Removes all elements within the FList that evaluate to true when applied to the predicate.
   * <pre>
   * {@code
   *  FList<Integer> oneToTweenty = of(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20);
   *  String s = oneToTweenty.filter(i -> i % 2 == 0)
   *    .toString(); 
   *
   *  print(s); // [1, 3, 5, 7, 9, 11, 13, 15, 17, 19 ]
   *
   * // Finding sum of all natural number below 1000 that are divisible by 3 or 5.
   *
   *  FList<Integer> list = range(1,999)
   *    .filter(i -> i % 3 == 0 || i % 5 == 0);
   *
   *  System.out.println(list.reduce(0,(i, i2) -> i + i2)); //
   * }
   * </pre>
   * @param p the predicated used to filter this flist.
   * @return a new FList in which elements have been filtered out.
   */
  public final FList<A> filter(F1<A,Boolean> p) {
     if(isEmpty())
      return Nil.instance();
    else if(p.call(head()))
      return flist(head(), () -> tail().filter(p));
    else
      return FList.concat(Nil.instance(), () -> tail().filter(p));
  }

  /**
   * Returns a new FList containing the elements of this FList up to element at the <code>i</code> index.
   *
   * @param i The index up to where the elements will be taken.
   * @return A new FList containing the elements of this FList up to element at the ith index.
   */
  public final FList<A> take(int i) {
    if(i < 0)
      return reverse().take(Math.abs(i));
    if(isEmpty() || i == 0) {
      return Nil.instance();
    } else {
      return flist(head(), () -> tail().take(i-1));
    }
  }

  /**
   * Returns a value represented the combined values of this list.
   *
   * @param a The initial vaule used when reducing this list, if this list is emtry <code> a </code> is returned.
   * @param reducer a combining function that combines all the elements within this FList in a single value.
   * @return A value 
   */
  public final A reduce(final A a, final F2<A,A,A> reducer) {
    A reducedValue = a;
    Iterator<A> it = iterator();
    while(it.hasNext()) {
      reducedValue = reducer.call(reducedValue,it.next());
    }
    return reducedValue;
  }

  public final A reduce(Monoid<A> monoid) {
    return reduce(monoid.mempty(), monoid::mappend);
  }

  public final Maybe<A> reduce(final F2<A,A,A> reducer) {
    if(size() < 2) {
      return Maybe.nothing();
    }
    else {
      Iterator<A> it = iterator();
      A reducedValue = it.next();
      while(it.hasNext()) {
        reducedValue = reducer.call(reducedValue,it.next());
      }
      return Maybe.maybe(reducedValue);
    }
  }

  /**
   * Returns the number of elements within this FList.
   *
   * @return The number of elements within this FList.
   */
    public int size() {
      FList<A> f = this;
      int i = 0;
      while(f.isEmpty() == false) {
        i++;
        f = f.tail();
      }
      return i;
    }

  /**
   * Returns a Flist with the head of this FList dropped.
   *
   * @return A FList with the head of this FList dropped.
   */
  public abstract FList<A> tail();

  /**
   * Returns the element at the head of this FList as a Maybe.
   * This function should be used instae of unsafeHead, because it ensures that
   * a null pointer exception is not possible.
   *
   * @return Returns the element at the head of this FList as a Maybe.
   */
  public abstract Maybe<A> safeHead();

  /**
   * Returns the element at the head of this FList. This function is not safe
   * because it will return null if this FList is empty.
   *
   * @return The element at the head of this FList.
   */
  public abstract A head();

  /**
   * Returns true if this FList has zero elements, or false otherwise.
   *
   * @return True if this FList has zero elements, or false otherwise.
   */
  public abstract boolean isEmpty();

  private static class Cons<A> extends FList<A> {

    private final A datum; //The data withing this FList


    //A supplier that return the next FList in this LinkedList like structure
    private volatile Eval<FList<A>> tail;

    private Cons(A datum, Eval<FList<A>> tail) {
      this.datum = datum;
      this.tail = tail;
    }

  /**
   * Appends the given FList to this FList.
   *
   * @param a The FList that will be appended to this FList.
   * @return A new FList with the given FList appended to it.
   */
  @Override
  public FList<A> concat(FList<A> a) {
    return new Cons<>(datum, tail.map(f -> f.concat(a)));
  }

    @Override
    public FList<A> tail() {
      return tail.value();
    }

    @Override
    public Maybe<A> safeHead() {
      return Maybe.maybe(datum);
    }


    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public A head() {
      return datum;
    }

    @Override
    public <B> FList<B> map(F1<? super A, B> fn) {
      return new Cons<B>(fn.call(head()), tail.map(l -> l.map(fn)));
    }

    @Override
    public FList<A> update(int i, A a) {
      return i == 0
      ? tail().add(a)
      : new Cons<>(head(), tail.map(t -> t.update(i - 1, a)));
    }

    @Override
    public FList<FList<A>> suffixes() {
      return new Cons<>(this, tail.map(t -> t.suffixes()));
    }

  }

  private static class Nil<A> extends FList<A> {

    private static Nil<?> instance = new Nil();

    private Nil(){}

    @SuppressWarnings("unchecked")
    private static <B> Nil<B> instance() {
      return (Nil<B>)instance;
    }

  @Override
  public FList<A> concat(FList<A> a) {
    return a;
  }

    @Override
    public FList<A> tail() {
      return instance();
    }

    @Override
    public Maybe<A> safeHead() {
      return Maybe.nothing();
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public A head() {
      return null;
    }

    @Override
    public <B> FList<B> map(F1<? super A, B> fn) {
      return Nil.empty();
    }
  
    @Override
    public FList<A> update(int i, A a) {
      throw new IndexOutOfBoundsException();
    }

    @Override
    public FList<FList<A>> suffixes() {
      return flist(empty());
    }
  }

  /**
   * Creates an empty FList.
   *
   * @return An empty FList.
   */
  public static <A> FList<A> empty() {
    return Nil.instance();
  }

  public static <A> FList<A> single(A a) {
    return flist(a);
  }



  /**
   * Creats a new FList from an array of elements.
   *
   * @param elements The elements that will be turned into a FList.
   * @return A new FList.
   */
  @SafeVarargs
  public static <B> FList<B> flist(B... elements) {
    Objects.requireNonNull(elements);
    FList<B> list = Nil.instance();
    for (int i = elements.length - 1; i >= 0; i--) {
      list = list.add(elements[i]);
    }
    return list;
  }

  @SuppressWarnings("unchecked")
  public static <B> FList<B> fromCollection(Collection<B> collection) {
    Objects.requireNonNull(collection);
    return flist((B[]) collection.toArray());
  }

  /**
   * Creates a new FList.
   *
   * @param b The element to placed at the head of this FList.
   * @param supplier A supplier that is used to generated the tail of this FList.
   * @return A new FList.
   */
  public static <B> FList<B> flist(B b, F0<FList<B>> supplier) {
    return new Cons<B>(b, later(supplier));
  }

  /**
   * Creates a new FList.
   *
   * @param b The element placed at the head of the created FList.
   * @param b1 The second element placed within the created FList.
   * @param supplier A supplier that is used to generate the tail of the created FList.
   * @return A new FList.
   */
  public static <B> FList<B> flist(B b, B b1, F0<FList<B>> supplier) {
   return flist(b, () -> flist(b1, supplier));
  }

  /**
   * Creates a new FList.
   *
   * @param b The element placed at the head of this created FList.
   * @param b1 The second element placed within the created FList.
   * @param b2 The third element placed within the created FList.
   * @param supplier A supplier that is used to generate the tail of the created FList.
   * @return a new FList.
   */
  public static <B> FList<B> flist(B b, B b1, B b2, F0<FList<B>> supplier) {
   return flist(b, () -> flist(b1, b2, supplier));
  }

  /**
   * Creates a new FList.
   *
   * @param b The element placed at the head of this created FList.
   * @param b1 The second element placed within the created FList.
   * @param b2 The third element placed within the created FList.
   * @param b3 The fourth element placed within the crated FList.
   * @param supplier A supplier that is used to generate the tail of the created FList.
   * @return a new FList.
   */
  public static <B> FList<B> flist(B b, B b1, B b2, B b3, F0<FList<B>> supplier) {
   return flist(b, () -> flist(b1, b2, b3, supplier));
  }

  /**
   * Converts a FList to a String.
   *
   * @param flist An FList that will be converted to an String.
   * @return The string representaion of <code> flist </code>.
   */
  public static <A> String toString(FList<A> flist) {
    Objects.requireNonNull(flist);
    StringBuilder builder = new StringBuilder(); 
    builder.append("[ ");
    Consumer<A> stringfy = a -> builder.append(a.toString()).append(" ");
    Iterator<A> it = flist.iterator();
    while(it.hasNext())
      stringfy.accept(it.next());
    builder.append("]");
    return builder.toString();
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

  /**
   * Flattens an FList.
   *
   * @param listOfList a FList containing an FList.
   * @return a flattened FList.
   */
  public static final <A> FList<A> flatten(FList<FList<A>> listOfList) {
    if(listOfList.isEmpty())
      return Nil.instance();
    else
      return concat(listOfList.head(), () -> flatten(listOfList.tail()));
  }

    @SafeVarargs
    public static final <A> FList<A> flatten(FList<A> ... listOfList) {
    FList<FList<A>> listOfListPrime = flist(listOfList);
    return flatten(listOfListPrime);
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
        return l2.call();
      else {
        FList<A> tail = l1.tail();
        return new Cons<>(l1.head(), later(() -> concat(tail,l2)));
      }
    }

    /**
     * Takes elements from <code> list </code> while the predicate <code> p </code> is true.
     *
     * @param list the FList elements will be taken from.
     * @param p A predcate that maps over elements of <code> list </code>.
     * @return an FList.
     */
      public static <A> FList<A> takeWhile(FList<A> list, Predicate<A> p) {
        Objects.requireNonNull(list);
        Objects.requireNonNull(p);
     if(list.isEmpty())
        return Nil.instance(); 
      else if (p.test(list.head()))
        return flist(list.head(), () -> takeWhile(list.tail(), p));
      else
        return Nil.instance();
    }

    /**
     * Returns true if <code> p </code> is true for all elements within <code> list </code>.
     *
     * @param list an FList whose elements will be tested with predicate <code> p </code>.
     * @param p a predicate that will test the elements of <code> list </code>.
     * @return True if <code> p </code> is true for all elements within <code> list </code> and falst otherwise.
     */
      public static <A> boolean allTrue(FList<A> list, Predicate<A> p) {
            Objects.requireNonNull(list);
        Objects.requireNonNull(p);
 if(list.isEmpty())
        return true;
      else if(p.test(list.head()))
        return allTrue(list.tail(), p);
      else
        return false;
    }

    public static boolean and(FList<Boolean> list) {
      F2<Boolean, Boolean, Boolean> fn = Boolean::logicalAnd;
      return list.foldr(fn,Boolean.TRUE).booleanValue();
    }

    public static boolean or(FList<Boolean> list) {
      F2<Boolean, Boolean, Boolean> fn = Boolean::logicalOr;
      return list.foldr(fn,Boolean.FALSE).booleanValue();
    }

    /**
     *
     *
     * @param list
     * @param w
     * @param p
     * @return
     */
      public static <A> boolean allTrueWhile(FList<A> list, Predicate<A> w, Predicate<A> p) {
             Objects.requireNonNull(list);
        Objects.requireNonNull(w);
        Objects.requireNonNull(p);
     if(list.isEmpty())
        return true;
      while(!list.isEmpty() && w.test(list.head())) {
        if(!p.test(list.head()))
          return false;
        list = list.tail();
      }
         return true;
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
              Objects.requireNonNull(fn);
        Objects.requireNonNull(l1);
        Objects.requireNonNull(l2);
     if(l1.isEmpty() ||  l2.isEmpty())
        return Nil.instance();
      return flist(fn.call(l1.head(), l2.head()), () -> zipWith(fn, l1.tail(), l2.tail()));
    }

    public static <A,B,C> FList<T2<A,B>> zip(FList<A> l1, FList<B> l2) {
        Objects.requireNonNull(l1);
        Objects.requireNonNull(l2);
     if(l1.isEmpty() ||  l2.isEmpty())
        return Nil.instance();
      return flist(t2(l1.head(), l2.head()), () -> zip(l1.tail(), l2.tail()));
    }

    /**
    * Tests if two FLists are equal.
    *
    * @param l1 an FList.
    * @param l2 an FList.
    * @return True if <code> l1 </code> and <code> l2 </code> or equal and false otherwise.
    */
    public static <A> boolean equals(FList<A> l1, FList<A> l2) {
        Objects.requireNonNull(l1);
        Objects.requireNonNull(l2);
     if(l1.isEmpty() != l2.isEmpty()) {
        return false;
      } else if(l1.isEmpty()) {
        return true;
      } else if(l1.head() == l2.head()){
        return equals(l1.tail(), l2.tail());
      } else {
        return false;
      }
    }

    /**
      * Creates a sequence from one seed element and a Function, <code>sequence</code> is useful when the previous elements in a
     * sequence dicate later elements within the sequence.
     *
     * @param seed The first value to be created within the generated sequence.
     * @param generator A function that use the previous value within the FList to create the next value.
     * @return A infinite FList.
     */
    public static <A> FList<A> sequence(A seed, F1<A,A> generator) {
       Objects.requireNonNull(seed);
        Objects.requireNonNull(generator);
     return flist(seed , () -> sequence(generator.call(seed), generator));
    }

    /**
     * Creates a sequence from two seed elements and a BiFunction, <code>sequence</code> is useful when the previous elements in a
     * sequence dicate later elements within the sequence.
     *
     * @param seed First element within a sequence.
     * @param seed1 Second element within a sequence.
     * @param generator A BiFunction that generates elements within the sequence using the previous two elements within the sequence.
     * @return a infinte FList.
     */
      public static <A> FList<A> sequence(A seed, A seed1, F2<A,A,A> generator) {
      Objects.requireNonNull(seed);
        Objects.requireNonNull(seed1);
        Objects.requireNonNull(generator);
        A seed2 = generator.call(seed,seed1);
        A seed3 = generator.call(seed1,seed2);
        return flist(seed , seed1, () -> sequence(seed2, seed3, generator));
    }

    /**
    *
    * Creates a sequence from three seed elements and a TriFuction, <code>sequence</code> is useful when the previous elements in a
    * sequence dicate later elements within the sequence.
    *
    * @param seed First element within a sequence.
    * @param seed1 Second element within a sequence.
    * @param seed2 Third element within a sequence.
    * @param generator A TriFunction that generates the next element within the sequnce using the previous three elements within the sequence.
    * @return an infinte FList.
    */
    public static <A> FList<A> sequence(A seed, A seed1, A seed2, F3<A,A,A,A> generator) {
      A seed3 = generator.call(seed,seed1,seed2);
      A seed4 = generator.call(seed1,seed2,seed3);
      A seed5 = generator.call(seed2,seed3,seed4);
      return flist(seed, seed1, seed2, () -> sequence(seed3, seed4, seed5, generator));
    }


    public static <M extends drjoliv.fjava.hkt.Witness,A> Maybe<Bind<M,FList<A>>> merge(FList<Bind<M,FList<A>>> listOfMonadsOfFList) {
      return listOfMonadsOfFList.reduce((m1,m2) -> Bind.liftM2(m1,m2, (l1,l2) -> l1.concat(l2)));
    }

    /**
    *
    *
    * @param wider
    * @return
    */
    public static <B> FList<B> asFList(Bind<FList.μ,B> wider) {
      return (FList<B>) wider;
    }

    /**
    
    *
    * @param list
    */
    public static <A> void print(FList<A> list) {
      System.out.println(list.toString());
    }

    //private static <A> FList<A> reverse(FList<A> list) {
    // if(list.isEmpty())
    //    return Nil.instance();
    // else
    //  return flist(list.unsafeLast(),  () -> reverse(list.tail()));
    //}

    private static <A> FList<A> reverse(FList<A> l) {
      return reverse_prime(l, FList.empty()).result();
    }

    private static <A> Trampoline<FList<A>> reverse_prime(FList<A> l, FList<A> acc) {
      if(l.isEmpty())
        return done(acc);
      else {
        return more(() -> reverse_prime(l.tail(), acc.add(l.head())));
      }
    }

  public static class instances {
    public static <A> Monoid<FList<A>> monoidInstance() {
      return new Monoid<FList<A>>(){

        @Override
        public FList<A> mappend(FList<A> w1, FList<A> w2) {
          return w1.concat(w2);
        }

        @Override
        public FList<A> mempty() {
          return FList.empty();
        }
      };
    }
  }
}