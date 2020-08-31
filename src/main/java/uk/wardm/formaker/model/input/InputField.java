package uk.wardm.formaker.model.input;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.wardm.formaker.model.Component;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class InputField extends Component {
    private final String name;

    // i18n message property
    private final String label;
}
