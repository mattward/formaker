package uk.wardm.formaker.generator.pojo;

import uk.wardm.formaker.annotation.*;
import uk.wardm.formaker.model.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        else if (field.isAnnotationPresent(Select.class)) {
            Select select = field.getAnnotation(Select.class);
            List<ChoiceField.Option> options = Arrays.stream(select.options()).
                    map(value -> new ChoiceField.Option(value, value)).
                    collect(Collectors.toList());
            return Optional.of(new ChoiceField(field.getName(), label, options, select.style()));
        }
        else if (formType.equals(ChoiceField.class)) {
            List<ChoiceField.Option> options = new ArrayList<>();
            if (field.getType().equals(Boolean.class) || field.getType().equals(boolean.class)) {
                options.add(new ChoiceField.Option("true", "true"));
                options.add(new ChoiceField.Option("false", "false"));
            }
            else if (field.getType().isEnum()) {
                options = Arrays.stream(field.getType().getEnumConstants()).
                        map(value -> new ChoiceField.Option(value.toString(), value)).
                        collect(Collectors.toList());
            }
            return Optional.of(new ChoiceField(field.getName(), label, options, ChoiceStyle.SELECT));
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
