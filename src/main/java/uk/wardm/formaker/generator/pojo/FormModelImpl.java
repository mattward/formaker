package uk.wardm.formaker.generator.pojo;

import uk.wardm.formaker.model.Form;
import uk.wardm.formaker.model.InputField;

import java.lang.reflect.Field;
import java.util.*;

/**
 * A {@link Form} (form object model) that describes
 * a form based on a POJO class form definition.
 */
public class FormModelImpl implements Form {
    private final Map<String, InputField> fields = new LinkedHashMap<>();
    private final ComponentFactory componentFactory = new ComponentFactoryImpl();

    public FormModelImpl(Object target) {
        initFields(target.getClass());
    }

    @Override
    public Collection<InputField> getFields() {
        return fields.values();
    }

    @Override
    public Collection<String> getFieldNames() {
        return fields.keySet();
    }

    @Override
    public InputField getField(String name) {
        if (fields.containsKey(name)) {
            return fields.get(name);
        }
        else {
            throw new IllegalArgumentException("No field with name " + name + " exists");
        }
    }

    private void initFields(Class formClass) {
        for (Field field : formClass.getDeclaredFields()) {
            if (DefaultFieldTypeConverter.supports(field.getType())) {
                componentFactory.createFromField(field).ifPresent(component -> {
                    InputField inputField = (InputField) component;
                    fields.put(inputField.getName(), inputField);
                });
            }
        }
    }
}
