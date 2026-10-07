package LLL.util.uncUtil.funcs;

@FunctionalInterface
public interface VariableCons<T>{
  @SuppressWarnings("unchecked")
  void apply(T... args);
}
