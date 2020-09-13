package uk.wardm.formaker.annotation;

import uk.wardm.formaker.model.ChoiceStyle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
//@ComponentType can only have one of these on a field?
public @interface Select {
    String[] options() default {};
    ChoiceStyle style() default ChoiceStyle.SELECT;
}
