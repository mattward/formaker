package uk.wardm.formaker.model;

import uk.wardm.formaker.model.input.InputField;

import java.util.Collection;

public interface FormMeta {
    Collection<InputField> getFields();

    Collection<String> getFieldNames();

    InputField getField(String name);
}
