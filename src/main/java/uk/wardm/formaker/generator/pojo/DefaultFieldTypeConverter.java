package uk.wardm.formaker.generator.pojo;

import uk.wardm.formaker.model.DateField;
import uk.wardm.formaker.model.InputField;
import uk.wardm.formaker.model.NumberField;
import uk.wardm.formaker.model.TextField;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Maps the supported Java types to their abstract nominal
 * form equivalent.
 * <p>
 * Note that this provides the default {@link InputField} Type for each
 * class, but some may map to different types given more details, such
 * as an annotation on the java field.
 */
public abstract class DefaultFieldTypeConverter {
    private static final Map<Class<?>, Class<? extends InputField>> map = new HashMap<>();
    static {
        map.put(String.class, TextField.class);
        map.put(LocalDate.class, DateField.class);
        map.put(int.class, NumberField.class);
        map.put(Integer.class, NumberField.class);
        map.put(short.class, NumberField.class);
        map.put(Short.class, NumberField.class);
        map.put(long.class, NumberField.class);
        map.put(Long.class, NumberField.class);
    }

    public static Set<Class<?>> supportedTypes() {
        return map.keySet();
    }

    public static Class<? extends InputField> formTypeFor(Class<?> javaType) {
        if (map.containsKey(javaType)) {
            return map.get(javaType);
        }
        else {
            throw new IllegalArgumentException("Unsupported type: " + javaType.getName());
        }
    }

    public static boolean supports(Class<?> type) {
        return supportedTypes().contains(type);
    }
}
