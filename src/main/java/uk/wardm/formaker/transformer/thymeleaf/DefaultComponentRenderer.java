package uk.wardm.formaker.transformer.thymeleaf;

import javassist.ClassMap;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.unbescape.html.HtmlEscape;
import uk.wardm.formaker.model.*;
import uk.wardm.formaker.model.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultComponentRenderer implements ComponentRenderer {
    private boolean renderRendererWarnings = true;


    @Override
    public IModel render(ITemplateContext context, Component component) {
        IModelFactory modelFactory = context.getModelFactory();
        if (component instanceof InputField) {
            IModel model = modelFactory.createModel();

            InputField inputField = (InputField) component;

            model.add(modelFactory.createOpenElementTag("div", "class", "form-group"));

            String id = inputField.getName();
            model.add(modelFactory.createOpenElementTag("label", "for", HtmlEscape.escapeHtml5(id)));


            String labelText = resolveMessage(context, inputField.getLabel(), inputField.getName());
            model.add(modelFactory.createText(HtmlEscape.escapeHtml5(labelText)));
            model.add(modelFactory.createCloseElementTag("label"));

            String fqPlaceholderKey = inputField.getLabel() + ".placeholder";
            String placeholderText = resolveMessage(context, fqPlaceholderKey, "");

            if (inputField instanceof TextBoxField) {
                renderTextBox(((TextBoxField) inputField), placeholderText, modelFactory, model);
            }
            else if (inputField instanceof ChoiceField) {
                // placeholder not needed, but need to resolve text options
                renderChoiceField((ChoiceField) inputField, placeholderText, modelFactory, model);
            }
            else {
                renderTextField(inputField, placeholderText, modelFactory, model);
            }

            Map<String, String> errorAttrs = new LinkedHashMap<>();
            errorAttrs.put("class", "error small");
            errorAttrs.put("th:errors", "*{" + inputField.getName() + "}");
            model.add(modelFactory.createOpenElementTag("div", errorAttrs, AttributeValueQuotes.DOUBLE, false));
            model.add(modelFactory.createCloseElementTag("div"));

            model.add(modelFactory.createCloseElementTag("div")); // form-group
            return model;
        }
        else {
            IModel model = modelFactory.createModel();

            if (renderRendererWarnings) {
                model.add(modelFactory.createOpenElementTag("p"));
                model.add(modelFactory.createText("Unable to render field for " + component.getClass()));
                model.add(modelFactory.createCloseElementTag("p"));
            }
            return model;
        }
    }

    // Resolve the message - may return null if no matching property.
    private String resolveMessage(ITemplateContext context, String fqKey, String defaultValue) {
        // TODO: cache by fqKey
        final Object[] emptyParams = new Object[0];
        final String prefix = "fm.";

        String[] keyParts = fqKey.split("\\.");

        // Match on fully key, if possible
        String value = context.getMessage(this.getClass(), fqKey, emptyParams, false);
        if (value != null) {
            return value;
        }

        // Match increasing parts of message key, starting with last part,
        // e.g. for a.b.c.d match on d, then c.d, etc.
        for (int i = keyParts.length - 1; i > 0; i--) {
            String key = String.join(".", Arrays.copyOfRange(keyParts, i, keyParts.length));
            value = context.getMessage(this.getClass(), key, emptyParams, false);
            if (value != null) {
                return value;
            }
        }

        // No match, return default
        return defaultValue;
    }

    private void renderTextBox(TextBoxField inputField, String placeholderText, IModelFactory modelFactory, IModel model) {
        Map<String, String> attrs = new LinkedHashMap<>();
        attrs.put("placeholder", HtmlEscape.escapeHtml5(placeholderText));
        attrs.put("th:field", "*{" + inputField.getName() + "}");
        attrs.put("class", "form-control");
        model.add(modelFactory.createOpenElementTag("textarea", attrs, AttributeValueQuotes.DOUBLE, false));
        model.add(modelFactory.createCloseElementTag("textarea"));
    }

    private void renderTextField(InputField inputField, String placeholderText, IModelFactory modelFactory, IModel model) {
        Map<String, String> attrs = new LinkedHashMap<>();
        String typeAttr = htmlInputType(inputField);
        attrs.put("type", typeAttr);
        attrs.put("class", typeAttr.equals("range") ? " custom-range" : "form-control");
        attrs.put("id", inputField.getName());
        if (inputField instanceof NumberField) {
            NumberField numberField = (NumberField) inputField;
            attrs.put("min", numberField.getMin() != null ? numberField.getMin().toString() : null);
            attrs.put("max", numberField.getMax() != null ? numberField.getMax().toString() : null);
        }

        attrs.put("placeholder", HtmlEscape.escapeHtml5(placeholderText));
        attrs.put("th:field", "*{" + inputField.getName() + "}");

        model.add(modelFactory.createStandaloneElementTag("input", attrs, AttributeValueQuotes.DOUBLE, false, false));
    }

    private void renderChoiceField(ChoiceField choiceField, String placeholderText, IModelFactory modelFactory, IModel model) {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("th:field", "*{" + choiceField.getName() + "}");
        attrs.put("class", "custom-select");
        model.add(modelFactory.createOpenElementTag("select", attrs, AttributeValueQuotes.DOUBLE, false));

        // If options are supplied, use them
        // If it is boolean or Boolean or an enum, then generate options
        for (String option : new String[] { "true", "false" }) {
            model.add(modelFactory.createOpenElementTag("option", "value", option));
            model.add(modelFactory.createText(option));
            model.add(modelFactory.createCloseElementTag("option"));
        }

        model.add(modelFactory.createCloseElementTag("select"));
    }

    private String htmlInputType(InputField inputField) {
        if (inputField instanceof PasswordField) {
            return "password";
        }
        else if (inputField instanceof NumberField) {
            NumberField numberField = (NumberField) inputField;
            if (numberField.isUseSlider() && numberField.getMin() != null && numberField.getMax() != null) {
                return "range";
            }
            return "number";
        }
        else if (inputField instanceof DateField) {
            return "date";
        }
        return "text"; // ???
    }
}
