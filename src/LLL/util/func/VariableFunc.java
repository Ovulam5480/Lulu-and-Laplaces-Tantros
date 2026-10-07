package LLL.util.func;

@FunctionalInterface
public interface VariableFunc<T, R>{
  @SuppressWarnings("unchecked")
  R apply(T... args);
}
