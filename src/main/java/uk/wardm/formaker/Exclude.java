package uk.wardm.formaker;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Excludes a field from being part of the form. For example, use on an @Id field
 * on a hibernate entity.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Exclude {
}
