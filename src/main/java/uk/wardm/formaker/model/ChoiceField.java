package uk.wardm.formaker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public class ChoiceField extends InputField {
    @Getter
    private List<Option> options;

    public ChoiceField(String name, String label, List<Option> options) {
        super(name, label);
        this.options = options;
    }

    @Data
    @AllArgsConstructor
    public static class Option {
        private String labelKey;
        private Object value;
    }
}
