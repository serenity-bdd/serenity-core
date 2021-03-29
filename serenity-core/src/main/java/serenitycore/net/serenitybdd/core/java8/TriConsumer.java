package serenitycore.net.serenitybdd.core.java8;

import java.util.Objects;

@FunctionalInterface
public interface TriConsumer<T, U, V> {
  public void accept(T t, U u, V v);

  public default TriConsumer<T, U, V> andThen(TriConsumer<? super T, ? super U, ? super V> after) {
    Objects.requireNonNull(after);
    return (a, b, c) -> {
      accept(a, b, c);
      after.accept(a, b, c);
    };
  }
}