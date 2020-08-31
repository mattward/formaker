package uk.wardm.formaker.thymeleaf;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.AttributeValueQuotes;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.unbescape.html.HtmlEscape;
import uk.wardm.formaker.TextBox;
import uk.wardm.formaker.model.Component;
import uk.wardm.formaker.model.input.*;

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
            // Do same for this as for placeholders
            String labelText = context.getMessage(this.getClass(), inputField.getLabel(), new Object[]{}, true);
            model.add(modelFactory.createText(HtmlEscape.escapeHtml5(labelText)));
            model.add(modelFactory.createCloseElementTag("label"));

            // The placeholder key(s) should be in the model
            // Nothing should be computed here - so it can be rendered by any renderer (e.g. json) and produce the same results
            String fqPlaceholderKey = inputField.getLabel() + ".placeholder";
            String placeholderText = context.getMessage(this.getClass(), fqPlaceholderKey, new Object[]{}, false);
            if (placeholderText == null) {
                String genericPlaceholderKey = "fm.fields." + inputField.getName() + ".placeholder";
                placeholderText = context.getMessage(this.getClass(), genericPlaceholderKey, new Object[]{}, true);
            }

            if (inputField instanceof TextBoxField) {
                renderTextBox(inputField, placeholderText, modelFactory, model);
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

    private void renderTextBox(InputField inputField, String placeholderText, IModelFactory modelFactory, IModel model) {
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
