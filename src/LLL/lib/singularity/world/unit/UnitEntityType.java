package LLL.lib.singularity.world.unit;

import mindustry.gen.*;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnitEntityType{
  Class<? extends Unit> value();
}
