package uk.wardm.formaker.transformer.thymeleaf;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;


/**
 * <p>Basic visual/theming configuration for the {@link BootstrapComponentRenderer}</p>
 *
 * <p>For example:</p>
 *
 * <pre><code>
 *     BootstrapConfig config = new BootstrapConfig();
 *     config.getTextInputAttrs().put("class", "form-control form-control-sm");
 *     config.getSelectAttrs().put("class", "custom-select custom-select-sm");
 *     config.getTextBoxAttrs().put("rows", "5");
 *     return new FormakerDialect(new BootstrapComponentRenderer(false, config));
 * </code></pre>
 */
public class BootstrapConfig {
    @Getter Map<String, String> formGroupAttrs = new HashMap<>(singletonMap("class", "form-group"));
    @Getter Map<String, String> fieldSetAttrs = new HashMap<>(singletonMap("class", "form-group"));
    @Getter Map<String, String> fieldErrorsAttrs = new HashMap<>(singletonMap("class", "error small"));
    @Getter Map<String, String> textBoxAttrs = new HashMap<>(singletonMap("class", "form-control"));
    @Getter Map<String, String> textInputAttrs = new HashMap<>(singletonMap("class", "form-control"));
    @Getter Map<String, String> rangeControlAttrs = new HashMap<>(singletonMap("class", "custom-range"));
    @Getter Map<String, String> selectAttrs = new HashMap<>(singletonMap("class", "custom-select"));
    @Getter Map<String, String> radioWrapperAttrs = new HashMap<>(singletonMap("class", "custom-control custom-radio"));
    @Getter Map<String, String> radioAttrs = new HashMap<>(singletonMap("class", "custom-control-input"));
    @Getter Map<String, String> radioLabelAttrs = new HashMap<>(singletonMap("class", "custom-control-label"));
}
