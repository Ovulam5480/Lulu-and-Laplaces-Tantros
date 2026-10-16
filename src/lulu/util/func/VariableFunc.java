package lulu.util.func;

@FunctionalInterface
public interface VariableFunc<T, R>{
  @SuppressWarnings("unchecked")
  R apply(T... args);
}
