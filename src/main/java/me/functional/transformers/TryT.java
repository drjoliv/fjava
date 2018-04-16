package me.functional.transformers;

import static me.functional.data.Try.*;

import java.util.function.Function;
import java.util.function.Supplier;

import me.functional.data.Either;
import me.functional.data.Try;
import me.functional.functions.F0;
import me.functional.functions.F1;
import me.functional.hkt.Hkt;
import me.functional.hkt.Hkt2;
import me.functional.hkt.Witness;
import me.functional.type.Bind;
import me.functional.type.BindUnit;

/**
 *
 *
 * @author
 */
public class TryT<M extends Witness,A> implements Bind<Hkt<TryT.μ,M>, A>, Hkt2<TryT.μ,M,A> {

  public static class μ implements Witness{}

  private final F0<Bind<M,Try<A>>> runTryT;

  private final BindUnit<M> mUnit;

  private TryT(Try<A> t, BindUnit<M> mUnit) {
    this.runTryT = () -> mUnit.unit(t);
    this.mUnit   = mUnit;
  }

  private TryT(Bind<M,Try<A>> t) {
    this.runTryT = () -> t;
    this.mUnit   = t.yield();
  }

  private TryT(F0<Bind<M,Try<A>>> runTryT, BindUnit<M> mUnit) {
    this.runTryT = runTryT;
    this.mUnit   = mUnit;
  }

  @Override
  public <B> TryT<M,B> fmap(F1<? super A, B> fn) {
    return new TryT<>(() -> runTryT.call().fmap(t -> t.fmap(fn)), mUnit); 
  }

  @Override
  public <B> TryT<M,B> mBind(F1<? super A, ? extends Bind<Hkt<μ, M>, B>> fn) {
    return new TryT<M,B>(() -> {
     Bind<M,Try<B>> m = runTryT.call().mBind(t -> {
       Either<Exception, F0<Bind<M,Try<B>>>> e =
         t.fmap(fn.then(TryT::asTryT))
         .run()
         .right()
         .fmap(tryT -> tryT.runTryT)
         .either();
       if(e.isRight())
         return e.valueR().call();
       else
         return mUnit.unit(failure(e.valueL()));
     });
        return m; 
     },mUnit);
  }

  @Override
  public <B> TryT<M,B> semi(Bind<Hkt<μ, M>, B> mb) {
    return mBind(a -> mb);
  }

  @Override
  public BindUnit<Hkt<μ, M>> yield() {
    return new BindUnit<Hkt<μ, M>>() {
      @Override
      public <B> Bind<Hkt<μ, M>, B> unit(B b) {
        return new TryT<M,B>(mUnit.unit(success(b)));
      }
    };
  }

  /**
  *
  *
  * @return
  */
  public Bind<M,Try<A>> runTryT() {
    return runTryT.call();
  }

  /**
   *
   *
   * @param b
   * @param mUnit
   * @return
   */
  public static <M extends Witness, B> TryT<M,B> tryT(B b, BindUnit<M> mUnit) {
    return new TryT<>(success(b), mUnit);
  }

  /**
   *
   *
   * @param t
   * @return
   */
  public static <M extends Witness, B> TryT<M,B> tryT(Bind<M,Try<B>> t) {
    return new TryT<>(t);
  }

  /**
   *
   *
   * @param t
   * @return
   */
  public static <M extends Witness, B> TryT<M,B> lift(Bind<M,B> t) {
    return new TryT<M,B>(t.fmap(b -> success(b)));
  }

  /**
   *
   *
   * @param monad
   * @return
   */
  public static <M extends Witness, B> TryT<M, B> asTryT(Bind<Hkt<μ, M>, B> monad) {
    return (TryT<M, B>) monad;
  }
}