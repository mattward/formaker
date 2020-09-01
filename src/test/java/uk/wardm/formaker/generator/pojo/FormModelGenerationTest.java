package uk.wardm.formaker.generator.pojo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.wardm.formaker.annotation.Password;
import uk.wardm.formaker.annotation.Range;
import uk.wardm.formaker.model.Form;
import uk.wardm.formaker.model.InputField;
import uk.wardm.formaker.model.NumberField;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FormModelGenerationTest {
    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void formModelContainsCorrectFieldNames() {
        Form formDef = new FormModelImpl(new FormDefinition1());

        // Field names are as expected
        assertIterableEquals(
                Arrays.asList("stringField", "integerField", "localDateField"),
                formDef.getFieldNames());
    }

    @Test
    void formModelContainsFieldObjectsWithCorrectNames() {
        Form formDef = new FormModelImpl(new FormDefinition1());

        assertIterableEquals(formDef.getFieldNames(),
                formDef.getFields().stream().
                        map(InputField::getName).
                        collect(Collectors.toList()));
    }

    @Test
    public void canGetFormFieldObjectByName() {
        Form formDef = new FormModelImpl(new FormDefinition1());

        NumberField field = (NumberField) formDef.getField("integerField");
        assertAll(
                () -> assertNotNull(field, "Field ref must not be null"),
                () -> assertEquals("integerField", field.getName()),
                () -> assertEquals(
                        "uk.wardm.formaker.generator.pojo.FormModelGenerationTest$FormDefinition1.integerField",
                        field.getLabel())
        );
    }

    @Test
    public void canCreateNumberFieldModel() {
        Form formDef = new FormModelImpl(new FormDefinitionWithAnnotations());

        final NumberField field = (NumberField) formDef.getField("firstIntField");
        assertAll(
                () -> assertNull(field.getMin()),
                () -> assertEquals(200, field.getMax()),
                () -> assertFalse(field.isUseSlider())
        );

        final NumberField invalidRangeField = (NumberField) formDef.getField("secondIntField");
        assertAll(
                () -> assertNull(invalidRangeField.getMin()),
                () -> assertNull(invalidRangeField.getMax()),
                // A slider won't be used without both min and max
                () -> assertFalse(invalidRangeField.isUseSlider())
        );

        final NumberField validRangeField = (NumberField) formDef.getField("thirdIntField");
        assertAll(
                () -> assertEquals(10, validRangeField.getMin()),
                () -> assertEquals(100, validRangeField.getMax()),
                // A slider (@Range) cam be used with min and max both set
                () -> assertTrue(validRangeField.isUseSlider())
        );
    }

    private class FormDefinition1 {
        private String stringField;
        private Integer integerField;
        private LocalDate localDateField;
        private Thread unsupportedFieldType;
    }

    private class FormDefinitionWithAnnotations {
        private String firstStringField;

        @Password
        private String secondStringField;

        @Max(200)
        private int firstIntField;

        @Range
        private int secondIntField;

        @Range @Min(10) @Max(100)
        private int thirdIntField;
    }
}