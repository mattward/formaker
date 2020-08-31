package uk.wardm.formaker.model.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


public class NumberField extends InputField {
    @Getter
    private Long min;

    @Getter
    private Long max;

    @Getter
    @Setter
    private boolean useSlider = false;

    public NumberField(String name, String label, Long min, Long max) {
        super(name, label);
        this.min = min;
        this.max = max;
    }
}
