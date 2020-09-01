package uk.wardm.formaker.generator.pojo;

import uk.wardm.formaker.annotation.Exclude;
import uk.wardm.formaker.annotation.Password;
import uk.wardm.formaker.annotation.Range;
import uk.wardm.formaker.annotation.TextBox;
import uk.wardm.formaker.model.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ComponentFactoryImpl implements ComponentFactory {
    /**
     * Create the correct type of component given a field on a class.
     *
     * @param field Must be supported according to DefaultFieldTypeConverter.supports(field.getType())
     * @return Component
     */
    @Override
    public Optional<Component> createFromField(Field field) {
        Class<?> formClass = field.getDeclaringClass();

        String label = String.join(".", new String[] { formClass.getName(), field.getName() });

        /*
         * Certain types of type will support certain annotations, for example both a NUMBER and a TEXT
         * field can support a @Password annotation to use a masked input field.
         *
         * Both a NUMBER and a RANGE field support Min and Max - though range requires both.
         */

        Class<? extends InputField> formType = DefaultFieldTypeConverter.formTypeFor(field.getType());

        if (field.isAnnotationPresent(Exclude.class)) {
            return Optional.empty();
        }
        if (field.isAnnotationPresent(Password.class)) {
            // TODO: check formType is valid for @Password annotation.
            return Optional.of(new PasswordField(field.getName(), label));
        }
        else if (field.isAnnotationPresent(TextBox.class)) {
            return Optional.of(new TextBoxField(field.getName(), label));
        }
        else if (formType.equals(NumberField.class)) {
            Long min = null;
            if (field.isAnnotationPresent(Min.class)) {
                min = field.getAnnotation(Min.class).value();
            }
            Long max = null;
            if (field.isAnnotationPresent(Max.class)) {
                max = field.getAnnotation(Max.class).value();
            }
            NumberField numberField = new NumberField(field.getName(), label, min, max);
            if (field.isAnnotationPresent(Range.class) && min != null && max != null) {
                numberField.setUseSlider(true);
            }
            return Optional.of(numberField);
        }
        else {
            try {
                Constructor<? extends InputField> ctor = formType.getConstructor(String.class, String.class);
//                System.out.println("Creating " + formType.getName() + " for field " + field.getName());
                return Optional.of(ctor.newInstance(field.getName(), label));
            }
            catch (NoSuchMethodException e) {
                throw new IllegalStateException("No valid constructor for " + formType.getName());
            }
            catch (IllegalAccessException|InstantiationException|InvocationTargetException e) {
                throw new RuntimeException("Unable to create " + formType.getName());
            }
        }
    }
}
