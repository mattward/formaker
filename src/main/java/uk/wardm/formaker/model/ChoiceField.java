package uk.wardm.formaker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;

public class ChoiceField extends InputField {
    @Getter
    private List<Option> options;

    @Getter
    private ChoiceStyle style;

    public ChoiceField(String name, String label, List<Option> options, ChoiceStyle style) {
        super(name, label);
        this.options = options;
        this.style = style;
    }

    @Data
    @AllArgsConstructor
    public static class Option {
        private String labelKey;
        private Object value;
    }
}
