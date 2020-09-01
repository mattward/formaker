package uk.wardm.formaker.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a String field as being intended for passwords.
 *
 * This would usually result in the contents being masked in the UI by default.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {
}
