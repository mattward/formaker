package uk.wardm.formaker.generator.pojo;

import uk.wardm.formaker.annotation.Exclude;
import uk.wardm.formaker.model.Component;

import java.lang.reflect.Field;
import java.util.Optional;

public interface ComponentFactory {
    /**
     * Given a field on a POJO, creates a form field object that
     * represents it. If the field is excluded (e.g. by having
     * an {@link Exclude @Exclude} annotation
     * attached, then the {@link Optional} returned will be empty.
     *
     * @param field Field on a POJO
     * @return Form Component, if applicable
     */
    Optional<Component> createFromField(Field field);
}
