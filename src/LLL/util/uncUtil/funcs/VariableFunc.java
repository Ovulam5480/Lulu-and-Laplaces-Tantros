package LLL.util.uncUtil.funcs;

@FunctionalInterface
public interface VariableFunc<T, R>{
  @SuppressWarnings("unchecked")
  R apply(T... args);
}
