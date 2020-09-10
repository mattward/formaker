package uk.wardm.formaker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
//@ComponentType can only have one of these on a field?
public @interface Select {
    String[] value() default {};
}
