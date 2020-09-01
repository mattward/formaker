package uk.wardm.formaker.model;

import java.util.Collection;

public interface Form {
    Collection<InputField> getFields();

    Collection<String> getFieldNames();

    InputField getField(String name);
}
